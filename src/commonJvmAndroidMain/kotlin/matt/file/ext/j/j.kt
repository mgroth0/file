
package matt.file.ext.j

import matt.collect.itr.recurse.DEFAULT_INCLUDE_SELF
import matt.collect.itr.recurse.recurse
import matt.file.JioFile
import matt.file.JvmMFile
import matt.file.common.AnyFsFileImpl
import matt.file.commons.fnames.DS_STORE
import matt.file.construct.mFile
import matt.file.construct.toMFile
import matt.file.ext.ExtensionSet
import matt.file.ext.FileExtension
import matt.file.ext.finalExtension
import matt.file.ext.mightHaveAnExtension
import matt.file.ext.singleExtension
import matt.file.ext.singleExtensionOrNullIfNoDots
import matt.file.toJioFile
import matt.file.types.requireIsExistingFolder
import matt.lang.anno.EnforcedMin
import matt.lang.common.NOT_IMPLEMENTED
import matt.lang.file.toJFile
import matt.lang.j.userHome
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.FileSystem
import matt.lang.model.file.ensureSuffix
import matt.lang.model.file.fName
import matt.log.warn.common.warn
import matt.model.code.FormatterConfig
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.FileTime
import kotlin.io.path.appendText
import kotlin.io.path.bufferedReader
import kotlin.io.path.nameWithoutExtension


context(FileSystem)
@Suppress("DEPRECATION")
fun createTempDir(
    prefix: String = "tmp",
    suffix: String? = null,
    directory: AnyFsFile? = null
) =
    createTempDir(prefix, suffix, directory?.toJFile()).toMFile()


context(FileSystem)
@Suppress("DEPRECATION")
fun createTempFile(
    prefix: String = "tmp",
    suffix: String? = null,
    directory: AnyFsFile? = null
) = createTempFile(prefix, suffix, directory?.toJFile()).toMFile()


/*fun matt.file.JioFile.copyRecursively(
  target: FsFile,
  overwrite: Boolean = false,
  onError: (MFile, IOException)->OnErrorAction = { _, exception -> throw exception }
): Boolean = userFile.copyRecursively(target.userFile, overwrite) { f, e ->
  onError(MFile(f), e)
}*/


fun String.writeToFile(
    f: AnyFsFile,
    mkdirs: Boolean = true
) {
    if (mkdirs) {
        f.parent!!.toJioFile().mkdirs()
    }
    f.toJioFile().writeText(this)
}


fun <T : AnyFsFile> Iterable<T>.filterHasExtension(ext: FileExtension) = filter { it.hasExtension(ext) }

fun <T : AnyFsFile> Sequence<T>.filterHasExtension(ext: FileExtension) = filter { it.hasExtension(ext) }


fun String.toPath(): Path = FileSystems.getDefault().getPath(trim())


@EnforcedMin
fun AnyFsFile.hasAnyExtension(
    extension: FileExtension,
    vararg extensions: FileExtension
): Boolean {
    val ext = singleExtension
    if (ext == extension) return true
    extensions.forEach {
        if (it == ext) return true
    }
    return false
}

fun AnyFsFile.hasAnyExtension(
    extensions: ExtensionSet
): Boolean {
    val ext = singleExtension
    return extensions.any { it == ext }
}

fun AnyFsFile.hasAnyFinalExtension(
    extension: FileExtension,
    vararg extensions: FileExtension
): Boolean {

    val ext = finalExtension

    if (extension == ext) return true

    return extensions.any { it == ext }
}


