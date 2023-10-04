package matt.file.sync

import matt.file.ext.backup.backup
import matt.file.ext.backup.defaultBackupFolder
import matt.file.toJioFile
import matt.lang.model.file.FsFile

class SynchronizedFileManager(
    private val file: FsFile,
    private val backupFolder: FsFile = file.defaultBackupFolder
) {

    var scheduledFinalBackup: Boolean = false
        private set

    @Synchronized
    fun backup(ensureFinal: Boolean = false) {
        check(!scheduledFinalBackup)
        if (ensureFinal) scheduledFinalBackup = true
        file.toJioFile().backup(backupFolder = backupFolder)
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