package matt.file

import kotlinx.serialization.Serializable
import matt.file.construct.mFile
import matt.file.ext.FileExtension
import matt.model.data.file.FilePath
import matt.model.data.file.FolderPath
import matt.model.data.message.SFile
import matt.model.obj.text.MightExist
import matt.model.obj.text.MightExistAndWritableText
import matt.model.obj.text.WritableBytes
import matt.prim.str.ensurePrefix
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass


/*file or url*/
interface FileOrURL {
    val cpath: String

    fun resolve(other: String): FileOrURL
    operator fun plus(other: String) = resolve(other)

    operator fun get(item: String): FileOrURL {
        return resolve(item)
    }

}

/*need a FileOrURL class with guaranteed equality if path is the same*/
@JvmInline
@Serializable
value class Src(private val path: String) : FileOrURL {
    override val cpath: String
        get() = path

    override fun resolve(other: String): FileOrURL {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return path
    }
}

class UnknownFileOrURL(path: String) : FileOrURL {
    override val cpath = path

    override fun resolve(other: String): FileOrURL {
        TODO("Not yet implemented")
    }

}

object FSRoot : FileOrURL {
    override val cpath: String
        get() = TODO("Not yet implemented")

    override fun resolve(other: String) = mFile(other.ensurePrefix("/"))
}


interface CommonFile : FileOrURL, FilePath, MightExist {


    fun getParentFile(): MFile?
    val parent get() = getParentFile() //  val fName: String


    //  fun resolve(other: MFile): MFile
    //  fun resolve(other: String): MFile
    //
    //  operator fun plus(other: MFile) = resolve(other)
    //  operator fun plus(other: String) = resolve(other)
}


internal expect val SEP: String

fun SFile.toMFile() = mFile(path)

fun MFile.toSFile() = SFile(userPath)


enum class CaseSensitivity {
    CaseSensitive, CaseInSensitive
}


expect val defaultCaseSensitivity: CaseSensitivity

expect sealed class MFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) : CommonFile,
    MightExistAndWritableText, WritableBytes {


    override fun isDir(): Boolean
    override val partSep: String
    override val filePath: String
    val userPath: String
    override val cpath: String

    override val fName: String

    override fun getParentFile(): MFile?

    fun resolve(
        other: MFile,
        cls: KClass<out MFile>? = null
    ): MFile

    override fun resolve(other: String): MFile

    final override fun toString(): String

    override var text: String
    override var bytes: ByteArray

    fun mkdirs(): Boolean


    override fun exists(): Boolean

    fun listFilesAsList(): List<MFile>?

    fun deleteIfExists()


}


fun fileClassForExtension(extension: FileExtension): KClass<out MFile> {
    return when (extension.afterDot) {
        "kt"           -> KotlinFile::class
        "py"           -> PythonFile::class
        "java"         -> JavaFile::class
        "groovy"       -> GroovyFile::class
        "sh"           -> ShellFileImpl::class
        "zshrc", "zsh" -> ZshFile::class
        "scpt"         -> BinaryApplescriptFile::class
        "applescript"  -> ApplescriptFile::class
        "zip"          -> ZipFile::class
        "jar"          -> JarFile::class
        "kexe"         -> KExeFile::class
        "exe"          -> ExeFile::class
        "dmg"          -> DmgFile::class
        "json"         -> JsonFile::class
        "cbor"         -> CborFile::class
        "xml"          -> XMLFile::class
        "html"         -> HTMLFile::class
        "md"           -> MarkDownFile::class
        "png"          -> PngFile::class
        "jpg", "jpeg"  -> JpgFile::class
        "tif", "tiff"  -> TiffFile::class
        "svg"          -> SvgFile::class
        "icns"         -> ICNSFile::class
        "ico"          -> ICOFile::class
        "mp3"          -> MP3File::class
        "mp4"          -> MP4File::class
        "pdf"          -> PdfFile::class
        "properties"   -> PropsFile::class
        "yaml", "yml"  -> YamlFile::class
        "toml"         -> TomlFile::class
        "log"          -> LogFile::class
        "txt"          -> TxtFile::class
        "DS_Store"     -> DSStoreFile::class
        else           -> UnknownFile::class
    }
}


class UnknownFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

fun MFile.asFolder() = Folder(userPath)

