@file:JvmName("ExtJvmAndroidKt")

package matt.file.ext

import matt.collect.itr.recurse.DEFAULT_INCLUDE_SELF
import matt.collect.itr.recurse.recurse
import matt.file.JioFile
import matt.file.JvmMFile
import matt.file.commons.DS_STORE
import matt.file.construct.mFile
import matt.file.construct.toMFile
import matt.file.toJioFile
import matt.file.types.requireIsExistingFolder
import matt.lang.NOT_IMPLEMENTED
import matt.lang.anno.EnforcedMin
import matt.lang.file.toJFile
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFile
import matt.lang.model.file.fName
import matt.lang.userHome
import matt.log.warn.warn
import matt.prim.str.ensureSuffix
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

context(FileSystem)
@Suppress("DEPRECATION")
fun createTempDir(
    prefix: String = "tmp",
    suffix: String? = null,
    directory: FsFile? = null
) =
    kotlin.io.createTempDir(prefix, suffix, directory?.toJFile()).toMFile()


context(FileSystem)
@Suppress("DEPRECATION")
fun createTempFile(
    prefix: String = "tmp",
    suffix: String? = null,
    directory: FsFile? = null
) =
    kotlin.io.createTempFile(prefix, suffix, directory?.toJFile()).toMFile()


/*fun matt.file.JioFile.copyRecursively(
  target: FsFile,
  overwrite: Boolean = false,
  onError: (MFile, IOException)->OnErrorAction = { _, exception -> throw exception }
): Boolean = userFile.copyRecursively(target.userFile, overwrite) { f, e ->
  onError(MFile(f), e)
}*/


fun String.writeToFile(
    f: FsFile,
    mkdirs: Boolean = true
) {
    if (mkdirs) {
        f.parent!!.toJioFile().mkdirs()
    }
    f.toJioFile().writeText(this)
}


fun <T : FsFile> Iterable<T>.filterHasExtension(ext: FileExtension) = filter { it.hasExtension(ext) }

fun <T : FsFile> Sequence<T>.filterHasExtension(ext: FileExtension) = filter { it.hasExtension(ext) }

//
//@Suppress("unused")
//fun FilePath.startsWithAny(
//    atLeastOne: FilePath,
//    vararg more: FilePath
//): Boolean {
//    if (startsWith(atLeastOne)) return true
//    more.forEach { if (startsWith(it)) return true }
//    return false
//}
//
//fun FilePath.startsWithAny(
//    atLeastOne: FilePath,
//    vararg more: FilePath
//): Boolean {
//    if (startsWith(atLeastOne.toPath())) return true
//    more.forEach { if (startsWith(it.toPath())) return true }
//    return false
//}

fun String.toPath(): Path = FileSystems.getDefault().getPath(this.trim())


@EnforcedMin
fun JvmMFile.hasAnyExtension(
    extension: FileExtension,
    vararg extensions: FileExtension
): Boolean {
    val ext = mExtension
    if (ext == extension) return true
    extensions.forEach {
        if (it == ext) return true
    }
    return false
}

fun JvmMFile.hasAnyExtension(
    extensions: ExtensionSet,
): Boolean {
    val ext = mExtension
    return extensions.any { it == ext }
}


var JvmMFile.writableForOwner: Boolean
    get() = NOT_IMPLEMENTED
    set(value) {
        val success = idFile.setWritable(value, true)
        if (!success) {
            warn("failure setting $this writable=$value for owner")
        }
    }

var JvmMFile.writableForEveryone: Boolean
    get() = NOT_IMPLEMENTED
    set(value) {
        val success = idFile.setWritable(value, false)
        if (!success) {
            warn("failure setting $this writable=$value for everyone")
        }
    }


fun FsFile.relativeToOrSelf(base: FsFile): FsFile =
    with(fileSystem) { toJFile().relativeToOrSelf(base.toJFile()).toMFile() }

fun FsFile.relativeToOrNull(base: FsFile): FsFile? =
    with(fileSystem) { toJFile().relativeToOrNull(base.toJFile())?.toMFile() }


fun JvmMFile.writeIfDifferent(s: String) {
    mkparents()
    if (doesNotExist || readText() != s) {
        write(s)
    }
}


fun JvmMFile.recursiveSize() =
    recurse { it.listFilesAsList() }.map { it.size() }.reduce { acc, byteSize -> acc + byteSize }

fun JvmMFile.clearIfTooBigThenAppendText(s: String) {
    if (size().kiB > 10) {
        write("cleared because over 10KB") /*got an out of memory error when limit was set as 100KB*/
    }
    append(s)

}


fun JvmMFile.recursiveLastModified(): Long {
    var greatest = 0L
    recurse { it.listFiles()?.toList() ?: listOf() }.forEach {
        greatest = listOf(greatest, it.toJFile().lastModified()).maxOrNull()!!
    }
    return greatest
}


