package matt.file.ext.backup

import matt.file.JvmMFile
import matt.file.commons.reg.BACKUP_FOLDER
import matt.file.ext.j.mkparents
import matt.file.ext.weird.getNextSubIndexedFile
import matt.file.toJioFile
import matt.lang.file.toJFile
import matt.lang.model.file.AnyFsFile

val AnyFsFile.defaultBackupFolder get() = parent!! + "backups"
val AnyFsFile.registeredBackupFolder get() = BACKUP_FOLDER["by_path"][path.removePrefix(myFileSystem.separator)]

fun JvmMFile.backup(
    text: String? = null,
    backupFolder: AnyFsFile = defaultBackupFolder
) {
    doBackupWork(text = text, backupFolder = backupFolder)
}

fun JvmMFile.doubleBackupWrite(
    s: String
) {

    mkparents()
    createNewFile()

    /*this is important. Extra security is always good.

    now I'm backing up version before AND after the change.

    yes, there is redundancy. In some contexts redundancy is good. Safe.

    Obviously this is a reaction to a mistake I made (that turned out ok in the end, but scared me a lot).*/

    val old = readText()

    doBackupWork(text = old, backupFolder = defaultBackupFolder)
    writeText(s)
    doBackupWork(text = old, backupFolder = defaultBackupFolder)
}


private fun JvmMFile.doBackupWork(
    text: String? = null,
    backupFolder: AnyFsFile
) {
    val backFolderJio = backupFolder.toJioFile()
    backFolderJio.mkdirs()
    require(backFolderJio.isDir()) { "backupFolder not a dir" }
    val backupTarget = backFolderJio.getNextSubIndexedFile(name, 100)
    if (isDir()) {
        backupTarget.deleteIfExists()
        toJFile().copyRecursively(backupTarget.toJFile())
        return
    }

    val realText = text ?: readText()

    backupTarget.text = realText
}
