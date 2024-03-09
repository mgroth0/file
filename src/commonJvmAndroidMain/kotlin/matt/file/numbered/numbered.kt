package matt.file.numbered

import matt.file.expects.file.toIoFile
import matt.file.ext.FileExtension
import matt.lang.anno.MergeWith
import matt.lang.common.substringBeforeSingular
import matt.lang.model.file.fName
import matt.lang.model.file.types.AnyFolder
import matt.prim.str.isInt

@MergeWith(id = 34206385)
class NumberedFiles(
    private val folder: AnyFolder,
    val extension: FileExtension
) {

    private fun currentValidFiles() =
        folder
            .toIoFile()
            .listFilesAsList()?.filter {
                it
                    .fName
                    .endsWith(extension.withPrefixDot) &&
                    it
                        .fName
                        .substringBeforeSingular(extension.withPrefixDot)
                        .isInt()
            } ?: listOf()

    fun currentMaxNumber() =
        currentValidFiles()
            .map {
                it
                    .fName
                    .substringBeforeSingular(
                        extension.withPrefixDot
                    ).toInt()
            }
            .sorted()
            .maxOrNull()

    fun currentMaxFile() = currentMaxNumber()?.let { numberToFile(it) }
    fun nextFile() = numberToFile((currentMaxNumber() ?: -1) + 1)
    fun numberToFile(num: Int) = folder["$num${extension.withPrefixDot}"]
}

