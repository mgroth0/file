package matt.file.modcheck

import matt.file.JioFile


fun JioFile.markModification() = FileModChecker(this, lastModified())

class FileModChecker(
    val file: JioFile,
    val lastModification: Long
) {
    fun checkNotModified(errorMsg: String) {
        if (file.lastModified() != lastModification) {
            error(errorMsg)
        }
    }
}
