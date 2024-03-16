@file:[JavaIoFileIsOk JvmName("FileJvmAndroidKt")]

package matt.file

import matt.collect.itr.subList
import matt.collect.itr.toFakeMutableIterator
import matt.file.common.FsFileImpl
import matt.file.common.IoFile
import matt.file.construct.mFile
import matt.file.construct.toMFile
import matt.file.ext.ExtensionSet
import matt.file.ext.FileExtension
import matt.file.ext.j.hasAnyExtension
import matt.file.ext.j.hasExtension
import matt.file.ext.j.mkparents
import matt.file.ext.j.walkTopDown
import matt.file.ext.j.writableForEveryone
import matt.file.filesystem.toMyJFileSystem
import matt.file.filesystem.toSunFileSystem
import matt.file.thismachine.thisMachine
import matt.io.ByteArrayExternalSource
import matt.io.LoadResult
import matt.io.NotFound
import matt.io.Success
import matt.lang.anno.EnforcedMin
import matt.lang.anno.bound.IoBound
import matt.lang.anno.ok.JavaIoFileIsOk
import matt.lang.common.If
import matt.lang.file.deleteOnExit
import matt.lang.file.toJFile
import matt.lang.fnf.runCatchingTrulyNotFound
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.AnyResolvableFilePath
import matt.lang.model.file.CaseSensitivity.CaseInSensitive
import matt.lang.model.file.CaseSensitivity.CaseSensitive
import matt.lang.model.file.CaseSensitivityAwareFilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.MacDefaultFileSystem
import matt.lang.model.file.constructFilePath
import matt.model.code.sys.LinuxFileSystem
import matt.model.data.byte.ByteSize
import matt.model.obj.path.PathLike
import matt.model.obj.text.MightExistAndWritableText
import matt.model.obj.text.WritableFile
import matt.prim.str.lower
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.io.RandomAccessFile
import java.net.URI
import java.nio.channels.FileChannel
import java.nio.file.DirectoryNotEmptyException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption
import java.nio.file.WatchEvent.Kind
import java.nio.file.WatchEvent.Modifier
import java.nio.file.WatchKey
import java.nio.file.WatchService
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.appendText
import kotlin.io.path.bufferedReader
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString
import kotlin.io.path.readBytes
import kotlin.io.path.readText
import kotlin.io.path.writeBytes
import kotlin.io.path.writeText
import kotlin.streams.asSequence

typealias JvmMFile = JioFile

internal const val GUESS_FS_WARNING =
    "I need to consider that the filesystem of files might change depending if they are on an external device. I have experience a mac external drive having a different case sensitivity and I have also heard it happens with android SD cards a lot!"



fun AnyFsFile.toJioFile() = JioFile(this)

fun File.toMacJioFile() = macJioFile(this)
fun AnyResolvableFilePath.toMacJioFile() = macJioFile(this)
fun macJioFile(path: String): JioFile = mFile(path, MacDefaultFileSystem).toJioFile()
fun linuxJioFile(path: String): JioFile = mFile(path, LinuxFileSystem).toJioFile()
fun macJioFile(file: File): JioFile = mFile(file, MacDefaultFileSystem).toJioFile()
fun macJioFile(path: AnyResolvableFilePath): JioFile = mFile(path.path, MacDefaultFileSystem).toJioFile()
context(FileSystem)
fun jioFile(path: String) = mFile(path).toJioFile()

fun createDyingTempFile(withText: String? = null): AnyFsFile {
    val f = JioFile.createTempFile("dying", ".temp")
    f.deleteOnExit()
    if (withText != null) {
        f.text = withText
    }
    return f
}



