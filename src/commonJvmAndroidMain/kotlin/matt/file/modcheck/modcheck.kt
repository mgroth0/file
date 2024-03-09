package matt.file.modcheck

import matt.file.JioFile
import java.nio.file.attribute.FileTime


fun JioFile.markModification() = FileModChecker(this, lastModified())

class FileModChecker(
    val file: JioFile,
    val lastModification: FileTime
) {
    fun checkNotModified(errorMsg: String) {
        if (file.lastModified() != lastModification) {
            error(errorMsg)
        }
    }
}
