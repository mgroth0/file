package matt.file.equality

import matt.file.Folder
import matt.file.MFile
import matt.file.commons.DS_STORE
import matt.file.hash.md5
import matt.file.hash.recursiveMD5
import matt.lang.anno.SeeURL

infix fun MFile.hasIdenticalDataToUsingHash(other: MFile): Boolean {
    return md5() == other.md5()
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

fun MFile.isRecursivelyIdenticalToUsingHash(
    other: MFile,
    ignoreDSStore: Boolean = true,
    ignoreFileNames: List<String> = listOf()
): Boolean {
    return recursiveMD5(ignoreDSStore=ignoreDSStore,ignoreFileNames=ignoreFileNames) == other.recursiveMD5(ignoreDSStore=ignoreDSStore,ignoreFileNames=ignoreFileNames)
}

private const val DEFAULT_IGNORE_DS_STORE = true

fun Folder.isRecursivelyIdenticalTo(
    other: Folder,
    ignoreDSStore: Boolean = DEFAULT_IGNORE_DS_STORE
): Boolean = firstRecursiveDiff(
    other,
    ignoreDSStore
) != null

fun Folder.firstRecursiveDiff(
    other: Folder,
    ignoreDSStore: Boolean = DEFAULT_IGNORE_DS_STORE,
    ignoreFileNames: List<String> = listOf()
): String? {
//    if (name != other.name) return "name $name is different from ${other.name}"

    fun predicate(file: MFile) = (!ignoreDSStore || file.name != DS_STORE) && file.name !in ignoreFileNames

    val files = this.listFiles()!!.filter(::predicate)
    val otherFiles = other.listFiles()!!.filter(::predicate)

    if (files.size != otherFiles.size) return "${this.path}: size ${files.size} is different from ${otherFiles.size}"


    files.forEach { file ->
        val otherFile =
            otherFiles.firstOrNull { it.name == file.name } ?: return "${this.path}: otherFiles has no ${file.name}"
        if (file.isDir()) {
            if (!otherFile.isDir()) return "${this.path}: $otherFile is not a dir"
            val rResult = Folder(file.absolutePath).firstRecursiveDiff(
                Folder(otherFile.absolutePath),
                ignoreDSStore = ignoreDSStore,
                ignoreFileNames = ignoreFileNames
            )
            if (rResult != null) {
                return rResult
            }
        } else {
            if (otherFile.isDir()) return "${this.path}: $otherFile is a dir"
            if (!file.hasIdenticalDataTo(otherFile)) return "${this.path}: $otherFile has different data"
        }
    }




    return null
}