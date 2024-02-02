package matt.file

import kotlinx.serialization.Serializable
import matt.file.construct.mFile
import matt.lang.anno.Duplicated
import matt.lang.anno.Open
import matt.lang.anno.optin.ExperimentalMattCode
import matt.lang.assertions.require.requireEquals
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.CaseSensitivityAwareFilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFileBase
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.ResolvableFileOrUrl
import matt.lang.model.file.ResolvableFilePath
import matt.lang.model.file.constructFilePath
import matt.lang.model.file.exts.contains
import matt.lang.model.file.withinFileSystem
import matt.model.data.message.AbsLinuxFile
import matt.model.data.message.AbsMacFile
import matt.model.data.message.RelLinuxFile
import matt.model.data.message.RelMacFile
import matt.model.obj.text.ReadableFile
import matt.model.obj.text.WritableFile
import kotlin.jvm.JvmInline


/*need a FileOrURL class with guaranteed equality if path is the same*/
@JvmInline
@Serializable
value class Src<F : ResolvableFileOrUrl<F>>(override val path: String) : ResolvableFileOrUrl<F> {

    override fun resolve(other: String): F {
        TODO()
    }

    override fun toString(): String = path
}

class UnknownFileOrURL<F : ResolvableFileOrUrl<F>>(override val path: String) : ResolvableFileOrUrl<F> {

    override fun resolve(other: String): F {
        TODO()
    }

}

fun <F : FsFileBase<F>> F.root(): F = fileInSameFs(fileSystem.separator)

fun FileSystem.root() = SimpleFsFileImpl(
    constructFilePath(separator),
    fileSystem = this,
)


fun ResolvableFilePath<*>.toFsFile() = mFile(path, MacFileSystem)
fun ResolvableFilePath<*>.toMFile() = mFile(path, MacFileSystem)


fun AnyFsFile.verifyToAbsMacFile(): AbsMacFile {
    requireEquals(this.fileSystem, MacFileSystem)
    require(this.isAbsolute)
    return toAbsMacFile()
}

fun ResolvableFilePath<*>.toRelMacFile() = RelMacFile(path)
fun ResolvableFilePath<*>.toAbsMacFile() = AbsMacFile(path)
fun ResolvableFilePath<*>.toRelLinuxFile() = RelLinuxFile(path)
fun ResolvableFilePath<*>.toAbsLinuxFile() = AbsLinuxFile(path)


//expect val caseSensitivityOfExecutingMachine: CaseSensitivity

const val OVERRIDE_MEMBERS_SHOULD_BE_FINAL = "OverrideMembersShouldBeFinal"

typealias AnyFsFileImpl = FsFileImpl<*>

class SimpleFsFileImpl(
    fsFilePath: CaseSensitivityAwareFilePath,
    fileSystem: FileSystem
) : FsFileImpl<SimpleFsFileImpl>(fsFilePath, fileSystem) {
    override fun constructSameType(
        convertedFsFilePath: CaseSensitivityAwareFilePath,
        newFileSystem: FileSystem
    ): SimpleFsFileImpl = SimpleFsFileImpl(convertedFsFilePath, newFileSystem)

}

abstract class FsFileImpl<F : FsFileImpl<F>>(
    final override val fsFilePath: CaseSensitivityAwareFilePath,
    final override val fileSystem: FileSystem
) : FsFileBase<F>() {

    final override fun fileInSameFs(path: String): F {
        val convertedPath = fileSystem.constructFilePath(path)
        return constructSameType(convertedPath, fileSystem)
    }

    @ExperimentalMattCode("need thorough testing for crazy stuff like this. This could result in have a CaseSensitive FsFilePath in a case-insensitive filesystem...")
    final override fun withinFileSystem(newFileSystem: FileSystem): F {
        val convertedPath = fsFilePath.withinFileSystem(newFileSystem)
        return constructSameType(convertedPath, newFileSystem)
    }

    protected abstract fun constructSameType(
        convertedFsFilePath: CaseSensitivityAwareFilePath,
        newFileSystem: FileSystem
    ): F

    final override val isRoot: Boolean
        get() = fsFilePath.path == fileSystem.separator

    final override val parent: F?
        get() = when {
            isRoot                         -> null
            isAbsolute && names.size == 2  -> fileInSameFs(fileSystem.root().path)
            !isAbsolute && names.size == 1 -> null
            else                           -> fileInSameFs(names.dropLast(1).joinToString(separator = partSep))
        }

    val isRelative get() = !path.startsWith(partSep)

    final override val isAbsolute: Boolean
        get() = !isRelative

    final override fun toString(): String {
        /*at least one java standard lib class expects File.toString() to just print the path... (Runtime.exec or something like that)*/
        return path
    }


    @Open
    @Duplicated
    override fun relativeTo(other: F): F {
        require(this in other) {
            "$this must be in $other in order to get the relative path"
        }
        val path = fileSystem.constructFilePath(
            this.path.removePrefix(other.path).removePrefix(partSep)
        )
        return fileInSameFs(path.path)
    }
}


expect fun AnyFsFile.toIoFile(): IoFile<*>

interface IoFile<F : IoFile<F>> : ReadableFile<F>, WritableFile<F>

