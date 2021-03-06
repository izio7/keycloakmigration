package de.klg71.keycloakmigration.changeControl.actions.client

import de.klg71.keycloakmigration.changeControl.actions.Action
import de.klg71.keycloakmigration.rest.extractLocationUUID
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.*

class ImportClientAction(
        realm: String? = null,
        private val clientRepresentationJsonFilename: String,
        private val relativeToFile: Boolean = true) : Action(realm) {
    private lateinit var clientUuid: UUID

    private fun fileBufferedReader() =
            if (relativeToFile) {
                FileInputStream(Paths.get(path, clientRepresentationJsonFilename).toString()).bufferedReader()
            } else {
                FileInputStream(clientRepresentationJsonFilename).bufferedReader()
            }

    private fun readJsonContentWithWhitespace() = fileBufferedReader().use { it.readText() }


    override fun execute() {
        client.importClient(readJsonContentWithWhitespace(), realm()).run {
            clientUuid = extractLocationUUID()
        }
    }

    override fun undo() {
        client.deleteClient(clientUuid, realm())
    }

    override fun name() = "ImportClient $clientRepresentationJsonFilename"

}
