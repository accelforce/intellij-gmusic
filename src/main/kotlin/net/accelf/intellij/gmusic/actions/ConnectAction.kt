package net.accelf.intellij.gmusic.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import net.accelf.intellij.gmusic.commands.ConnectCommand
import net.accelf.intellij.gmusic.services.AppService

class ConnectAction : AnAction("Connect to GPMDP") {

    override fun actionPerformed(e: AnActionEvent) {
        val appService = ServiceManager.getService(AppService::class.java)
        val gpmdp = appService.gpmdp

        gpmdp.send(ConnectCommand())
    }
}
