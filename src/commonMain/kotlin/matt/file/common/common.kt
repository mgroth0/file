package matt.file.common

import kotlinx.serialization.Serializable
import matt.file.construct.common.mFile
import matt.lang.anno.Duplicated
import matt.lang.anno.Open
import matt.lang.anno.optin.ExperimentalMattCode
import matt.lang.assertions.require.requireEquals
import matt.lang.common.NEVER
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.CaseSensitivityAwareFilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFileBase
import matt.lang.model.file.MacDefaultFileSystem
import matt.lang.model.file.ResolvableFileOrUrl
import matt.lang.model.file.ResolvableFilePath
import matt.lang.model.file.constructFilePath
import matt.lang.model.file.exts.inDir
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

fun <F : FsFileBase<F>> F.root(): F = fileInSameFs(myFileSystem.separator)
fun FileSystem.root() =
    SimpleFsFileImpl(
        constructFilePath(separator),
        fileSystem = this
    )

fun ResolvableFilePath<*>.toFsFile() = mFile(path, MacDefaultFileSystem)
fun ResolvableFilePath<*>.toMFile() = mFile(path, MacDefaultFileSystem)
fun AnyFsFile.verifyToAbsMacFile(): AbsMacFile {
    requireEquals(myFileSystem, MacDefaultFileSystem)
    require(isAbs)
    return toAbsMacFile()
}

fun ResolvableFilePath<*>.toRelMacFile() = RelMacFile(path)
fun ResolvableFilePath<*>.toAbsMacFile() = AbsMacFile(path)
fun ResolvableFilePath<*>.toRelLinuxFile() = RelLinuxFile(path)
fun ResolvableFilePath<*>.toAbsLinuxFile() = AbsLinuxFile(path)
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
    final override val myFileSystem: FileSystem
) : FsFileBase<F>() {

    final override fun fileInSameFs(path: String): F {
        check(path.isNotBlank()) {
            "path should not be blank"
        }
        val convertedPath = myFileSystem.constructFilePath(path)
        return constructSameType(convertedPath, myFileSystem)
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
        get() = fsFilePath.path == myFileSystem.separator

    final override val parent: F?
        get() =
            when {
                isRoot                    -> null
                names.isEmpty() -> NEVER
                names.size == 1 ->
                    when {
                        isAbs -> fileInSameFs(myFileSystem.root().path)
                        else -> null
                    }
                isAbs -> fileInSameFs(partSep + names.dropLast(1).joinToString(separator = partSep))
                else -> fileInSameFs(names.dropLast(1).joinToString(separator = partSep))
            }

    private val isRelative get() = !path.startsWith(partSep)


    final override val isAbs: Boolean
        get() = !isRelative

    final override fun toString(): String {
        /*at least one java standard lib class expects File.toString() to just print the path... (Runtime.exec or something like that)*/
        return path
    }


    @Open
    @Duplicated
    override fun relativeTo(other: F): F {
        require(this inDir other) {
            "$this must be in $other in order to get the relative path"
        }
        val path =
            myFileSystem.constructFilePath(
                path.removePrefix(other.path).removePrefix(partSep)
            )
        return fileInSameFs(path.path)
    }
}

interface IoFile<F : IoFile<F>> : ReadableFile<F>, WritableFile<F>
