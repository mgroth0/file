package matt.file.equality

import matt.file.Folder
import matt.file.MFile
import matt.file.commons.DS_STORE
import matt.lang.anno.SeeURL

@SeeURL("https://stackoverflow.com/questions/22818590/java-how-to-check-that-2-binary-files-are-same")
infix fun MFile.hasIdenticalDataToUsingHash(@Suppress("UNUSED_PARAMETER") other: MFile): Boolean {
    throw NotImplementedError("I could compute a hash and use that check file equality, but I don't think it is necessary for my purposes yet")
}

@SeeURL("https://stackoverflow.com/a/22819255/6596010")
infix fun MFile.hasIdenticalDataTo(other: MFile): Boolean {
    if (doesNotExist) error("$this does not exist")
    if (other.doesNotExist) error("$other does not exist")
    if (length() != other.length()) {
        return false
    }

    val in1 = inputStream().buffered()
    val in2 = other.inputStream().buffered()

    do {
        val val1 = in1.read()
        val val2 = in2.read()
        if (val1 != val2) {
            return false
        }
    } while (val1 >= 0)

    return true

}

fun Folder.isRecursivelyIdenticalTo(
    other: Folder,
    ignoreDSStore: Boolean = true
): Boolean = firstRecursiveDiff(other,ignoreDSStore) != null

fun Folder.firstRecursiveDiff(
    other: Folder,
    ignoreDSStore: Boolean = true
): String? {
//    if (name != other.name) return "name $name is different from ${other.name}"

    fun predicate(file: MFile) = !ignoreDSStore || file.name != DS_STORE

    val files = this.listFiles()!!.filter(::predicate)
    val otherFiles = other.listFiles()!!.filter(::predicate)

    if (files.size != otherFiles.size) return "${this.path}: size ${files.size} is different from ${otherFiles.size}"


    files.forEach { file ->
        val otherFile = otherFiles.firstOrNull { it.name == file.name } ?: return "${this.path}: otherFiles has no ${file.name}"
        if (file.isDir()) {
            if (!otherFile.isDir()) return "${this.path}: $otherFile is not a dir"
            val rResult = Folder(file.absolutePath).firstRecursiveDiff(
                Folder(otherFile.absolutePath),
                ignoreDSStore = ignoreDSStore
            )
            if (rResult!=null) {
                return rResult
            }
        } else {
            if (otherFile.isDir()) return "${this.path}: $otherFile is a dir"
            if (!file.hasIdenticalDataTo(otherFile)) return "${this.path}: $otherFile has different data"
        }
    }




    return null
}