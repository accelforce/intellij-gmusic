package net.accelf.intellij.gmusic.services

import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.openapi.wm.StatusBar
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.LifecycleRegistry
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import net.accelf.intellij.gmusic.network.GPMDP
import net.accelf.intellij.gmusic.responses.Track
import net.accelf.intellij.gmusic.statusbar.MusicWidget
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class AppService {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

    val lifecycleRegistry = LifecycleRegistry()

    private val scarlet = Scarlet.Builder()
        .webSocketFactory(okHttpClient.newWebSocketFactory("ws://localhost:5672"))
        .addMessageAdapterFactory(GsonMessageAdapter.Factory())
        .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
        .lifecycle(lifecycleRegistry)
        .build()

    val gpmdp = scarlet.create<GPMDP>()

    var connected: Boolean = false

    val notificationGroup = NotificationGroup("intellij-gmusic", NotificationDisplayType.BALLOON, true)

    private val propertiesComponent = PropertiesComponent.getInstance()

    var code = propertiesComponent.getValue("code")
        set(value) {
            field = value

            propertiesComponent.setValue("code", value)
        }

    private val statusBars = mutableSetOf<StatusBar>()

    fun addStatusBar(statusBar: StatusBar) {
        statusBars.add(statusBar)
    }

    fun removeStatusBar(statusBar: StatusBar) {
        statusBars.remove(statusBar)
    }

    fun updateWidget() {
        statusBars.forEach {
            it.updateWidget(MusicWidget::class.java.name)
        }
    }

    var track: Track? = null
}
