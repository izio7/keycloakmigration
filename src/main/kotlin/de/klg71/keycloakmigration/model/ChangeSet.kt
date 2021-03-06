package de.klg71.keycloakmigration.model

import com.fasterxml.jackson.annotation.JsonIgnore
import de.klg71.keycloakmigration.changeControl.actions.Action

data class ChangeSet(val id: String,
                     val author: String,
                     val changes: List<Action>,
                     val realm: String? = null,
                     @JsonIgnore
                     var path: String = "") {

    lateinit var hash: String

    init {
        changes.forEach {
            it.path = path
            if (it.realm == null) {
                if (realm != null) {
                    it.realm = realm
                }
            }
        }
    }

    fun hash() = hash
}