class JioFile(
    path: CaseSensitivityAwareFilePath,
    fileSystem: FileSystem
) : FsFileImpl<JioFile>(path, fileSystem),
    matt.model.obj.stream.Streamable,
    MightExistAndWritableText,
    IoFile<JioFile>,
    Appendable,
    PathLike<JvmMFile>,
    WritableFile<JioFile>,
    ByteArrayExternalSource,
    Path {



    fun lastModified() = (this as Path).getLastModifiedTime()

    fun renameTo(dest: JioFile) = toJFile().renameTo(dest.toJFile())
    fun setWritable(b: Boolean) = toJFile().setWritable(b)
    val isDirectory get() = isDir()
    override fun constructSameType(
        convertedFsFilePath: CaseSensitivityAwareFilePath,
        newFileSystem: FileSystem
    ): JioFile = JioFile(convertedFsFilePath, newFileSystem)




    fun lines() = bufferedReader().lines().asSequence()

    override fun resolveNames(names: List<String>): JioFile {

        var f = this
        names.forEach {
            f = f[it]
        }

        return f
    }

    operator fun plus(c: AnyFsFile) = get(c.path)

    override fun relativeNamesFrom(other: JioFile): List<String> = relativeTo(other).path.split(separator).filter { it.isNotBlank() }

    fun relativeNamesFromWithUserFiles(other: JioFile): List<String> =
        relativeToWithUserFiles(other).path.split(separator).filter {
            it.isNotBlank()
        }

    override fun append(c: Char): java.lang.Appendable {
        appendText(c.toString())
        return this
    }

    override fun append(csq: CharSequence?): java.lang.Appendable {
        appendText(csq!!.toString())
        return this
    }

    override fun append(
        csq: CharSequence?,
        start: Int,
        end: Int
    ): java.lang.Appendable = append(csq!!.subSequence(start, end))


    val userFile = File(this.path)

    override fun inputStream() = userFile.inputStream()

    override fun isDir(): Boolean = (this as Path).isDirectory()

    override fun size(): ByteSize = ByteSize(Files.size(this))

    constructor(
        file: File,
        fileSystem: FileSystem
    ) : this(file.toMFile(fileSystem))

    constructor(
        parent: String,
        child: String,
        fileSystem: FileSystem
    ) : this(File(parent, child), fileSystem)

    constructor(
        parent: JioFile,
        child: String
    ) : this(parent.path, child, parent.myFileSystem)

    constructor(
        uri: URI,
        fileSystem: FileSystem
    ) : this(File(uri), fileSystem)

    constructor(
        fsFile: AnyFsFile
    ) : this(fsFile.fsFilePath, fsFile.myFileSystem)

    companion object {


        val separatorChar by lazy { File.separatorChar }
        val separator: String by lazy { File.separator }
        val unixSeparator: String = MacDefaultFileSystem.separator

        /*these are colons meant to delimit lists of files*/
        @Deprecated("I can't think of any use case of this other than to cause bugs")
        val pathSeparatorChar by lazy { File.pathSeparatorChar }

        /*these are colons meant to delimit lists of files*/
        @Deprecated("I can't think of any use case of this other than to cause bugs")
        val pathSeparator: String by lazy { File.pathSeparator }


        fun listRoots() = File.listRoots().map { mFile(it.path, thisMachine.fileSystemFor(it.path)) }.toTypedArray()

        fun createTempFile(
            prefix: String,
            suffix: String?,
            directory: JioFile?
        ) = run {
            val f = File.createTempFile(prefix, suffix, directory?.toJFile())
            with(thisMachine.fileSystemFor(f.path)) { mFile(f) }
        }

        fun createTempFile(
            prefix: String,
            suffix: String?
        ) = run {
            val f = File.createTempFile(prefix, suffix)
            with(thisMachine.fileSystemFor(f.path)) { mFile(f) }
        }
    }

    val identityGetter by lazy {
        when (fileSystem.caseSensitivity) {
            CaseSensitive   -> {
                { s: String -> s }
            }

            CaseInSensitive -> {
                { s: String ->
                    s.lower()
                }
            }
        }
    }

    fun readChannel() = FileChannel.open(this)
    fun oldRandomAccessReadWriteChannel(): FileChannel = RandomAccessFile(toJFile(), "rw").channel
    fun openWriteChannel(
        createIfDoesNotExist: Boolean = false,
        truncateExisting: Boolean = false
    ) = FileChannel.open(
        this,
        StandardOpenOption.WRITE,
        *If(createIfDoesNotExist).then(StandardOpenOption.CREATE),
        *If(truncateExisting).then(StandardOpenOption.TRUNCATE_EXISTING)
    )


    val fName: String get() = name


    /*    override fun getAbsoluteFile(): FsFile {
            return super.getAbsoluteFile().toMFile(caseSensitivity = caseSensitivity)
        }*/



    fun siblings(): List<JioFile> = parent!!.listFiles()!!.filter { it != this }

    fun listFiles(): Array<JioFile>? =
        with(myFileSystem) {
            toJFile().listFiles()?.map { it.toMFile() }?.toTypedArray()
        }

    fun listFiles(filenameFilter: FilenameFilter?): Array<JioFile>? =
        with(myFileSystem) {
            return toJFile().listFiles(filenameFilter)?.map { it.toMFile() }?.toTypedArray()
        }

    fun listFiles(fileFilter: FileFilter?): Array<JioFile>? =
        with(myFileSystem) {
            return toJFile().listFiles(fileFilter)?.map { it.toMFile() }?.toTypedArray()
        }


    override fun listFilesAsList() = listFiles()?.toList()

    /*must remain lower since in ext.kt i look here for matching with a astring

    MUST REMAIN LAZY because for android osFun contains a "network" op that blocks the main thread and throws an error*/


    val idFile by lazy { File(identityGetter(this.path)) }


    operator fun compareTo(other: File): Int = idFile.compareTo((other.toMFile(myFileSystem).idFile))

    override fun load(): LoadResult<ByteArray> =
        runCatchingTrulyNotFound {
            Success(bytes)
        }.getOrElse {
            NotFound(it)
        }


    /*MUST KEEP THESE METHODS HERE AND NOT AS EXTENSIONS IN ORDER TO ROBUSTLY OVERRIDE KOTLIN.STDLIB'S DEFAULT FILE EXTENSIONS. OTHERWISE, I'D HAVE TO MICROMANAGE MY IMPORTS TO MAKE SURE I'M IMPORTING THE CORRECT EXTENSIONS*/


    override fun relativeTo(other: JioFile): JioFile = super.relativeTo(other).toJioFile()
    fun relativeToWithUserFiles(base: JioFile): JioFile = super.relativeTo(base).toJioFile()




    fun resolveSibling(relative: JioFile): JioFile = super<FsFileImpl>.resolveSibling(relative.path).toJioFile()


    @CopiedDefaultPathMethodExistsForDesktopButNotAndroidItSeems
    override fun resolveSibling(other: Path): Path {
        val parent = getParent()
        return if ((parent == null)) other else parent.resolve(other)
    }


    override fun resolveSibling(other: String): JioFile = super<FsFileImpl>.resolveSibling(other)







    override fun compareTo(other: Path): Int {
        TODO("Not yet implemented")
    }

    override fun register(
        watcher: WatchService,
        events: Array<out Kind<*>>,
        vararg modifiers: Modifier
    ): WatchKey {
        TODO()
    }

    @CopiedDefaultPathMethodExistsForDesktopButNotAndroidItSeems
    override fun register(
        watcher: WatchService,
        vararg events: Kind<*>
    ): WatchKey = register(watcher, events)

    private val jFileSystem by lazy {
        fileSystem.toMyJFileSystem()
    }
    private val sunFileSystem by lazy {
        fileSystem.toSunFileSystem()
    }

    override fun getFileSystem(): java.nio.file.FileSystem = jFileSystem


    override fun isAbsolute(): Boolean = isAbs

    override fun getRoot(): Path {
        TODO("Not yet implemented")
    }

    override fun getFileName(): Path =
        constructSameType(
            myFileSystem.constructFilePath(fName),
            myFileSystem
        )


    override fun getName(index: Int): Path =
        constructSameType(
            myFileSystem.constructFilePath(names[index]),
            myFileSystem
        )


    override fun getParent(): Path? = parent

    override fun getNameCount(): Int = names.size






    override fun subpath(
        beginIndex: Int,
        endIndex: Int
    ): Path {
        TODO("Not yet implemented")
    }

    override fun startsWith(other: Path): Boolean {



        if (other.fileSystem != fileSystem) return false

        if (other.nameCount > nameCount) return false

        if (other.nameCount == 0) TODO("not sure how to handle other having no names")

        if (!isAbsolute) TODO("not sure how to handle not being absolute")
        if (!other.isAbsolute) TODO("not sure how to handle other not being absolute")

        (0 until other.nameCount).forEach {
            if (getName(it) != other.getName(it)) return false
        }

        return true
    }

    @CopiedDefaultPathMethodExistsForDesktopButNotAndroidItSeems
    override fun endsWith(other: String): Boolean = endsWith(fileSystem.getPath(other))

    override fun endsWith(other: Path): Boolean {

        check(other is JioFile)
        if (other.myFileSystem != myFileSystem) return false
        if (other.isAbsolute) {
            if (!isAbsolute) return false
            return this == other
        } else {


            val otherNameCount = other.nameCount
            check(otherNameCount >= 1)
            if (other.isAbsolute) {
                return other == this
            } else {
                val myNameCount = nameCount
                if (otherNameCount > myNameCount) return false
                val skip = myNameCount - otherNameCount
                return names.subList(skip, names.size) == other.names
            }
        }
    }

    override fun normalize(): Path {
        TODO("Not yet implemented")
    }

    override fun resolve(other: Path): Path =
        when (other) {
            is JioFile -> resolve(other as AnyResolvableFilePath)
            else -> {
                check(!other.isAbsolute)
                resolve(other.pathString)
            }
        }

    override fun resolve(other: String): JioFile = super<FsFileImpl>.resolve(other)

    override fun startsWith(other: String): Boolean {
        TODO()
    }


    /*

    Partially verified in [[CommonJvmAndroidFileTests#pathEqualsResolvedRelativized]]

     */
    override fun relativize(other: Path): Path {




        if (other ==  this) {
            /*

            According to docs for [[java.nio.file.Path#relativize]], we are supposed to return an empty path in this case


             "A Path is considered to be an empty path if it consists solely of one name element that is empty. Accessing a file using an empty path is equivalent to accessing the default directory of the file system"
                 - [[java.nio.file.Path]]


             */
            return sunFileSystem.getPath("")
        }




        if (other is JioFile) {
            if (other.myFileSystem !=
                myFileSystem
            ) TODO("not sure how to handle other having different file system (fs=$myFileSystem,other.fs=${other.myFileSystem}) yet")
        } else {
            if (other.fileSystem !=
                sunFileSystem
            ) TODO("not sure how to handle other having different SUN file system (fs=$sunFileSystem,other.fs=${other.fileSystem}) yet")
        }



        if (!isAbsolute) TODO("handle this not being absolute")
        if (!other.isAbsolute) TODO("Handle other not being absolute")

        val otherForCaseSafeComparison =
            if (other is JioFile) other
            else other.toMFile(myFileSystem)

        if (!otherForCaseSafeComparison.startsWith(this)) TODO("unsure how to handle this. $other does not start with $this")
        if (other.nameCount <= nameCount) TODO("unsure how to handle this. ${other.nameCount} is less than or equal to $nameCount")



        val r: Path =
            constructSameType(
                myFileSystem.constructFilePath(
                    List(other.nameCount) { other.getName(it) }.subList(names.size).joinToString(separator)
                ),
                myFileSystem
            )

        return r
    }

    override fun toUri(): URI = toJFile().toURI()

    override fun toAbsolutePath(): Path {
        TODO("Not yet implemented")
    }

    override fun toRealPath(vararg options: LinkOption?): Path {
        TODO("Not yet implemented")
    }


    fun setExecutable(b: Boolean) = toJFile().setExecutable(b)
    override var text
        get() = (this as Path).readText()
        set(v) {

            mkparents()
            (this as Path).writeText(v)
        }

    override var bytes: ByteArray
        get() = readBytes()
        set(value) {
            mkparents()
            writeBytes(value)
        }


    fun createNewFile() = toJFile().createNewFile()
    fun createNewFile(child: String): JioFile =
        resolve(child).apply {
            createNewFile()
        }.toJioFile()

    fun createNewFile(
        child: String,
        text: String
    ): JioFile = createNewFile(child).also { it.text = text }


    fun write(
        s: String,
        mkParents: Boolean = true
    ) {
        if (mkParents) mkparents()
        writeText(s)
    }


    @OptIn(ExperimentalPathApi::class)
    override fun deleteIfExists() {
        if (exists()) {
            if (isDir()) {
                deleteRecursively()
            } else {
                delete()
            }
        }
    }

    fun delete() = toJFile().delete()

    override fun exists() = (this as Path).exists()

    @IoBound
    fun copyInto(
        newParent: JioFile,
        overwrite: Boolean  = DEFAULT_COPY_OVERWRITE
    ) = copyTo(newParent[name], overwrite = overwrite)

    /*

verified in (disabled) [[CopySpeedTests]] that java.nio is faster than any java.io, faster than cp

Locally on my mac, this is IO-bound. A single thread can use a portion of its computational power to facilitate the maximum transfer rate on my Macbook Pro's SSD. Therefore, multiple threads are not needed.

*/



    @OptIn(ExperimentalPathApi::class)
    @IoBound
    fun copyTo(
        target: JioFile,
        overwrite: Boolean = DEFAULT_COPY_OVERWRITE
    ) {
        val myTarget = target.toJioFile()
        if (isDirectory) {
            var atRoot = true
            walkTopDown().forEach {
                val subTarget =
                    when (it) {
                        this -> myTarget
                        else -> myTarget [it.relativeTo(this)]
                    }
                try {
                    Files.copy(
                        it,
                        subTarget,
                        *If(overwrite).then(REPLACE_EXISTING)
                    )
                } catch (e: DirectoryNotEmptyException) {
                    if (!atRoot) throw e
                    subTarget.deleteRecursively()
                    Files.copy(it, subTarget) /*create empty directory*/
                }
                atRoot = false
            }
        } else {
            Files.copy(
                this,
                myTarget,
                *If(overwrite).then(REPLACE_EXISTING)
            )
        }
    }





    override fun mkdirs(): Boolean = toJFile().mkdirs()

    override fun mkdir() {
        toJFile().mkdir()
    }


    /*will prevent accidental edits of generated code (both me and IntelliJ are making accidental edits)*/
    fun ifDifferentForceWriteThenMakeReadOnlyForEveryone(newText: String) {
        val exist = exists()
        if (!exist || text != newText) {
            if (exist) writableForEveryone = true
            mkparents()
            text = newText
        }
        writableForEveryone = false
    }

    fun canExecute(): Boolean = toJFile().canExecute()

    @CopiedDefaultPathMethodExistsForDesktopButNotAndroidItSeemsButAlsoModified
    override fun toFile(): File {
        val default = FileSystems.getDefault()
        if (sunFileSystem === default) {
            return File(toString())
        } else {
            throw UnsupportedOperationException(
                "Path $this not associated with default file system ($sunFileSystem !== $default)"
            )
        }
    }

    @CopiedDefaultPathMethodExistsForDesktopButNotAndroidItSeems
    override fun iterator(): MutableIterator<Path> {
        return object : Iterator<Path> {
            private var i = 0

            override fun hasNext(): Boolean = (i < nameCount)

            override fun next(): Path {
                if (i < nameCount) {
                    val result = getName(i)
                    i++
                    return result
                } else {
                    throw NoSuchElementException()
                }
            }
        }.toFakeMutableIterator()
    }
}


