package net.accelf.intellij.gmusic.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import io.reactivex.subjects.PublishSubject
import java.util.*

@State(
    name = "net.accelf.intellij.gmusic.services.SettingsState",
    storages = [Storage("GMusicPlugin.xml")],
)
class SettingsState : PersistentStateComponent<SettingsState> {

    var address: String = ""
    var wslEnabled: Boolean = false

    val reconnectEvent: PublishSubject<Any> = PublishSubject.create()

    override fun getState(): SettingsState? {
        return this
    }

    override fun loadState(state: SettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        return address == (other as SettingsState).address &&
            wslEnabled == other.wslEnabled
    }

    override fun hashCode(): Int {
        return Objects.hash(
            address,
            wslEnabled,
        )
    }

    companion object {
        fun getInstance(): SettingsState {
            return ServiceManager.getService(SettingsState::class.java)
        }
    }
}
