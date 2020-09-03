package net.accelf.intellij.gmusic.commands

@Suppress("unused")
open class Command(
    val namespace: String,
    val method: String,
    val arguments: List<String>,
)
