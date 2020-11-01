package net.accelf.intellij.gmusic.settings

import net.accelf.intellij.gmusic.services.SettingsState
import javax.swing.JComponent

class SettingsComponent {

    val mainPanel: MainSettingsPanel = MainSettingsPanel()

    fun getPreferredFocusedComponent(): JComponent {
        return mainPanel.addressTextField
    }

    fun fromSettingsState(state: SettingsState) {
        mainPanel.addressTextField.text = state.address
        mainPanel.wslCheckBox.isSelected = state.wslEnabled
    }

    fun toSettingsState(): SettingsState {
        return SettingsState()
            .apply {
                address = mainPanel.addressTextField.text
                wslEnabled = mainPanel.wslCheckBox.isSelected
            }
    }
}
