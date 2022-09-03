@file:JavaIoFileIsOk
package matt.file.construct

import matt.file.Extensions
import matt.file.Folder
import matt.file.MFile
import matt.file.UnknownFile
import matt.file.ok.JavaIoFileIsOk
import matt.collect.dmap.withStoringDefault
import matt.stream.recurse.recurse
import java.io.File
import java.net.URI
import java.nio.file.Path
import kotlin.reflect.KClass

fun Path.toMFile() = toFile().toMFile()
fun File.toMFile() = mFile(this)


fun mFile(file: MFile) = mFile(file.userPath)
fun mFile(file: File) = mFile(file.path)
fun mFile(parent: String, child: String) = mFile(File(parent, child))
fun mFile(parent: MFile, child: String) = mFile(parent.cpath, child)
fun mFile(uri: URI) = mFile(File(uri))



actual fun mFile(userPath: String): MFile {
  val f = File(userPath)
  if (f.isDirectory) return Folder(userPath)
  return fileTypes[f.extension].constructors.first().call(userPath)

  //  val f = File(userPath)
  //  MFile::class.sealedSubclasses.firstOrNull {
  //	it.annotations.filterIsInstance<Extensions>().firstOrNull()?.exts?.let { f.extension in it } ?: false
  //  }
  //
  //  when (File(userPath).extension) {
  //	"json" -> JsonFile(userPath)
  //	else   -> UnknownFile(userPath)
  //  }
}


private val fileTypes by lazy {
  mutableMapOf<String, KClass<out MFile>>().withStoringDefault { extension ->
	MFile::class.sealedSubclasses.flatMap { it.recurse { it.sealedSubclasses } }.firstOrNull {
	  val b = it.annotations.filterIsInstance<Extensions>().firstOrNull()?.exts?.let { extension in it } ?: false
	  b
	} ?: UnknownFile::class
  }
}
