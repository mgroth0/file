package matt.file.equality

import matt.file.JioFile
import matt.file.commons.DS_STORE
import matt.file.hash.md5
import matt.file.hash.recursiveMD5
import matt.file.toJioFile
import matt.lang.anno.SeeURL
import matt.lang.model.file.FsFile
import matt.lang.model.file.FsFileNameImpl
import matt.lang.model.file.types.Folder
import matt.lang.model.file.types.asFolder

infix fun JioFile.hasIdenticalDataToUsingHash(other: JioFile): Boolean {
    return md5() == other.md5()
}

@SeeURL("https://stackoverflow.com/a/22819255/6596010")
infix fun JioFile.hasIdenticalDataTo(other: JioFile): Boolean {
    if (doesNotExist) error("$this does not exist")
    if (other.doesNotExist) error("$other does not exist")
    if (size() != other.size()) {
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

fun JioFile.isRecursivelyIdenticalToUsingHash(
    other: JioFile,
    ignoreDSStore: Boolean = true,
    ignoreFileNames: List<String> = listOf()
): Boolean {
    return recursiveMD5(ignoreDSStore = ignoreDSStore, ignoreFileNames = ignoreFileNames) == other.recursiveMD5(
        ignoreDSStore = ignoreDSStore,
        ignoreFileNames = ignoreFileNames
    )
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

    fun predicate(file: FsFile) = (!ignoreDSStore || !file.hasName(DS_STORE)) && file.fsFileName !in (ignoreFileNames.map {
        FsFileNameImpl(it,fileSystem)
    })

    val files = this.toJioFile().listFiles()!!.filter(::predicate)
    val otherFiles = other.toJioFile().listFiles()!!.filter(::predicate)

    if (files.size != otherFiles.size) return "${this.path}: size ${files.size} is different from ${otherFiles.size}"


    files.forEach { file ->
        check(file.isAbsolute)
        val otherFile =
            otherFiles.firstOrNull { it.name == file.name } ?: return "${this.path}: otherFiles has no ${file.name}"
        check(otherFile.isAbsolute)
        if (file.isDir()) {
            if (!otherFile.isDir()) return "${this.path}: $otherFile is not a dir"
            val rResult = file.asFolder().firstRecursiveDiff(
                otherFile.asFolder(),
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