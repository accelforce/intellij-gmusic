package net.accelf.intellij.gmusic.services

import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.StatusBar
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.ShutdownReason
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import net.accelf.intellij.gmusic.actions.ConnectAction
import net.accelf.intellij.gmusic.commands.ConnectCommand
import net.accelf.intellij.gmusic.network.GPMDP
import net.accelf.intellij.gmusic.responses.ApiVersionResponse
import net.accelf.intellij.gmusic.responses.ConnectResponse
import net.accelf.intellij.gmusic.responses.Track
import net.accelf.intellij.gmusic.responses.TrackResponse
import net.accelf.intellij.gmusic.statusbar.MusicWidget
import net.accelf.intellij.gmusic.utils.debug
import net.accelf.intellij.gmusic.utils.notify
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.buffer
import okio.source
import java.io.File

class AppService {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

    private val gsonMessageAdapterFactory = GsonMessageAdapter.Factory()
    private val rxJava2StreamAdapterFactory = RxJava2StreamAdapterFactory()

    private var lifecycleRegistry = LifecycleRegistry()

    lateinit var gpmdp: GPMDP

    var connected: Boolean = false

    private val notificationGroup = NotificationGroup("intellij-gmusic", NotificationDisplayType.BALLOON, true)

    private val propertiesComponent = PropertiesComponent.getInstance()

    var code = propertiesComponent.getValue("code")
        set(value) {
            field = value

            propertiesComponent.setValue("code", value)
        }

    private val statusBars = mutableSetOf<StatusBar>()

    fun setWsClient(state: SettingsState) {
        val address = when {
            state.wslEnabled -> {
                File("/etc/resolv.conf").source().buffer().use { source ->
                    generateSequence { source.readUtf8Line() }
                        .first { it.startsWith("nameserver") }
                        .split(" ").last()
                } + ":5672"
            }
            state.address.isNotEmpty() -> {
                when {
                    ":" !in state.address -> "${state.address}:5672"
                    else -> state.address
                }
            }
            else -> "localhost:5672"
        }

        createWsClient(address)
    }

    private fun createWsClient(address: String) {
        lifecycleRegistry.onNext(Lifecycle.State.Stopped.WithReason(ShutdownReason.GRACEFUL))
        lifecycleRegistry = LifecycleRegistry()

        val scarlet = Scarlet.Builder()
            .addMessageAdapterFactory(gsonMessageAdapterFactory)
            .addStreamAdapterFactory(rxJava2StreamAdapterFactory)
            .lifecycle(lifecycleRegistry)
            .webSocketFactory(okHttpClient.newWebSocketFactory("ws://$address"))
            .build()

        gpmdp = scarlet.create()

        gpmdp.observeWebSocketEvent()
            .subscribe {
                when (it) {
                    is WebSocket.Event.OnConnectionOpened<*> -> {
                        connected = true
                        notificationGroup.debug("Connected to GPMDP")

                        if (code == null) {
                            notificationGroup.notify("Please set authorization code", action = ConnectAction())
                            return@subscribe
                        }

                        gpmdp.send(ConnectCommand(code))
                    }
                    is WebSocket.Event.OnConnectionClosed -> {
                        connected = false
                        notificationGroup.notify("Disconnected from GPMDP", NotificationType.WARNING)
                    }
                    is WebSocket.Event.OnConnectionFailed -> {
                        connected = false
                        notificationGroup.notify(
                            "Could not connect to GPMDP\n${it.throwable.message}",
                            NotificationType.ERROR
                        )
                        it.throwable.printStackTrace()

                        lifecycleRegistry.onNext(Lifecycle.State.Stopped.WithReason(ShutdownReason.GRACEFUL))
                    }
                    else -> {
                    }
                }
            }

        gpmdp.observe()
            .subscribe(
                {
                    notificationGroup.debug("${it.channel}: ${it.payload}")

                    when (it) {
                        is ConnectResponse -> {
                            if (it.codeRequired) {
                                ApplicationManager.getApplication().invokeLater {
                                    val inputCode = Messages.showInputDialog(
                                        "Input the code displayed in GPMDP",
                                        "Input authorization code",
                                        null
                                    ) ?: return@invokeLater
                                    gpmdp.send(ConnectCommand(inputCode))
                                    updateWidget()
                                }
                            } else {
                                notificationGroup.notify("Authorization verified")

                                code = it.code
                                gpmdp.send(ConnectCommand(code))
                            }
                        }
                        is ApiVersionResponse -> {
                            if (!it.supports) {
                                @Suppress("MaxLineLength")
                                notificationGroup.notify(
                                    "Looks like you are using unsupported version of GPMDP.\nAPI_VERSION = ${it.payload}",
                                    NotificationType.ERROR
                                )
                                lifecycleRegistry.onNext(Lifecycle.State.Stopped.WithReason(ShutdownReason.GRACEFUL))
                            }
                        }
                        is TrackResponse -> {
                            track = it.track
                            updateWidget()
                        }
                    }
                },
                {
                    notificationGroup.notify("${it.message}", NotificationType.ERROR)
                    it.printStackTrace()
                }
            )

        lifecycleRegistry.onNext(Lifecycle.State.Started)
    }

    fun addStatusBar(statusBar: StatusBar) {
        statusBars.add(statusBar)
    }

    fun removeStatusBar(statusBar: StatusBar) {
        statusBars.remove(statusBar)
    }

    private fun updateWidget() {
        statusBars.forEach {
            it.updateWidget(MusicWidget::class.java.name)
        }
    }

    var track: Track? = null
}
