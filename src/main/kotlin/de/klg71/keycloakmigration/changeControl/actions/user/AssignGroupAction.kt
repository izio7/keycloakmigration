package de.klg71.keycloakmigration.changeControl.actions.user

import de.klg71.keycloakmigration.changeControl.actions.Action
import de.klg71.keycloakmigration.changeControl.actions.MigrationException
import de.klg71.keycloakmigration.model.AssignGroup
import de.klg71.keycloakmigration.model.Group
import de.klg71.keycloakmigration.rest.existsGroup
import de.klg71.keycloakmigration.rest.existsUser
import de.klg71.keycloakmigration.rest.groupByName
import de.klg71.keycloakmigration.rest.groupUUID
import de.klg71.keycloakmigration.rest.userUUID

class AssignGroupAction(
        realm: String? = null,
        private val user: String,
        private val group: String) : Action(realm) {

    override fun execute() {
        if (!client.existsUser(user, realm())) {
            throw MigrationException("User with name: $user does not exist in realm: ${realm()}!")
        }
        if (!client.existsGroup(group, realm())) {
            throw MigrationException("Group with name: $group does not exist in realm: ${realm()}!")
        }

        findGroup().run {
            assignGroup()
        }.let {
            client.assignGroup(it, realm(), client.userUUID(user, realm()), client.groupUUID(group, realm()))
        }
    }

    private fun Group.assignGroup() = AssignGroup(realm(), id, client.userUUID(user, realm()))

    override fun undo() {
        client.revokeGroup(realm(), client.userUUID(user, realm()), client.groupUUID(group, realm()))
    }

    private fun findGroup() = client.groupByName(group, realm())

    override fun name() = "AssignGroup $group to $user"
}
