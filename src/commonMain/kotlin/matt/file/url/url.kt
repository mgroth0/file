package matt.file.url

import matt.file.FileOrURL

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

  fun loadText(): String
}
