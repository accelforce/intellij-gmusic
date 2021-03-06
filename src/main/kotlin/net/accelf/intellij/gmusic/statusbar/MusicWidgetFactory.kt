package net.accelf.intellij.gmusic.statusbar

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class MusicWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = MusicWidget::class.java.name

    override fun getDisplayName() = "Google Play Music"

    override fun isAvailable(project: Project) = true

    override fun createWidget(project: Project) = MusicWidget()

    override fun disposeWidget(widget: StatusBarWidget) {
        Disposer.dispose(widget)
    }

    override fun canBeEnabledOn(statusBar: StatusBar) = true
}
