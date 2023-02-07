package matt.file

import matt.file.construct.mFile
import matt.lang.not
import matt.model.data.file.FilePath
import matt.model.data.file.FolderPath
import matt.model.data.message.SFile
import matt.model.obj.text.MightExistAndWritableText
import matt.model.obj.text.WritableBytes
import kotlin.reflect.KClass


/*file or url*/
interface FileOrURL {
  val cpath: String

  fun resolve(other: String): FileOrURL
  operator fun plus(other: String) = resolve(other)
}


interface CommonFile: FileOrURL, FilePath {


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

class FileExtension(input: String) {
  init {
	require(not(input.endsWith("."))) {
	  "file extension \"${input}\" should not end with a dot"
	}
  }

  val id = input.removePrefix(".")

  override fun equals(other: Any?): Boolean {
	return other is FileExtension && other.id == id
  }

  override fun hashCode(): Int {
	return id.hashCode()
  }

  val afterDot = id
  val withPrefixDot = ".$id"

}

expect sealed class MFile(userPath: String): CommonFile, MightExistAndWritableText, WritableBytes {


  override fun isDir(): Boolean
  override val partSep: String
  override val filePath: String
  val userPath: String
  override val cpath: String

  override val fName: String

  override fun getParentFile(): MFile?

  fun resolve(other: MFile, cls: KClass<out MFile>? = null): MFile
  override fun resolve(other: String): MFile

  final override fun toString(): String

  override var text: String
  override var bytes: ByteArray

  fun mkdirs(): Boolean


  override fun exists(): Boolean


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


class UnknownFile(userPath: String): MFile(userPath)

fun MFile.requireIsFolder(): Folder {
  if (this !is Folder) {
	if (this.isDir()) return Folder(userPath)
	error("$this is not a folder. Does it exist?")
  }
  return this
}

open class Folder(userPath: String): MFile(userPath), FolderPath

sealed class CodeFile(userPath: String): MFile(userPath)

val String.kt get() = KotlinFile("$this.kt")

class KotlinFile(userPath: String): CodeFile(userPath) {
  companion object {
	const val FILE_ANNO_LINE_MARKER = "@file:"
  }
}

val String.py get() = PythonFile("$this.py")

class PythonFile(userPath: String): CodeFile(userPath)
class JavaFile(userPath: String): CodeFile(userPath)
class GroovyFile(userPath: String): CodeFile(userPath)
interface ShellFile: CommonFile

val String.sh get() = ShellFileImpl("$this.sh")

class ShellFileImpl(userPath: String): CodeFile(userPath), ShellFile
class ZshFile(userPath: String): CodeFile(userPath), ShellFile

val String.scpt get() = BinaryApplescriptFile("$this.scpt")

class BinaryApplescriptFile(userPath: String): ExecutableFile(userPath)

val String.applescript get() = ApplescriptFile("$this.applescript")

class ApplescriptFile(userPath: String): CodeFile(userPath)

interface ArchiveFile: CommonFile

sealed class BaseZip(userPath: String): MFile(userPath), ArchiveFile

class ZipFile(userPath: String): BaseZip(userPath)

val String.jar get() = JarFile("$this.jar")

class JarFile(userPath: String): BaseZip(userPath)

sealed class ExecutableFile(userPath: String): MFile(userPath)

val String.kexe get() = KExeFile("$this.kexe")

class KExeFile(userPath: String): ExecutableFile(userPath)
class ExeFile(userPath: String): ExecutableFile(userPath)


sealed class DiskImageFile(userPath: String): MFile(userPath)
class DmgFile(userPath: String): DiskImageFile(userPath)


sealed class DataFile(
  userPath: String, val binary: Boolean
): MFile(userPath) //sealed class HumanReadableDataFile(userPath: String): DataFile(userPath) //sealed class BinaryDataFile(userPath: String): DataFile(userPath)


val String.json get() = JsonFile("$this.json")

class JsonFile(userPath: String): DataFile(userPath, binary = false)

val String.cbor get() = CborFile("$this.cbor")

class CborFile(userPath: String): DataFile(userPath, binary = true)


sealed interface MarkupLanguageFile: CommonFile
class XMLFile(userPath: String): DataFile(userPath, binary = false), MarkupLanguageFile
=class HTMLFile(userPath: String): MFile(userPath), MarkupLanguageFile

val String.md get() = MarkDownFile("$this.md")

class MarkDownFile(userPath: String): MFile(userPath), MarkupLanguageFile


sealed class ImageFile(userPath: String, val raster: Boolean): MFile(userPath)

val String.png get() = PngFile("$this.png")

class PngFile(userPath: String): ImageFile(userPath, raster = true)
class JpgFile(userPath: String): ImageFile(userPath, raster = true)
class TiffFile(userPath: String): ImageFile(userPath, raster = true)

val String.svg get() = SvgFile("$this.svg")

class SvgFile(userPath: String): ImageFile(userPath, raster = false)

val String.icns get() = ICNSFile("$this.icns")


class ICNSFile(userPath: String): ImageFile(userPath, raster = true)

val String.ico get() = ICOFile("$this.ico")

class ICOFile(userPath: String): ImageFile(userPath, raster = true)


class MP3File(userPath: String): MFile(userPath)

val String.mp4 get() = MP4File("$this.mp4")

class MP4File(userPath: String): MFile(userPath)

class PdfFile(userPath: String): MFile(userPath)

class PropsFile(userPath: String): DataFile(userPath, binary = false)

class YamlFile(userPath: String): DataFile(userPath, binary = false)
class TomlFile(userPath: String): DataFile(userPath, binary = false)

val String.log get() = LogFile("$this.log")

class LogFile(userPath: String): MFile(userPath)

val String.txt get() = TxtFile("$this.txt")

class TxtFile(userPath: String): MFile(userPath)
class DSStoreFile(userPath: String): DataFile(userPath, binary = false)


