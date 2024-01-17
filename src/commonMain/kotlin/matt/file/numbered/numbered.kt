package matt.file.numbered

import matt.file.construct.mFile
import matt.file.toIoFile
import matt.lang.anno.MergeWith
import matt.lang.model.file.FsFile
import matt.lang.model.file.fName

@MergeWith(id = 34206385)
fun FsFile.next(): FsFile {
    var ii = 0
    while (true) {
        val f = mFile(path + ii.toString(),fileSystem)
        if (!f.toIoFile().exists()) {
            return f
        }
        ii += 1
    }
}

@MergeWith(id = 34206385)
fun FsFile.withNumber(num: Int): FsFile {
    return if ("." !in fName) mFile(
        "$filePath ($num)",
        fileSystem
    )
    else mFile(
        filePath.substringBeforeLast(".") + " ($num)." + filePath.substringAfterLast(
            "."
        ),
        fileSystem
    )
}

@MergeWith(id = 34206385)
fun FsFile.numberedSequence() = sequence {
    yield(this@numberedSequence)
    var i = 2
    while (true) {
        yield(this@numberedSequence.withNumber(i++))
    }
}

@MergeWith(id = 34206385)
fun FsFile.firstNonExistingFromNumberedSequence() = numberedSequence().first { it.toIoFile().doesNotExist }