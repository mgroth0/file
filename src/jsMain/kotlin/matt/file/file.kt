package matt.file

actual fun mFile(userPath: String): MFile {

  /*val f = File(userPath)
  if (f.isDirectory) return Folder(userPath)
  return fileTypes[f.extension].constructors.first().call(userPath)*/

  return UnknownFile(userPath)

}

private const val SEP = "/"

actual sealed class MFile actual constructor(internal actual val userPath: String): CommonFile {

  private val path = userPath.removePrefix(SEP).removeSuffix(SEP)
  operator fun plus(other: MFile) = mFile(path + SEP + other.path)
  operator fun plus(other: String) = this + mFile(other)
  override fun toString() = path

  actual override fun getParentFile(): MFile? {
	val names = path.split(SEP)
	if (names.size > 1) return mFile(names.dropLast(1).joinToString(SEP))
	return null
  }

}