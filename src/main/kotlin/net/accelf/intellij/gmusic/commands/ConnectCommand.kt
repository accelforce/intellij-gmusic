package net.accelf.intellij.gmusic.commands

class ConnectCommand(code: String? = null) : Command(
    "connect",
    "connect",
    if (code == null) listOf(APP_NAME) else listOf(APP_NAME, code),
) {

    companion object {
        private const val APP_NAME = "intellij-gmusic"
    }
}
