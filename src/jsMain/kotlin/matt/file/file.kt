package matt.file

import org.w3c.dom.url.URL


actual fun mFile(userPath: String): MFile {

  /*val f = File(userPath)
  if (f.isDirectory) return Folder(userPath)
  return fileTypes[f.extension].constructors.first().call(userPath)*/

  return UnknownFile(userPath)

}


actual sealed class MFile actual constructor(userPath: String): CommonFile {


  actual val userPath = userPath.removePrefix(SEP).removeSuffix(SEP)
  actual override val cpath: String = userPath

  val names by lazy { cpath.split(SEP) }

  actual override val fname by lazy { names.first() }

  actual fun resolve(other: MFile) = mFile(cpath + SEP + other.cpath)
  actual override fun resolve(other: String): MFile = this.resolve(mFile(other))


  operator fun plus(other: MFile) = resolve(other)
  override operator fun plus(other: String) = resolve(other)


  actual override fun getParentFile(): MFile? {
	val names = cpath.split(SEP)
	if (names.size > 1) return mFile(names.dropLast(1).joinToString(SEP))
	return null
  }

  actual final override fun toString() = userPath

}

internal actual const val SEP = "/"

actual class MURL actual constructor(path: String): CommonURL {

  override val cpath = path

  val jsURL = URL(path)

  actual val protocol: String get() = jsURL.protocol

  actual override fun resolve(other: String) = MURL(
	cpath.removeSuffix(CommonURL.URL_SEP) + CommonURL.URL_SEP + other.removePrefix(CommonURL.URL_SEP)
  )

  actual override fun toString() = cpath
}
