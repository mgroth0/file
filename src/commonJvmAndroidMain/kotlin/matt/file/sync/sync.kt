package matt.file.sync

import matt.file.MFile

class SynchronizedFileManager(
    private val file: MFile
) {

    var scheduledFinalBackup: Boolean = false
        private set

    @Synchronized
    fun backup(ensureFinal: Boolean = false) {
        check(!scheduledFinalBackup)
        if (ensureFinal) scheduledFinalBackup = true
        file.backup(thread = false)
    }

    @Synchronized
    fun read() = file.text

    var didFinalWrite: Boolean = false
        private set

    @Synchronized
    fun write(
        text: String,
        ensureFinal: Boolean = false
    ) {
        check(!didFinalWrite)
        if (ensureFinal) didFinalWrite = true
        file.write(text)
    }

}