fun JvmMFile.recursiveChildren(includeSelf: Boolean = DEFAULT_INCLUDE_SELF) =
    recurse(includeSelf = includeSelf) { it.listFiles()?.toList() ?: listOf() }

val JvmMFile.ensureAbsolute get() = apply { require(isAbsolute) { "$this is not absolute" } }
val JvmMFile.absolutePathEnforced: String get() = ensureAbsolute.path


operator fun JvmMFile.plus(item: Char): JvmMFile {
    return resolve(item.toString())
}




val JvmMFile.unixNlink get() = Files.getAttribute(this.toJFile().toPath(), "unix:nlink").toString().toInt()
val JvmMFile.hardLinkCount get() = unixNlink


fun FsFile.hasExtension(extension: FileExtension) = mExtension == extension


infix fun JvmMFile.withExtension(ext: FileExtension): JvmMFile {
    with(fileSystem) {
        if ("." !in this@withExtension.fName) mFile(this@withExtension.cpath + "." + ext)
        return when (this@withExtension.fName.substringAfterLast(".")) {
            ext.afterDot -> this@withExtension
            else         -> mFile(
                this@withExtension.cpath.substringBeforeLast(".") + ext.withPrefixDot
            )
        }
    }
}


fun JvmMFile.appendln(line: String) {
    append(line + "\n")
}

fun FsFile.resRepExt(newExt: FileExtension) = mFile(
    parentFile!!.cpath + JioFile.separator + toJFile().nameWithoutExtension + "." + newExt.afterDot,
    fileSystem = fileSystem
)


internal class IndexFolder(val f: FsFile) {
    val name = f.fName
    val index = name.toInt()
    operator fun plus(other: JvmMFile) = f[other]
    operator fun plus(other: String) = f + other
    fun next() = IndexFolder(f.parent!! + (index + 1).toString())
    fun previous() = IndexFolder(f.parent!! + (index - 1).toString())
}


/*val JvmMFile.abspath: String
    get() = toJFile().absolutePath*/

infix fun JvmMFile.withLastNameExtension(s: String) = mFile(abspath.removeSuffix(fileSystem.separator) + s, fileSystem)


fun JvmMFile.moveInto(
    newParent: JvmMFile,
    overwrite: Boolean = false
): JvmMFile {
    return (if (overwrite) Files.move(
        this.toJFile().toPath(), (newParent + this.name).toJFile().toPath(), StandardCopyOption.REPLACE_EXISTING
    )
    else Files.move(this.toJFile().toPath(), (newParent + this.name).toJFile().toPath())).toFile().toMFile(fileSystem)
}


/*calling this 'mkdir' like I used to could cause errors since it shares a name with the shell command*/
fun JvmMFile.mkFold(child: String) = resolve(child).apply {
    mkdir()
}.toJioFile().requireIsExistingFolder()

/*calling this 'mkdir' like I used to could cause errors since it shares a name with the shell command*/
fun JvmMFile.mkFold(int: Int) = mkFold(int.toString())

fun JvmMFile.isImage() = mExtension?.isImage == true


val JvmMFile.url get() = toJFile().toURI().toURL()


fun JvmMFile.createIfNecessary(defaultText: String? = null): Boolean {
    var r = false
    if (mkparents()) r = true
    if (createNewFile()) r = true
    if (defaultText != null && text.isBlank()) {
        text = defaultText
        r = true
    }
    return r
}

fun JvmMFile.listNonDSStoreFiles() = listFiles()?.filter { !it.hasName(DS_STORE) }
fun JvmMFile.listFilesOrEmpty() = listFiles() ?: arrayOf()

fun JvmMFile.wildcardChildrenPath() = path.ensureSuffix(matt.file.JioFile.separator) + "*"

fun JvmMFile.startsWith(other: JvmMFile): Boolean = idFile.startsWith(other.idFile)
fun JvmMFile.startsWith(other: String): Boolean = idFile.startsWith(identityGetter(other))
fun JvmMFile.endsWith(other: JvmMFile) = idFile.endsWith(other.idFile)
fun JvmMFile.endsWith(other: String): Boolean = idFile.endsWith(identityGetter(other))

fun JvmMFile.mkparents() = parent!!.mkdirs()
fun JvmMFile.tildeString() = toString().replace(userHome.removeSuffix(fileSystem.separator), "~")

fun JvmMFile.isBlank() = bufferedReader().run {
    val r = read() == -1
    close()
    r
}


fun JvmMFile.append(
    s: String,
    mkdirs: Boolean = true
) {
    if (mkdirs) mkparents()
    toJFile().appendText(s)
}


fun FsFile.walk(direction: FileWalkDirection = FileWalkDirection.TOP_DOWN) =
    (this.toJFile()).walk(direction = direction).map {
        with(fileSystem) {
            it.toMFile()
        }
    }

fun FsFile.walkTopDown() = walk(direction = FileWalkDirection.TOP_DOWN)
fun FsFile.walkBottomUp() = walk(direction = FileWalkDirection.BOTTOM_UP)




