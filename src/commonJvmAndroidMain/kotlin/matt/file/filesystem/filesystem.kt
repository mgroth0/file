package matt.file.filesystem

import matt.collect.itr.subList
import matt.file.filesystem.MyMacFileSystemProvider.toUnixPath
import matt.file.macJioFile
import matt.lang.anno.j.JavaMayReturnNull
import matt.lang.assertions.require.requireEquals
import matt.lang.model.file.FileSystem
import matt.lang.model.file.MacDefaultFileSystem
import matt.lang.model.file.MacFileSystemInter
import matt.lang.model.file.constructFilePath
import matt.model.code.sys.LinuxFileSystem
import java.lang.ref.WeakReference
import java.net.URI
import java.nio.channels.FileChannel
import java.nio.channels.SeekableByteChannel
import java.nio.file.AccessMode
import java.nio.file.CopyOption
import java.nio.file.DirectoryStream
import java.nio.file.DirectoryStream.Filter
import java.nio.file.FileStore
import java.nio.file.FileSystems
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.WatchService
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider
import kotlin.io.path.name


/*I MIGHT WANT TO OVERRIDE EQUALS ON THESE CLASSES BECAUSE LOTS OF CODE SEEMS TO CHECK FILESYSTEM EQUALITY AND THESE SHOULD BE CONSIDERED EQUAL TO THEIR SUN COUNTERPARTS...*/
fun FileSystem.toMyJFileSystem(): java.nio.file.FileSystem =
    when (this) {
        is MacFileSystemInter -> MyMacFileSystem
        is LinuxFileSystem    -> MyLinuxFileSystem
        else -> TODO("Get My J FileSystem for $this")
    }
fun FileSystem.toSunFileSystem(): java.nio.file.FileSystem {
    return when (toMyJFileSystem()) {
        MyMacFileSystem -> {
            val default = sunSystem.get()
            check(default::class.simpleName == "MacOSXFileSystem")
            return default
        }
        else -> TODO("Get Sun FileSystem for $this")
    }
}



object MyLinuxFileSystem: MyUnixFilesystem() {

    override fun provider() = MyLinuxFileSystemProvider

    override fun getSeparator() = LinuxFileSystem.separator

    override fun getPath(
        first: String,
        vararg more: String?
    ): Path {
        TODO()
    }
}

object MyMacFileSystem: MyUnixFilesystem() {




    override fun provider(): FileSystemProvider = MyMacFileSystemProvider

    override fun getSeparator(): String = MacDefaultFileSystem.separator



    override fun getPath(
        first: String,
        vararg more: String
    ): Path = macJioFile((listOf(first) + more).joinToString(separator = separator))
}

abstract class MyUnixFilesystem: java.nio.file.FileSystem() {



    final override fun close() {
        TODO()
    }

    final override fun isOpen(): Boolean {
        TODO()
    }

    final override fun isReadOnly(): Boolean {
        TODO()
    }

    final override fun getRootDirectories(): MutableIterable<Path> {
        TODO()
    }

    final override fun getFileStores(): MutableIterable<FileStore> {
        TODO()
    }

    final override fun supportedFileAttributeViews(): MutableSet<String> {
        TODO()
    }
    final override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher {
        TODO()
    }

    final override fun getUserPrincipalLookupService(): UserPrincipalLookupService {
        TODO()
    }

    final override fun newWatchService(): WatchService {
        TODO()
    }
}

object MyLinuxFileSystemProvider: MyUnixFileSystemProvider() {
    override val expectedFileSystemClassName = "LinuxFileSystem"
    override val mattFileSystem: FileSystem
        get() = LinuxFileSystem

    override fun getScheme(): String {
        TODO()
    }

    override fun newFileSystem(
        uri: URI?,
        env: MutableMap<String, *>?
    ): java.nio.file.FileSystem {
        TODO()
    }

    override fun getFileSystem(uri: URI?): java.nio.file.FileSystem {
        TODO()
    }

    override fun getPath(uri: URI): Path {
        TODO()
    }

    override fun newDirectoryStream(
        dir: Path?,
        filter: Filter<in Path>?
    ): DirectoryStream<Path> {
        TODO()
    }

    override fun delete(path: Path?) {
        TODO()
    }

    override fun copy(
        source: Path?,
        target: Path?,
        vararg options: CopyOption?
    ) {
        TODO()
    }

    override fun move(
        source: Path?,
        target: Path?,
        vararg options: CopyOption?
    ) {
        TODO()
    }

    override fun isSameFile(
        path: Path?,
        path2: Path?
    ): Boolean {
        TODO()
    }

    override fun isHidden(path: Path?): Boolean {
        TODO()
    }

    override fun getFileStore(path: Path?): FileStore {
        TODO()
    }

    override fun setAttribute(
        path: Path?,
        attribute: String?,
        value: Any?,
        vararg options: LinkOption?
    ) {
        TODO()
    }
}

object MyMacFileSystemProvider: MyUnixFileSystemProvider() {


    override val expectedFileSystemClassName: String
        get() = "MacOSXFileSystem"
    override val mattFileSystem: FileSystem
        get() = MacDefaultFileSystem


    override fun getScheme(): String {
        TODO()
    }

    override fun newFileSystem(
        uri: URI,
        env: MutableMap<String, *>
    ): java.nio.file.FileSystem {
        TODO()
    }

