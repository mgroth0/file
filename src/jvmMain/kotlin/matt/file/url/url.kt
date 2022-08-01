package matt.file.url

import java.net.URI


actual class MURL actual constructor(path: String): CommonURL {

  override val cpath = path

  val jURL = URI(path).toURL()

  actual val protocol = jURL.protocol

  actual override fun resolve(other: String): MURL {
	return MURL(jURL.toURI().resolve(other).toString())
  }

  actual override fun toString() = cpath

  actual fun loadText() = jURL.readText()

}