fun MFile.requireIsExistingFolder(): Folder {
    if (this !is Folder) {
        if (this.isDir()) return Folder(userPath)
        error("$this is not a folder. Does it exist?")
    }
    return this
}

open class Folder(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity), FolderPath {
    constructor(mFile: MFile) : this(mFile.userPath)
}

sealed class CodeFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

val String.kt get() = KotlinFile("$this.kt")

class KotlinFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    CodeFile(userPath, caseSensitivity) {
    companion object {
        const val FILE_ANNO_LINE_MARKER = "@file:"
    }
}

val String.py get() = PythonFile("$this.py")

class PythonFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    CodeFile(userPath, caseSensitivity)

class JavaFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    CodeFile(userPath, caseSensitivity)

class GroovyFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    CodeFile(userPath, caseSensitivity)

interface ShellFile : CommonFile

val String.sh get() = ShellFileImpl("$this.sh")

class ShellFileImpl(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    CodeFile(userPath, caseSensitivity), ShellFile

class ZshFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    CodeFile(userPath, caseSensitivity), ShellFile

val String.scpt get() = BinaryApplescriptFile("$this.scpt")

class BinaryApplescriptFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ExecutableFile(userPath, caseSensitivity)

val String.applescript get() = ApplescriptFile("$this.applescript")

class ApplescriptFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    CodeFile(userPath, caseSensitivity)

interface ArchiveFile : CommonFile

sealed class BaseZip(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity), ArchiveFile

class ZipFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    BaseZip(userPath, caseSensitivity)

val String.jar get() = JarFile("$this.jar")

class JarFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    BaseZip(userPath, caseSensitivity)

sealed class ExecutableFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

val String.kexe get() = KExeFile("$this.kexe")

class KExeFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ExecutableFile(userPath, caseSensitivity)

class ExeFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ExecutableFile(userPath, caseSensitivity)


sealed class DiskImageFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

class DmgFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    DiskImageFile(userPath, caseSensitivity)


sealed class DataFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity,
    val binary: Boolean
) : MFile(
    userPath,
    caseSensitivity
) //sealed class HumanReadableDataFile(userPath: String): DataFile(userPath) //sealed class BinaryDataFile(userPath: String): DataFile(userPath)


val String.json get() = JsonFile("$this.json")

class JsonFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    DataFile(userPath, caseSensitivity, binary = false)

val String.cbor get() = CborFile("$this.cbor")

class CborFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    DataFile(userPath, caseSensitivity, binary = true)


sealed interface MarkupLanguageFile : CommonFile
class XMLFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    DataFile(userPath, caseSensitivity, binary = false), MarkupLanguageFile

class HTMLFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity), MarkupLanguageFile

val String.md get() = MarkDownFile("$this.md")

class MarkDownFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity), MarkupLanguageFile


sealed class ImageFile(
    userPath: String,
    caseSensitivity: CaseSensitivity,
    val raster: Boolean
) :
    MFile(userPath, caseSensitivity)

val String.png get() = PngFile("$this.png")

class PngFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ImageFile(userPath, caseSensitivity, raster = true)

class JpgFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ImageFile(userPath, caseSensitivity, raster = true)

class TiffFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ImageFile(userPath, caseSensitivity, raster = true)

val String.svg get() = SvgFile("$this.svg")

class SvgFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ImageFile(userPath, caseSensitivity, raster = false)

val String.icns get() = ICNSFile("$this.icns")


class ICNSFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ImageFile(userPath, caseSensitivity, raster = true)

val String.ico get() = ICOFile("$this.ico")

class ICOFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    ImageFile(userPath, caseSensitivity, raster = true)


class MP3File(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

val String.mp4 get() = MP4File("$this.mp4")

class MP4File(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

class PdfFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

class PropsFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    DataFile(userPath, caseSensitivity, binary = false)

class YamlFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    DataFile(userPath, caseSensitivity, binary = false)

class TomlFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    DataFile(userPath, caseSensitivity, binary = false)

val String.log get() = LogFile("$this.log")

class LogFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

val String.txt get() = TxtFile("$this.txt")

class TxtFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    MFile(userPath, caseSensitivity)

class DSStoreFile(
    userPath: String,
    caseSensitivity: CaseSensitivity = defaultCaseSensitivity
) :
    DataFile(userPath, caseSensitivity, binary = false)


