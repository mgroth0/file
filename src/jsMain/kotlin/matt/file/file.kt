package matt.file

import matt.file.construct.mFile
import matt.lang.NOT_IMPLEMENTED
import matt.model.text.WritableText
import kotlin.reflect.KClass


actual sealed class MFile actual constructor(userPath: String): CommonFile, WritableText {


  actual val userPath = userPath.removeSurrounding(SEP)
  actual override val cpath: String = userPath

  val names by lazy { cpath.split(SEP) }

  actual override val fname by lazy { names.first() }

  actual fun resolve(other: MFile, cls: KClass<out MFile>?) =
	if (cls != null) NOT_IMPLEMENTED else mFile(cpath + SEP + other.cpath)

  actual override fun resolve(other: String): MFile = this.resolve(mFile(other))


  operator fun plus(other: MFile) = resolve(other)
  override operator fun plus(other: String) = resolve(other)


  actual override fun getParentFile(): MFile? {
	val names = cpath.split(SEP)
	if (names.size > 1) return mFile(names.dropLast(1).joinToString(SEP))
	return null
  }

  actual final override fun toString() = userPath

  @Suppress("UNUSED_PARAMETER")
  actual override var text: String
	get() = TODO("Not yet implemented")
	set(value) {
	  TODO("Not yet implemented")
	}

  actual fun mkdirs(): Boolean {
	TODO("Not yet implemented")
  }

}

internal actual const val SEP = "/"
