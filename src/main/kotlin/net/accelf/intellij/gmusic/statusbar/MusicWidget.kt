package net.accelf.intellij.gmusic.statusbar

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import net.accelf.intellij.gmusic.services.AppService

class MusicWidget : StatusBarWidget {

    private lateinit var statusBar: StatusBar

    private val appService by lazy {
        ApplicationManager.getApplication().getService(AppService::class.java)
    }

    override fun ID(): String = this::class.java.name

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        appService.addStatusBar(statusBar)

        statusBar.updateWidget(ID())
    }

    override fun dispose() {
        appService.removeStatusBar(statusBar)
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation? = MusicWidgetPresentation()
}
