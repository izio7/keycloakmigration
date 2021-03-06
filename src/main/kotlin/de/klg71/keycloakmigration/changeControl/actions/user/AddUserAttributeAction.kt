package de.klg71.keycloakmigration.changeControl.actions.user

import de.klg71.keycloakmigration.changeControl.actions.Action
import de.klg71.keycloakmigration.changeControl.actions.MigrationException
import de.klg71.keycloakmigration.model.User
import de.klg71.keycloakmigration.rest.userByName

class AddUserAttributeAction(
        realm: String? = null,
        private val name: String,
        private val attributeName: String,
        private val attributeValues: List<String>,
        private val override: Boolean = false) : Action(realm) {

    private lateinit var user: User

    private fun updateUser() =
            userAttributes().toMutableMap().let {
                if (attributeName in it && !override) {
                    throw MigrationException("Attribute $attributeName is already present on user ${user.username}!")
                }
                it[attributeName] = attributeValues
                User(user.id, user.createdTimestamp,
                        user.username,
                        user.enabled,
                        user.emailVerified,
                        it,
                        user.notBefore,
                        user.totp,
                        user.access,
                        user.disableableCredentialTypes,
                        user.requiredActions,
                        user.email,
                        user.firstName,
                        user.lastName, null)
            }

    private fun userAttributes(): Map<String, List<String>> = user.attributes ?: emptyMap()

    override fun execute() {
        user = client.userByName(name, realm())
        client.updateUser(user.id, updateUser(), realm())
    }

    override fun undo() {
        client.updateUser(user.id, user, realm())
    }

    override fun name() = "AddUserAttribute $name"

}