    override fun getFileSystem(uri: URI): java.nio.file.FileSystem {
        TODO()
    }

    override fun getPath(uri: URI): Path {
        TODO()
    }



    override fun newDirectoryStream(
        dir: Path,
        filter: Filter<in Path>
    ): DirectoryStream<Path> = sunProvider.get().newDirectoryStream(dir.toUnixPath(), filter)


    override fun delete(path: Path) = sunProvider.get().delete(path.toUnixPath())

    override fun copy(
        source: Path,
        target: Path,
        vararg options: CopyOption
    ) = sunProvider.get().copy(source.toUnixPath(), target.toUnixPath(), *options)

    override fun move(
        source: Path,
        target: Path,
        vararg options: CopyOption
    ) = sunProvider.get().move(source.toUnixPath(), target.toUnixPath(), *options)

    override fun isSameFile(
        path: Path,
        path2: Path
    ): Boolean {
        TODO()
    }

    override fun isHidden(path: Path?): Boolean {
        TODO()
    }

    override fun getFileStore(path: Path?): FileStore {
        TODO()
    }






    override fun setAttribute(
        path: Path,
        attribute: String,
        value: Any,
        vararg options: LinkOption
    ) = sunProvider.get().setAttribute(path, attribute, value, *options)
}

abstract class MyUnixFileSystemProvider : FileSystemProvider() {

    final override fun createDirectory(
        dir: Path,
        vararg attrs: FileAttribute<*>
    ) = sunProvider.get().createDirectory(dir.toUnixPath(), *attrs)

    final override fun newFileChannel(
        path: Path,
        options: MutableSet<out OpenOption>,
        vararg attrs: FileAttribute<*>
    ): FileChannel = sunProvider.get().newFileChannel(path.toUnixPath(), options, *attrs)


    final override fun <A : BasicFileAttributes> readAttributes(
        path: Path,
        type: Class<A>,
        vararg options: LinkOption
    ): A = sunProvider.get().readAttributes(path.toUnixPath(), type, *options)

    final override fun readAttributes(
        path: Path,
        attributes: String,
        vararg options: LinkOption
    ): MutableMap<String, Any> = sunProvider.get().readAttributes(path.toUnixPath(), attributes, *options)


    final override fun checkAccess(
        path: Path,
        vararg modes: AccessMode
    ) = sunProvider.get().checkAccess(path.toUnixPath(), *modes)

    final override fun newByteChannel(
        path: Path,
        options: MutableSet<out OpenOption>,
        vararg attrs: FileAttribute<*>
    ): SeekableByteChannel = sunProvider.get().newByteChannel(path.toUnixPath(), options, *attrs)

    protected val sunProvider: RecalculatingWeakReference<FileSystemProvider> by lazy {
        RecalculatingWeakReference {
            sunSystem.get().also {
                requireEquals(it::class.simpleName, expectedFileSystemClassName)
            }.provider()
        }
    }
    abstract val expectedFileSystemClassName: String
    abstract val mattFileSystem: FileSystem

    @JavaMayReturnNull
    final override fun <V : FileAttributeView> getFileAttributeView(
        path: Path,
        type: Class<V>,
        vararg options: LinkOption
    ): V? = sunProvider.get().getFileAttributeView(path.toUnixPath(), type, *options)

    protected fun Path.toUnixPath(): Path {
        val theSunSystem = sunSystem.get()
        val theNameCount = nameCount
        check(theNameCount >= 0)
        val (rawUnixPathFirst, rawUnixPathMore) =
            run {
                val (rawFirst, rawMore) =
                    when (theNameCount) {
                        0    -> {
                            theSunSystem.separator to emptyArray()
                        }
                        1    -> {
                            ((if (isAbsolute) theSunSystem.separator else "") + name) to emptyArray()
                        }
                        else -> {
                            val itr = iterator()
                            (if (isAbsolute) theSunSystem.separator else "") + itr.next().singleNameString to
                                Array(
                                    theNameCount - 1
                                ) {
                                    itr.next().singleNameString
                                }
                        }
                    }



                val fixedPath =
                    mattFileSystem.constructFilePath(
                        arrayOf(rawFirst, *rawMore).joinToString(separator = theSunSystem.separator)
                    ).path


                val removedFirstSep = if (fixedPath.startsWith(theSunSystem.separator)) theSunSystem.separator else ""
                val split = fixedPath.removePrefix(theSunSystem.separator).split(theSunSystem.separator)

                (removedFirstSep + split[0]) to split.subList(1).toTypedArray()
            }

        val unixPath = theSunSystem.getPath(rawUnixPathFirst, *rawUnixPathMore)
        return unixPath
    }
}

private val Path.singleNameString: String get() {
    check(nameCount == 1)
    return name
}

private val sunSystem: RecalculatingWeakReference<java.nio.file.FileSystem> by lazy {
    RecalculatingWeakReference {
        FileSystems.getDefault()
    }
}


class RecalculatingWeakReference<T: Any>(private val recalcSupplier: () -> T) {
    private var weakRef: WeakReference<T>

    init {
        weakRef = WeakReference(recalcSupplier())
    }

    fun get(): T {
        var value = weakRef.get()
        if (value == null) {
            value = recalcSupplier()
            weakRef = WeakReference(value)
        }
        return value
    }
}
