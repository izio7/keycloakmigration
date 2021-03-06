package de.klg71.keycloakmigration.changeControl.actions.role

import de.klg71.keycloakmigration.changeControl.actions.Action
import de.klg71.keycloakmigration.changeControl.actions.MigrationException
import de.klg71.keycloakmigration.model.AddRole
import de.klg71.keycloakmigration.model.Role
import de.klg71.keycloakmigration.rest.clientRoleByName
import de.klg71.keycloakmigration.rest.clientUUID
import de.klg71.keycloakmigration.rest.roleExistsByName

class DeleteRoleAction(
        realm: String? = null,
        private val name: String,
        private val clientId: String? = null) : Action(realm) {

    private lateinit var deletedRole: Role

    private fun addRole() = AddRole(name, deletedRole.description)
    private fun updateRole() = Role(deletedRole.id, deletedRole.name, deletedRole.description,
            deletedRole.composite,
            deletedRole.clientRole,
            deletedRole.containerId,
            deletedRole.attributes)


    override fun execute() {
        if (clientId != null) {
            if (!client.roleExistsByName(name, realm(), clientId)) {
                throw MigrationException("Role with name: $name does not exist in realm: ${realm()}!")
            }
        } else {
            if (!client.roleExistsByName(name, realm())) {
                throw MigrationException("Role with name: $name does not exist in realm: ${realm()}!")
            }
        }
        findRole().run {
            deletedRole = this
            client.deleteRole(id, realm())
        }
    }

    override fun undo() {
        if (clientId == null) {
            client.addRole(addRole(), realm())
        } else {
            client.addClientRole(addRole(), client.clientUUID(clientId, realm()), realm())
        }
        findRole().run {
            client.updateRole(updateRole(), id, realm())
        }
    }

    private fun findRole() = if (clientId == null) {
        client.roleByName(name, realm())
    } else {
        client.clientRoleByName(name, clientId, realm())
    }

    override fun name() = "DeleteRole $name"

}
