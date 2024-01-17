package matt.file.numbered

import matt.file.ext.FileExtension
import matt.file.toIoFile
import matt.lang.anno.MergeWith
import matt.lang.model.file.fName
import matt.lang.model.file.types.Folder
import matt.prim.str.isInt

@MergeWith(id = 34206385)
class NumberedFiles(
    private val folder: Folder,
    val prefix: String,
    val extension: FileExtension
) {

    private fun currentValidFiles() = folder.toIoFile().listFilesAsList()?.filter {
        it.fName.startsWith(prefix) && it.fName.endsWith(extension.withPrefixDot) && it.fName.substringAfter(prefix)
            .substringBefore(extension.withPrefixDot).isInt()
    } ?: listOf()

    fun currentMaxNumber() = currentValidFiles().map {
        it.fName.substringAfter(prefix).substringBefore(extension.withPrefixDot).toInt()
    }.sorted().maxOrNull()

    fun currentMaxFile() = currentMaxNumber()?.let { numberToFile(it) }
    fun nextFile() = numberToFile((currentMaxNumber() ?: -1) + 1)

    fun numberToFile(num: Int) = folder["$prefix$num${extension.withPrefixDot}"]

}

