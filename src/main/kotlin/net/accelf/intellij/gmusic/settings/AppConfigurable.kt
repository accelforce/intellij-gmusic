package net.accelf.intellij.gmusic.settings

import com.intellij.openapi.options.Configurable
import net.accelf.intellij.gmusic.services.SettingsState
import javax.swing.JComponent

class AppConfigurable : Configurable {

    private lateinit var component: SettingsComponent

    override fun getDisplayName() = "Google Play Music"

    override fun createComponent(): JComponent? {
        component = SettingsComponent()
        return component.mainPanel.rootPanel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return component.getPreferredFocusedComponent()
    }

    override fun isModified(): Boolean {
        val state = SettingsState.getInstance()
        return state != component.toSettingsState()
    }

    override fun apply() {
        val state = SettingsState.getInstance()
        state.loadState(component.toSettingsState())
        state.reconnectEvent.onNext(0)
    }

    override fun reset() {
        val state = SettingsState.getInstance()
        component.fromSettingsState(state)
    }
}
