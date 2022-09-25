package matt.file.construct

import matt.file.MFile
import matt.file.UnknownFile
import matt.lang.NOT_IMPLEMENTED
import kotlin.reflect.KClass


actual fun mFile(userPath: String, cls: KClass<out MFile>?): MFile {

  if (cls!=null) NOT_IMPLEMENTED

  /*val f = File(userPath)
  if (f.isDirectory) return Folder(userPath)
  return fileTypes[f.extension].constructors.first().call(userPath)*/

  return UnknownFile(userPath)

}