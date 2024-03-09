package matt.file.equality

import matt.file.JioFile
import matt.file.commons.fnames.DS_STORE
import matt.file.hash.common.md5
import matt.file.hash.j.recursiveMD5
import matt.file.toJioFile
import matt.lang.anno.SeeURL
import matt.lang.anno.optin.IncubatingMattCode
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.FsFileNameImpl
import matt.lang.model.file.types.AnyFolder
import matt.lang.model.file.types.asFolder
import java.util.jar.JarFile

infix fun JioFile.hasIdenticalDataToUsingHash(other: JioFile): Boolean = md5() == other.md5()

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
): Boolean =
    recursiveMD5(ignoreDSStore = ignoreDSStore, ignoreFileNames = ignoreFileNames) ==
        other.recursiveMD5(
            ignoreDSStore = ignoreDSStore,
            ignoreFileNames = ignoreFileNames
        )

private const val DEFAULT_IGNORE_DS_STORE = true

fun AnyFolder.isRecursivelyIdenticalTo(
    other: AnyFolder,
    ignoreDSStore: Boolean = DEFAULT_IGNORE_DS_STORE
): Boolean =
    firstRecursiveDiff(
        other,
        ignoreDSStore
    ) != null

fun AnyFolder.firstRecursiveDiff(
    other: AnyFolder,
    ignoreDSStore: Boolean = DEFAULT_IGNORE_DS_STORE,
    ignoreFileNames: List<String> = listOf()
): String? {

    fun predicate(file: AnyFsFile) =
        (!ignoreDSStore || !file.hasName(DS_STORE)) && file.fsFileName !in (
            ignoreFileNames.map {
                FsFileNameImpl(it, myFileSystem)
            }
        )

    val files = toJioFile().listFiles()!!.filter(::predicate)
    val otherFiles = other.toJioFile().listFiles()!!.filter(::predicate)

    if (files.size != otherFiles.size) return "$path: size ${files.size} is different from ${otherFiles.size}"


    files.forEach { file ->
        check(file.isAbs)
        val otherFile =
            otherFiles.firstOrNull { it.name == file.name } ?: return "$path: otherFiles has no ${file.name}"
        check(otherFile.isAbs)
        if (file.isDir()) {
            if (!otherFile.isDir()) return "$path: $otherFile is not a dir"
            val rResult =
                file.asFolder().firstRecursiveDiff(
                    otherFile.asFolder(),
                    ignoreDSStore = ignoreDSStore,
                    ignoreFileNames = ignoreFileNames
                )
            if (rResult != null) {
                return rResult
            }
        } else {
            if (otherFile.isDir()) return "$path: $otherFile is a dir"
            if (!file.hasIdenticalDataTo(otherFile)) return "$path: $otherFile has different data"
        }
    }




    return null
}


@IncubatingMattCode
fun JarFile.firstRecursiveDifference(other: JarFile): String? {
    val mySize = size()
    val otherSize = other.size()
    if (mySize != otherSize) {
        return "number of entries is different between $mySize, $otherSize"
    }
    val myEntries = entries()
    val otherEntries = other.entries()
    while (true) {
        if (!myEntries.hasMoreElements()) break
        if (!otherEntries.hasMoreElements()) break
        val myEntry = myEntries.nextElement()
        val otherEntry = otherEntries.nextElement()
        if (myEntry.name != otherEntry.name) {
            return "names are different between ${myEntry.name},${otherEntry.name}"
        }
        if (myEntry.size != otherEntry.size) {
            return "sizes of ${myEntry.name} are different between ${myEntry.size},${otherEntry.size}"
        }
        if (myEntry.isDirectory != otherEntry.isDirectory) {
            return "isDirectory of ${myEntry.name} are different between ${myEntry.isDirectory},${otherEntry.isDirectory}"
        }
        if (myEntry.comment != otherEntry.comment) {
            return "comment of ${myEntry.name} is different between ${myEntry.comment},${otherEntry.comment}"
        }
        if (myEntry.crc != otherEntry.crc) {
            return "crc of ${myEntry.name} is different between ${myEntry.crc},${otherEntry.crc}"
        }
    }
    return null
}
