package matt.file


actual fun mFile(userPath: String): MFile {

  /*val f = File(userPath)
  if (f.isDirectory) return Folder(userPath)
  return fileTypes[f.extension].constructors.first().call(userPath)*/

  return UnknownFile(userPath)

}


actual sealed class MFile actual constructor(userPath: String): CommonFile {


  actual val userPath = userPath.removePrefix(SEP).removeSuffix(SEP)
  actual override val cpath: String = userPath

  actual fun resolve(other: MFile) = mFile(cpath + SEP + other.cpath)
  actual override fun resolve(other: String): MFile = this.resolve(mFile(other))


  actual override fun getParentFile(): MFile? {
	val names = cpath.split(SEP)
	if (names.size > 1) return mFile(names.dropLast(1).joinToString(SEP))
	return null
  }

  actual final override fun toString() = userPath

}

internal actual const val SEP = "/"