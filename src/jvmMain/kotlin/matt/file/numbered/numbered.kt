package matt.file.numbered

import matt.file.MFile
import matt.file.ext.FileExtension
import matt.prim.str.isInt

/*TODO: MERGE THIS CODE WITH nextFileThatDoesNotExistWithNameLike and COMMON CODE*/
class NumberedFiles(
    private val folder: MFile,
    val prefix: String,
    val extension: FileExtension
) {

    private fun currentValidFiles() = folder.listFiles()?.filter {
        it.name.startsWith(prefix) && it.name.endsWith(extension.withPrefixDot) && it.name.substringAfter(prefix)
            .substringBefore(extension.withPrefixDot).isInt()
    } ?: listOf()

    fun currentMaxNumber() = currentValidFiles().map {
        it.name.substringAfter(prefix).substringBefore(extension.withPrefixDot).toInt()
    }.sorted().maxOrNull()

    fun currentMaxFile() = currentMaxNumber()?.let { numberToFile(it) }
    fun nextFile() = numberToFile((currentMaxNumber() ?: -1) + 1)

    fun numberToFile(num: Int) = folder["$prefix$num${extension.withPrefixDot}"]

}

