package matt.file

/*file or url*/
open class Path(path: String) {
  protected val path = path.removePrefix(SEP).removeSuffix(SEP)
  operator fun plus(other: Path) = Path(path + SEP + other.path)
  open operator fun plus(other: MFile) = mFile(path + SEP + other.path)
  open operator fun plus(other: String) = this + Path(other)
  override fun toString() = path
}


actual fun mFile(userPath: String): MFile {

  /*val f = File(userPath)
  if (f.isDirectory) return Folder(userPath)
  return fileTypes[f.extension].constructors.first().call(userPath)*/

  return UnknownFile(userPath)

}


actual sealed class MFile actual constructor(userPath: String): Path(userPath), CommonFile {

  internal actual val userPath = path

  override operator fun plus(other: MFile) = mFile(path + SEP + other.path)
  override operator fun plus(other: String) = this + mFile(other)

  actual override fun getParentFile(): MFile? {
	val names = path.split(SEP)
	if (names.size > 1) return mFile(names.dropLast(1).joinToString(SEP))
	return null
  }

}

private const val SEP = "/"