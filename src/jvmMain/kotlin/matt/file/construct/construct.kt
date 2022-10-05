@file:JavaIoFileIsOk

package matt.file.construct

import matt.file.Extensions
import matt.file.Folder
import matt.file.MFile
import matt.file.UnknownFile
import matt.file.ok.JavaIoFileIsOk
import java.io.File
import java.net.URI
import java.nio.file.Path
import kotlin.reflect.KClass

fun Path.toMFile() = toFile().toMFile()
fun File.toMFile(cls: KClass<out MFile>? = null) = mFile(this, cls = cls)


fun mFile(file: MFile) = mFile(file.userPath)
fun mFile(file: File, cls: KClass<out MFile>? = null) = mFile(file.path, cls = cls)
fun mFile(parent: String, child: String) = mFile(File(parent, child))
fun mFile(parent: MFile, child: String) = mFile(parent.cpath, child)
fun mFile(uri: URI) = mFile(File(uri))


actual fun mFile(userPath: String, cls: KClass<out MFile>?): MFile {
  if (cls != null && cls != MFile::class) {

	val constructor = cls.constructors.first()
//	val oldA = constructor.isAccessible
//	constructor.isAccessible = true
	val r = constructor.call(userPath)
//	constructor.isAccessible = oldA
	return r
  }
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
