package net.accelf.intellij.gmusic.utils

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import java.lang.management.ManagementFactory

private val debug by lazy {
    ManagementFactory.getRuntimeMXBean().inputArguments.toString().indexOf("jdwp") >= 0
}

fun NotificationGroup.notify(
    text: String,
    type: NotificationType = NotificationType.INFORMATION,
    action: AnAction? = null,
) {
    val notification = createNotification(text, type)

    action?.let {
        notification.addAction(action)
    }

    Notifications.Bus.notify(notification)
}

fun NotificationGroup.debug(text: String, type: NotificationType = NotificationType.INFORMATION) {
    if (debug) {
        notify(text, type)
    }
}
