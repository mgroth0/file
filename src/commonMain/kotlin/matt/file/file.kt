package matt.file

import matt.file.CommonURL.Companion

internal expect val SEP: String

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


interface CommonURL: FileOrURL {

  override val cpath: String

  companion object {
	const val URL_SEP = "/"
  }
}

expect class MURL(path: String): CommonURL {
  val protocol: String

  override fun resolve(other: String): MURL

  final override fun toString(): String
}


interface CommonFile: FileOrURL {


  fun getParentFile(): MFile?
  val parent get() = getParentFile()


  //  fun resolve(other: MFile): MFile
  //  fun resolve(other: String): MFile
  //
  //  operator fun plus(other: MFile) = resolve(other)
  //  operator fun plus(other: String) = resolve(other)
}

expect fun mFile(userPath: String): MFile

expect sealed class MFile(userPath: String): CommonFile {
  val userPath: String
  override val cpath: String

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
@Extensions("applescript") class ApplescriptFile(userPath: String): CodeFile(userPath)
open class BaseZip internal constructor(userPath: String): MFile(userPath)
@Extensions("zip") open class ZipFile(userPath: String): BaseZip(userPath)

val String.jar get() = JarFile("$this.jar")

@Extensions("jar") class JarFile(userPath: String): BaseZip(userPath)
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

@Extensions("properties") class PropsFile(userPath: String): DataFile(userPath, binary = false)

@Extensions("yaml", "yml") class YamlFile(userPath: String): DataFile(userPath, binary = false)
@Extensions("toml") class TomlFile(userPath: String): DataFile(userPath, binary = false)

@Extensions("log") class LogFile(userPath: String): MFile(userPath)
@Extensions("txt") class TxtFile(userPath: String): MFile(userPath)
@Extensions("DS_Store") class DSStoreFile(userPath: String): DataFile(userPath, binary = false)
