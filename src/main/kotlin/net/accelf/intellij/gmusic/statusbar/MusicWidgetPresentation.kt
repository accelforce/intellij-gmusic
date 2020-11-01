package net.accelf.intellij.gmusic.statusbar

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import net.accelf.intellij.gmusic.services.AppService
import java.awt.event.MouseEvent

class MusicWidgetPresentation : StatusBarWidget.TextPresentation {

    private val appService: AppService = ApplicationManager.getApplication().getService(AppService::class.java)

    override fun getTooltipText(): String? = null

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return Consumer {}
    }

    override fun getText(): String {
        return when {
            !appService.connected -> "not connected"
            appService.code == null -> "not authorized"
            appService.track == null -> "connected"
            else -> {
                appService.track!!.run {
                    "$title - $artist"
                }
            }
        }
    }

    override fun getAlignment() = 0f
}
