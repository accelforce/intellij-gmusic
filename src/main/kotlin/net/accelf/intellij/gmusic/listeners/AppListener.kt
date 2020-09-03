package net.accelf.intellij.gmusic.listeners

import com.intellij.ide.AppLifecycleListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.ShutdownReason
import com.tinder.scarlet.WebSocket
import net.accelf.intellij.gmusic.actions.ConnectAction
import net.accelf.intellij.gmusic.commands.ConnectCommand
import net.accelf.intellij.gmusic.responses.ApiVersionResponse
import net.accelf.intellij.gmusic.responses.ConnectResponse
import net.accelf.intellij.gmusic.responses.TrackResponse
import net.accelf.intellij.gmusic.services.AppService
import net.accelf.intellij.gmusic.utils.debug
import net.accelf.intellij.gmusic.utils.notify

internal class AppListener : AppLifecycleListener {

    override fun appStarting(projectFromCommandLine: Project?) {
        val appService = ServiceManager.getService(AppService::class.java)
        val lifecycleRegistry = appService.lifecycleRegistry
        val gpmdp = appService.gpmdp
        val notificationGroup = appService.notificationGroup

        gpmdp.observeWebSocketEvent()
            .subscribe {
                when (it) {
                    is WebSocket.Event.OnConnectionOpened<*> -> {
                        notificationGroup.debug("Connected to GPMDP")

                        if (appService.code == null) {
                            notificationGroup.notify("Please set authorization code", action = ConnectAction())
                            return@subscribe
                        }

                        gpmdp.send(ConnectCommand(appService.code))
                    }
                    is WebSocket.Event.OnConnectionClosed -> {
                        notificationGroup.notify("Disconnected from GPMDP", NotificationType.WARNING)
                    }
                    is WebSocket.Event.OnConnectionFailed -> {
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
                                    appService.updateWidget()
                                }
                            } else {
                                notificationGroup.notify("Authorization verified")

                                appService.code = it.code
                                gpmdp.send(ConnectCommand(appService.code))
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
                            appService.track = it.track
                            appService.updateWidget()
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
}
