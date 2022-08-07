package matt.file

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import matt.file.construct.mFile
import matt.file.url.MURL
import matt.klib.release.Release
import matt.stream.message.SFile


/*file or url*/
interface FileOrURL {
  val cpath: String

  fun resolve(other: String): FileOrURL
  operator fun plus(other: String) = resolve(other)
}


fun String.isValidHttpUrl(): Boolean {
  val url = try {
	MURL(this)
  } catch (e: Exception) {
	return false
  }

  return url.protocol === "http:" || url.protocol === "https:";
}


fun fileOrURL(s: String): FileOrURL {
  return if (s.isValidHttpUrl()) MURL(s) else mFile(s)
}


interface CommonFile: FileOrURL {


  fun getParentFile(): MFile?
  val parent get() = getParentFile()
  val fname: String


  //  fun resolve(other: MFile): MFile
  //  fun resolve(other: String): MFile
  //
  //  operator fun plus(other: MFile) = resolve(other)
  //  operator fun plus(other: String) = resolve(other)
}


internal expect val SEP: String

fun SFile.toMFile() = mFile(path)

expect sealed class MFile(userPath: String): CommonFile {

  val userPath: String
  override val cpath: String

  override val fname: String

  override fun getParentFile(): MFile?

  fun resolve(other: MFile): MFile
  override fun resolve(other: String): MFile

  final override fun toString(): String


}

internal annotation class Extensions(vararg val exts: String)


class UnknownFile(userPath: String): MFile(userPath)

open class Folder(userPath: String): MFile(userPath)

sealed class CodeFile(userPath: String): MFile(userPath)

val String.kt get() = KotlinFile("$this.kt")

@Extensions("kt") class KotlinFile(userPath: String): CodeFile(userPath) {
  companion object {
	const val FILE_ANNO_LINE_MARKER = "@file:"
  }
}

@Extensions("py") class PythonFile(userPath: String): CodeFile(userPath)
@Extensions("java") class JavaFile(userPath: String): CodeFile(userPath)
@Extensions("groovy") class GroovyFile(userPath: String): CodeFile(userPath)
interface ShellFile: CommonFile
@Extensions("sh") class ShellFileImpl(userPath: String): CodeFile(userPath), ShellFile
@Extensions("zshrc", "zsh") class ZshFile(userPath: String): CodeFile(userPath), ShellFile

val String.scpt get() = BinaryApplescriptFile("$this.scpt")

@Extensions("scpt") class BinaryApplescriptFile(userPath: String): ExecutableFile(userPath)

val String.applescript get() = ApplescriptFile("$this.applescript")

@Extensions("applescript") class ApplescriptFile(userPath: String): CodeFile(userPath)

interface ArchiveFile: CommonFile

sealed class BaseZip(userPath: String): MFile(userPath), ArchiveFile

@Extensions("zip") class ZipFile(userPath: String): BaseZip(userPath)

val String.jar get() = JarFile("$this.jar")

@Extensions("jar") class JarFile(userPath: String): BaseZip(userPath)

sealed class ExecutableFile(userPath: String): MFile(userPath)

val String.kexe get() = KExeFile("$this.kexe")

@Extensions("kexe") class KExeFile(userPath: String): ExecutableFile(userPath)
@Extensions("exe") class ExeFile(userPath: String): ExecutableFile(userPath)


sealed class DiskImageFile(userPath: String): MFile(userPath)
@Extensions("dmg") class DmgFile(userPath: String): DiskImageFile(userPath)


sealed class DataFile(userPath: String, val binary: Boolean):
  MFile(userPath) //sealed class HumanReadableDataFile(userPath: String): DataFile(userPath)
//sealed class BinaryDataFile(userPath: String): DataFile(userPath)


val String.json get() = JsonFile("$this.json")

@Extensions("json") class JsonFile(userPath: String): DataFile(userPath, binary = false)
@Extensions("cbor") class CborFile(userPath: String): DataFile(userPath, binary = true)

sealed interface MarkupLanguageFile: CommonFile
@Extensions("xml") class XMLFile(userPath: String): DataFile(userPath, binary = false), MarkupLanguageFile
@Extensions("html") class HTMLFile(userPath: String): MFile(userPath), MarkupLanguageFile
@Extensions("md") class MarkDownFile(userPath: String): MFile(userPath), MarkupLanguageFile


sealed class ImageFile(userPath: String, val raster: Boolean): MFile(userPath)
@Extensions("png") class PngFile(userPath: String): ImageFile(userPath, raster = true)
@Extensions("jpg,jpeg") class JpgFile(userPath: String): ImageFile(userPath, raster = true)
@Extensions("tif", "tiff") class TiffFile(userPath: String): ImageFile(userPath, raster = true)
@Extensions("svg") class SvgFile(userPath: String): ImageFile(userPath, raster = false)


@Extensions("pdf") class PdfFile(userPath: String): MFile(userPath)

@Extensions("properties") class PropsFile(userPath: String): DataFile(userPath, binary = false)

@Extensions("yaml", "yml") class YamlFile(userPath: String): DataFile(userPath, binary = false)
@Extensions("toml") class TomlFile(userPath: String): DataFile(userPath, binary = false)

val String.log get() = LogFile("$this.log")

@Extensions("log") class LogFile(userPath: String): MFile(userPath)

val String.txt get() = TxtFile("$this.txt")

@Extensions("txt") class TxtFile(userPath: String): MFile(userPath)
@Extensions("DS_Store") class DSStoreFile(userPath: String): DataFile(userPath, binary = false)


object GitHub {
  fun releasesOf(project: String): List<Release> {
	val json = MURL("https://api.github.com/repos/mgroth0/${project.removeSurrounding("/")}/tags").loadText()
	return Json.decodeFromString(json)
  }

  fun releasesPageOf(project: String) = MURL("https://github.com/mgroth0/${project.removeSurrounding("/")}/releases")

}