fun Array<JioFile>.withExtension(ext: FileExtension) = filter { it.hasExtension(ext) }
fun Iterable<JioFile>.withExtension(ext: FileExtension) = filter { it.hasExtension(ext) }
fun Set<JioFile>.withExtension(ext: FileExtension) = filterTo(mutableSetOf()) { it.hasExtension(ext) }
fun Sequence<JioFile>.withExtension(ext: FileExtension) = filter { it.hasExtension(ext) }


@EnforcedMin
fun Array<JioFile>.withAnyExtension(
    ext: FileExtension,
    vararg exts: FileExtension
) = filter { it.hasAnyExtension(ext, *exts) }

@EnforcedMin
fun Iterable<JioFile>.withAnyExtension(
    ext: FileExtension,
    vararg exts: FileExtension
) = filter { it.hasAnyExtension(ext, *exts) }

@EnforcedMin
fun Set<JioFile>.withAnyExtension(
    ext: FileExtension,
    vararg exts: FileExtension
) = filterTo(mutableSetOf()) { it.hasAnyExtension(ext, *exts) }

@EnforcedMin
fun Sequence<JioFile>.withAnyExtension(
    ext: FileExtension,
    vararg exts: FileExtension
) = filter { it.hasAnyExtension(ext, *exts) }


@EnforcedMin
fun Array<JioFile>.withAnyExtension(
    exts: ExtensionSet
) = filter { it.hasAnyExtension(exts) }

@EnforcedMin
fun Iterable<JioFile>.withAnyExtension(
    exts: ExtensionSet
) = filter { it.hasAnyExtension(exts) }

@EnforcedMin
fun Set<JioFile>.withAnyExtension(
    exts: ExtensionSet
) = filterTo(mutableSetOf()) { it.hasAnyExtension(exts) }

@EnforcedMin
fun Sequence<JioFile>.withAnyExtension(
    exts: ExtensionSet
) = filter { it.hasAnyExtension(exts) }




private annotation class CopiedDefaultPathMethodExistsForDesktopButNotAndroidItSeems
private annotation class CopiedDefaultPathMethodExistsForDesktopButNotAndroidItSeemsButAlsoModified

const val DEFAULT_COPY_OVERWRITE = false
