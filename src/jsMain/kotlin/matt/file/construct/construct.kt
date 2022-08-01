package matt.file.construct

import matt.file.MFile
import matt.file.UnknownFile


actual fun mFile(userPath: String): MFile {

  /*val f = File(userPath)
  if (f.isDirectory) return Folder(userPath)
  return fileTypes[f.extension].constructors.first().call(userPath)*/

  return UnknownFile(userPath)

}