package matt.file.sync

import matt.file.ext.backup
import matt.file.toJioFile
import matt.lang.model.file.FsFile

class SynchronizedFileManager(
    private val file: FsFile
) {

    var scheduledFinalBackup: Boolean = false
        private set

    @Synchronized
    fun backup(ensureFinal: Boolean = false) {
        check(!scheduledFinalBackup)
        if (ensureFinal) scheduledFinalBackup = true
        file.toJioFile().backup(thread = false)
    }

    @Synchronized
    fun read() = file.toJioFile().text

    var didFinalWrite: Boolean = false
        private set

    @Synchronized
    fun write(
        text: String,
        ensureFinal: Boolean = false
    ) {
        check(!didFinalWrite)
        if (ensureFinal) didFinalWrite = true
        file.toJioFile().write(text)
    }

}