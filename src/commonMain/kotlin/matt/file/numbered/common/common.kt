package matt.file.numbered.common



import matt.file.construct.common.mFile
import matt.file.expects.file.toIoFile
import matt.lang.anno.MergeWith
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.fName

@MergeWith(id = 34206385)
fun AnyFsFile.next(): AnyFsFile {
    var ii = 0
    while (true) {
        val f = mFile(path + ii.toString(), myFileSystem)
        if (!f.toIoFile().exists()) return f
        ii += 1
    }
}

@MergeWith(id = 34206385)
fun AnyFsFile.withNumber(num: Int): AnyFsFile =
    if ("." !in fName) mFile(
        "$path ($num)",
        myFileSystem
    )
    else mFile(
        path.substringBeforeLast(".") + " ($num)." +
            path.substringAfterLast(
                "."
            ),
        myFileSystem
    )

@MergeWith(id = 34206385)
fun AnyFsFile.numberedSequence() =
    sequence {
        yield(this@numberedSequence)
        var i = 2
        while (true) {
            yield(this@numberedSequence.withNumber(i++))
        }
    }

@MergeWith(id = 34206385)
fun AnyFsFile.firstNonExistingFromNumberedSequence() =
    numberedSequence()
        .first {
            it.toIoFile().doesNotExist
        }
