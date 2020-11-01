package net.accelf.intellij.gmusic.settings

import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JTextField

class MainSettingsPanel {

    lateinit var rootPanel: JPanel
    lateinit var addressTextField: JTextField
    lateinit var wslCheckBox: JCheckBox

    init {
        wslCheckBox.addChangeListener { syncFields() }
    }

    private fun syncFields() {
        addressTextField.isEnabled = !wslCheckBox.isSelected
        wslCheckBox.isEnabled = "microsoft" in System.getProperty("os.version")
    }
}
