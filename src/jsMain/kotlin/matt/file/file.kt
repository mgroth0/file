package matt.file

import matt.file.CaseSensitivity.CaseInSensitive
import matt.file.construct.mFile
import matt.model.obj.text.MightExistAndWritableText
import matt.model.obj.text.WritableBytes


actual val defaultCaseSensitivity by lazy {
    CaseInSensitive
}

actual sealed class MFile actual constructor(userPath: String, actual val caseSensitivity: CaseSensitivity) : CommonFile, MightExistAndWritableText, WritableBytes {


    actual val userPath = userPath.removeSuffix(SEP)
    actual override val cpath: String = userPath

    val names by lazy { cpath.split(SEP) }

    actual override val fName by lazy { names.first() }

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

    @Suppress("UNUSED_PARAMETER")
    actual override var text: String
        get() = TODO("Not yet implemented")
        set(value) {
            TODO("Not yet implemented")
        }

    actual fun mkdirs(): Boolean {
        TODO("Not yet implemented")
    }

    actual override val filePath get() = cpath
    actual override val partSep = SEP
    actual override fun isDir(): Boolean {
        TODO("Not yet implemented")
    }

    @Suppress("UNUSED_PARAMETER")
    actual override var bytes: ByteArray
        get() = TODO("Not yet implemented")
        set(value) {
            TODO("Not yet implemented")
        }

    actual override fun exists(): Boolean {
        TODO("Not yet implemented")
    }


    actual fun listFilesAsList(): List<MFile>? {
        TODO("Not yet implemented")
    }

    actual fun deleteIfExists() {
    }


}

internal actual const val SEP = "/"
