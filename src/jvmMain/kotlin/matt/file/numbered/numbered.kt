package matt.file.numbered

import matt.file.MFile
import matt.file.construct.mFile
import matt.file.ext.FileExtension
import matt.prim.str.isInt

class NumberedFiles(
    private val folder: MFile, val prefix: String, val extension: FileExtension
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

fun MFile.next(): MFile {
    var ii = 0
    while (true) {
        val f = mFile(absolutePath + ii.toString())
        if (!f.exists()) {
            return f
        }
        ii += 1
    }
}

fun MFile.withNumber(num: Int): MFile {
    return if ("." !in name) {
        mFile(
            "$abspath ($num)"
        )
    } else {
        mFile(
            abspath.substringBeforeLast(".") + " ($num)." + abspath.substringAfterLast(
                "."
            )
        )
    }
}

fun MFile.numberedSequence() = sequence<MFile> {
    yield(this@numberedSequence)
    var i = 2
    while (true) {
        yield(this@numberedSequence.withNumber(i++))
    }
}

fun MFile.firstNonExistingFromNumberedSequence() = numberedSequence().first { it.doesNotExist }