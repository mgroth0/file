package matt.file

import kotlinx.serialization.Serializable
import matt.file.construct.mFile
import matt.lang.anno.Duplicated
import matt.lang.anno.optin.ExperimentalMattCode
import matt.lang.assertions.require.requireEquals
import matt.lang.assertions.require.requireNot
import matt.lang.model.file.CommonFile
import matt.lang.model.file.FileOrURL
import matt.lang.model.file.FilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFile
import matt.lang.model.file.FsFilePath
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.constructFilePath
import matt.lang.model.file.exts.contains
import matt.lang.model.file.withinFileSystem
import matt.model.data.message.AbsMacFile
import matt.model.data.message.MacFile
import matt.model.obj.text.ReadableFile
import matt.model.obj.text.WritableFile
import matt.prim.str.ensurePrefix
import kotlin.jvm.JvmInline


/*need a FileOrURL class with guaranteed equality if path is the same*/
@JvmInline
@Serializable
value class Src(private val path: String) : FileOrURL {
    override val cpath: String
        get() = path

    override fun resolve(other: String): FileOrURL {
        TODO()
    }

    override fun toString(): String {
        return path
    }
}

class UnknownFileOrURL(path: String) : FileOrURL {
    override val cpath = path

    override fun resolve(other: String): FileOrURL {
        TODO()
    }

}

class FSRoot(override val fileSystem: FileSystem) : FsFile {
    override fun withinFileSystem(newFileSystem: FileSystem): FsFile {
        TODO()
    }

    override val fsFilePath: FsFilePath
        get() = fileSystem.constructFilePath(fileSystem.separator)
//            UnsafeFilePath(fileSystem.separator)

    override fun get(item: String): FsFile {
        return resolve(item)
    }

    override fun resolve(other: String): FsFile {
        requireNot(other.startsWith(fileSystem.separatorChar))
        return mFile(other.ensurePrefix(fileSystem.separatorChar.toString()), fileSystem)
    }

    override val isAbsolute = true
    override val parent = null
    override val isRoot = true
    override fun relativeTo(other: FsFile): FsFile {
        error("why would you ever need to relative path of the root to something else?")
    }
}


fun FilePath.toFsFile() = mFile(path, MacFileSystem)
fun FilePath.toMFile() = mFile(path, MacFileSystem)
fun FilePath.toMacFile() = MacFile(filePath)


fun FsFile.verifyToAbsMacFile(): AbsMacFile {
    requireEquals(this.fileSystem, MacFileSystem)
    require(this.isAbsolute)
    return toAbsMacFile()
}

fun FilePath.toAbsMacFile() = AbsMacFile(filePath)


//expect val caseSensitivityOfExecutingMachine: CaseSensitivity

open class FsFileImpl(
    override val fsFilePath: FsFilePath,
    override val fileSystem: FileSystem
) : CommonFile, FsFile {


    @ExperimentalMattCode("need thorough testing for crazy stuff like this. This could result in have a CaseSensitive FsFilePath in a case-insensitive filesystem...")
    final override fun withinFileSystem(newFileSystem: FileSystem): FsFile {
        return FsFileImpl(fsFilePath.withinFileSystem(newFileSystem), newFileSystem)
    }

//    override val parent: FsFile?
//        get() = super.parent

    final override val isRoot: Boolean
        get() = fsFilePath.path == fileSystem.separator

    val names by lazy { cpath.split(partSep) }
    final override val cpath: String get() = path


    override val parent: FsFile?
        get() = when {
            isRoot                         -> null
            isAbsolute && names.size == 2  -> FSRoot(fileSystem)
            !isAbsolute && names.size == 1 -> null
            else                           -> mFile(names.dropLast(1).joinToString(separator = partSep), fileSystem)
        }
    override val parentFile: FsFileImpl? get() = parent?.let { it as FsFileImpl }

    override val name: String
        get() = names.last()

    final val isRelative get() = !path.startsWith(partSep)

    fun resolve(other: FsFileImpl): FsFileImpl {
        require(other.isRelative) {
            "$other should be relative to resolve against $this"
        }
        require(other.fileSystem == fileSystem)
        return mFile(path + partSep + other.path, fileSystem)
    }

    override fun resolve(other: String): FsFileImpl {
        return resolve(mFile(other, fileSystem))
    }

    override operator fun plus(other: String) = resolve(other)

    override operator fun get(item: String): FsFileImpl {
        return resolve(item)
    }

    final override val isAbsolute: Boolean
        get() = !isRelative

    final override fun toString(): String {
        /*at least one java standard lib class expects File.toString() to just print the path... (Runtime.exec or something like that)*/
        return path
    }


    @Duplicated
    override fun relativeTo(other: FsFile): FsFile {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        return FsFileImpl(path, fileSystem)
    }


    final override fun equals(other: Any?): Boolean {
        if (other !is FsFile) return false
        return if (other.fileSystem == fileSystem) {
            fsFilePath == other.fsFilePath
        } else false
    }

    final override fun hashCode(): Int {
        var result = fsFilePath.hashCode()
        result = 31 * result + fileSystem.hashCode()
        return result
    }

}


expect fun FsFile.toIoFile(): IoFile

interface IoFile : ReadableFile, WritableFile