fun AnyFsFile.hasAnyFinalExtension(
    extensions: ExtensionSet
): Boolean {
    val ext = finalExtension
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


fun AnyFsFile.relativeToOrSelf(base: AnyFsFile): AnyFsFile =
    with(myFileSystem) { toJFile().relativeToOrSelf(base.toJFile()).toMFile() }

fun AnyFsFile.relativeToOrNull(base: AnyFsFile): AnyFsFile? =
    with(myFileSystem) { toJFile().relativeToOrNull(base.toJFile())?.toMFile() }


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


fun JvmMFile.recursiveLastModified(): FileTime {
    var greatest = FileTime.fromMillis(0)
    recurse { it.listFiles()?.toList() ?: listOf() }.forEach {
        greatest = listOf(greatest, it.lastModified()).maxOrNull()!!
    }
    return greatest
}


fun JvmMFile.recursiveChildren(includeSelf: Boolean = DEFAULT_INCLUDE_SELF) =
    recurse(includeSelf = includeSelf) { it.listFiles()?.toList() ?: listOf() }

val JvmMFile.ensureAbsolute get() = apply { require(isAbs) { "$this is not absolute" } }
val JvmMFile.absolutePathEnforced: String get() = ensureAbsolute.path


operator fun JvmMFile.plus(item: Char): JvmMFile = resolve(item.toString())


val JvmMFile.unixNlink get() = Files.getAttribute(this, "unix:nlink").toString().toInt()
val JvmMFile.hardLinkCount get() = unixNlink


val AnyFsFile.hasKotlinExtension get() = hasExtension(FileExtension.KT) || hasExtension(FileExtension.KTS)
infix fun AnyFsFile.hasExtension(extension: FileExtension) = mightHaveAnExtension &&  singleExtension == extension


infix fun JvmMFile.withExtension(ext: FileExtension): JvmMFile {
    with(myFileSystem) {
        if ("." !in this@withExtension.fName) mFile(this@withExtension.path + "." + ext)
        return when (this@withExtension.fName.substringAfterLast(".")) {
            ext.afterDot -> this@withExtension
            else         ->
                mFile(
                    this@withExtension.path.substringBeforeLast(".") + ext.withPrefixDot
                )
        }
    }
}


fun JvmMFile.appendln(line: String) {
    append(line + "\n")
}

fun AnyFsFile.resRepExt(newExt: FileExtension) =
    mFile(
        parent!!.path + JioFile.separator + toJioFile().nameWithoutExtension + "." + newExt.afterDot,
        fileSystem = myFileSystem
    )

fun AnyFsFile.verifyWithNoSingleExtension(): AnyFsFileImpl {
    check(name.count { it == '.' } == 1)
    return mFile(
        parent!!.path + JioFile.separator + toJioFile().nameWithoutExtension,
        fileSystem = myFileSystem
    )
}


internal class IndexFolder(val f: AnyFsFile) {
    val name = f.fName
    val index = name.toInt()
    operator fun plus(other: JvmMFile) = f[other]
    operator fun plus(other: String) = f + other
    fun next() = IndexFolder(f.parent!! + (index + 1).toString())
    fun previous() = IndexFolder(f.parent!! + (index - 1).toString())
}


/*val JvmMFile.abspath: String
    get() = toJFile().absolutePath*/

infix fun JvmMFile.withLastNameExtension(s: String) = mFile(abspath.removeSuffix(myFileSystem.separator) + s, myFileSystem)


fun JvmMFile.moveInto(
    newParent: JvmMFile,
    overwrite: Boolean = false
): JvmMFile =
    (
        if (overwrite) Files.move(
            this, (newParent + name), StandardCopyOption.REPLACE_EXISTING
        )
        else Files.move(this, (newParent + name))
    ).toFile().toMFile(myFileSystem)


/*calling this 'mkdir' like I used to could cause errors since it shares a name with the shell command*/
fun JvmMFile.mkFold(child: String) =
    resolve(child).apply {
        mkdir()
    }.toJioFile().requireIsExistingFolder()

/*calling this 'mkdir' like I used to could cause errors since it shares a name with the shell command*/
fun JvmMFile.mkFold(int: Int) = mkFold(int.toString())

fun JvmMFile.isImage() = singleExtensionOrNullIfNoDots?.isImage == true


val JvmMFile.url get() = toUri().toURL()


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

fun JvmMFile.wildcardChildrenPath() = path.ensureSuffix(JioFile.separator) + "*"

fun JvmMFile.startsWith(other: JvmMFile): Boolean = idFile.startsWith(other.idFile)
fun JvmMFile.startsWith(other: String): Boolean = idFile.startsWith(identityGetter(other))
fun JvmMFile.endsWith(other: JvmMFile) = idFile.endsWith(other.idFile)
fun JvmMFile.endsWith(other: String): Boolean = idFile.endsWith(identityGetter(other))

fun JvmMFile.mkparents() = parent!!.mkdirs()
fun JvmMFile.tildeString() = toString().replace(userHome.removeSuffix(myFileSystem.separator), "~")

fun JvmMFile.isBlank() =
    bufferedReader().run {
        val r = read() == -1
        close()
        r
    }


fun JvmMFile.append(
    s: String,
    mkdirs: Boolean = true
) {
    if (mkdirs) mkparents()
    appendText(s)
}


fun AnyFsFile.walk(direction: FileWalkDirection = FileWalkDirection.TOP_DOWN) =
    (toJFile()).walk(direction = direction).map {
        with(myFileSystem) {
            it.toMFile()
        }
    }

fun AnyFsFile.walkTopDown() = walk(direction = FileWalkDirection.TOP_DOWN)
fun AnyFsFile.walkBottomUp() = walk(direction = FileWalkDirection.BOTTOM_UP)






interface KotlinFormatterConfigInter : FormatterConfig {
    val linter: MyKotlinLinter
    val script: Boolean
}

interface MyKotlinLinterProvider {
    fun linterFor(
        editorConfig: String
    ): MyKotlinLinter
}

interface MyKotlinLinter {
    fun formatKotlinCode(
        kotlinCode: String,
        asScript: Boolean
    ): String
}
