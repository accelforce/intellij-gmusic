package net.accelf.intellij.gmusic.listeners

import com.intellij.ide.AppLifecycleListener
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import net.accelf.intellij.gmusic.services.AppService
import net.accelf.intellij.gmusic.services.SettingsState

internal class AppListener : AppLifecycleListener {

    override fun appStarting(projectFromCommandLine: Project?) {
        val appService = ServiceManager.getService(AppService::class.java)
        val settingsState = SettingsState.getInstance()

        appService.setWsClient(settingsState)

        settingsState.reconnectEvent
            .subscribe {
                appService.setWsClient(settingsState)
            }
    }
}
