package matt.file.numbered

import matt.file.MFile
import matt.file.construct.mFile
import matt.lang.model.file.FilePath

fun FilePath.next(): MFile {
    var ii = 0
    while (true) {
        val f = mFile(filePath + ii.toString())
        if (!f.exists()) {
            return f
        }
        ii += 1
    }
}

fun FilePath.withNumber(num: Int): MFile {
    return if ("." !in fName) mFile(
        "$filePath ($num)"
    )
    else mFile(
        filePath.substringBeforeLast(".") + " ($num)." + filePath.substringAfterLast(
            "."
        )
    )
}


fun MFile.numberedSequence() = sequence {
    yield(this@numberedSequence)
    var i = 2
    while (true) {
        yield(this@numberedSequence.withNumber(i++))
    }
}

fun MFile.firstNonExistingFromNumberedSequence() = numberedSequence().first { it.doesNotExist }