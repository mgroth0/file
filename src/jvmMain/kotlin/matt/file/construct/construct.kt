@file:JavaIoFileIsOk

package matt.file.construct

import matt.collect.map.dmap.withStoringDefault
import matt.collect.map.lazyMap
import matt.file.FileExtension
import matt.file.Folder
import matt.file.MFile
import matt.file.fileClassForExtension
import matt.model.code.ok.JavaIoFileIsOk
import java.io.File
import java.lang.reflect.Constructor
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
	val constructor = constructorsByCls[cls]
	/*return constructor.call(userPath)*/
	return constructor.newInstance(userPath) as MFile
  }
  val f = File(userPath)
  return constructors[f.extension].newInstance(userPath) as MFile
  /*.call(userPath)*/
}

fun mFolder(userPath: String): Folder {
  return Folder(userPath)
}


private val fileTypes by lazy {


  mutableMapOf<String, KClass<out MFile>>().withStoringDefault { extension ->

	fileClassForExtension(FileExtension(extension))

	/*	MFile::class.sealedSubclasses.flatMap { it.recurse { it.sealedSubclasses } }.firstOrNull {
		  val b = it.annotations.filterIsInstance<Extensions>().firstOrNull()?.exts?.let { extension in it } ?: false
		  b
		} ?: UnknownFile::class*/


  }


}
private val constructors = lazyMap<String, Constructor<*>> {
  /*surprisingly getting constructors is expensive so this could have a huge performance benefit and even solve some bugs maybe*/
  constructorsByCls[fileTypes[it]]
}
private val constructorsByCls = lazyMap<KClass<out MFile>, Constructor<*>> {
  /*surprisingly getting constructors is expensive so this could have a huge performance benefit and even solve some bugs maybe*/
  it.java.constructors.first()
  /*it.constructors.first()*/
}

