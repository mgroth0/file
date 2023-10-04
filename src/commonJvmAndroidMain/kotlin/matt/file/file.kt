@file:JavaIoFileIsOk
@file:JvmName("FileJvmAndroidKt")

package matt.file

import matt.file.construct.mFile
import matt.file.construct.toMFile
import matt.file.ext.ExtensionSet
import matt.file.ext.FileExtension
import matt.file.ext.hasAnyExtension
import matt.file.ext.hasExtension
import matt.file.ext.mkparents
import matt.file.ext.writableForEveryone
import matt.file.thismachine.thisMachine
import matt.io.ExternalSource
import matt.io.LoadResult
import matt.io.NotFound
import matt.io.Success
import matt.lang.anno.EnforcedMin
import matt.lang.anno.ok.JavaIoFileIsOk
import matt.lang.file.toJFile
import matt.lang.model.file.CaseSensitivity.CaseInSensitive
import matt.lang.model.file.CaseSensitivity.CaseSensitive
import matt.lang.model.file.FilePath
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFile
import matt.lang.model.file.FsFilePath
import matt.lang.model.file.MacFileSystem
import matt.model.data.byte.ByteSize
import matt.model.obj.path.PathLike
import matt.model.obj.text.MightExistAndWritableText
import matt.model.obj.text.WritableFile
import matt.prim.str.lower
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.io.FilenameFilter
import java.io.RandomAccessFile
import java.net.URI
import java.net.URL
import java.nio.channels.FileChannel
import java.nio.file.Files
import kotlin.streams.asSequence

typealias JvmMFile = JioFile

internal const val GUESS_FS_WARNING =
    "I need to consider that the filesystem of files might change depending if they are on an external device. I have experience a mac external drive having a different case sensitivity and I have also heard it happens with android SD cards a lot!"


actual fun FsFile.toIoFile(): IoFile = toJioFile()
fun FsFile.toJioFile() = JioFile(this)

fun File.toMacJioFile() = macJioFile(this)
fun FilePath.toMacJioFile() = macJioFile(this)
fun macJioFile(path: String): JioFile = mFile(path, MacFileSystem).toJioFile()
fun macJioFile(file: File): JioFile = mFile(file, MacFileSystem).toJioFile()
fun macJioFile(path: FilePath): JioFile = mFile(path.path, MacFileSystem).toJioFile()

class JioFile(
    path: FsFilePath,
    fileSystem: FileSystem
) : FsFileImpl(path, fileSystem),
    matt.model.obj.stream.Streamable,
    MightExistAndWritableText,
    IoFile,
    Appendable,
    PathLike<JvmMFile>,
    WritableFile,
    ExternalSource<ByteArray> {


    fun lastModified() = toJFile().lastModified()

    fun renameTo(dest: JioFile) = toJFile().renameTo(dest.toJFile())
    val nameWithoutExtension get() = toJFile().nameWithoutExtension
    fun setWritable(b: Boolean) = toJFile().setWritable(b)
    inline fun <T> useLines(block: (Sequence<String>) -> T) = toJFile().useLines(block = block)
    fun toURI() = toJFile().toURI()
    val isDirectory get() = isDir()
    fun deleteRecursively() = toJFile().deleteRecursively()
    fun list() = toJFile().list()
    fun canExecute() = toJFile().canExecute()
    fun delete() = toJFile().delete()
    fun outputStream() = toJFile().outputStream()
    fun readLines() = toJFile().readLines()
    fun bufferedWriter() = toJFile().bufferedWriter()

    override val parent: JioFile? get() = super.parent?.let { JioFile(fsFile = it) }

    //    override val parentFile get() = parent
    override val name: String
        get() = toJFile().name

    fun lines() = bufferedReader().lines().asSequence()
    fun bufferedReader() = toJFile().bufferedReader()
    fun reader() = toJFile().reader()
    override val parentFile get() = parent

    override fun resolveNames(names: List<String>): JioFile {

//        TestCommonThreadObject
//        TestCommonJvmAndroidThreadObject
//        TestAndroidThreadObject
//        TestJvmThreadObject

        var f = this
        names.forEach {
            f = f[it]
        }

        return f
    }

    override operator fun get(c: FilePath) = get(c.path)
    override fun resolve(c: FilePath) = resolve(c.path)
    override operator fun plus(c: FilePath) = get(c.path)
    operator fun plus(c: FsFile) = get(c.path)

    override fun relativeNamesFrom(other: JioFile): List<String> {
        return this.relativeTo(other).path.split(separator).filter { it.isNotBlank() }
    }

    fun relativeNamesFromWithUserFiles(other: JioFile): List<String> {
        return this.relativeToWithUserFiles(other).path.split(separator).filter { it.isNotBlank() }
    }

    override fun append(c: Char): java.lang.Appendable {
        this.toJFile().appendText(c.toString())
        return this
    }

    override fun append(csq: CharSequence?): java.lang.Appendable {
        this.toJFile().appendText(csq!!.toString())
        return this
    }

    override fun append(
        csq: CharSequence?,
        start: Int,
        end: Int
    ): java.lang.Appendable {
        return append(csq!!.subSequence(start, end))
    }


    override val filePath: String get() = super<FsFileImpl>.filePath
    val userFile = File(this.cpath)

    override fun inputStream() = userFile.inputStream()

    override fun isDir(): Boolean {
        return toJFile().isDirectory()
    }

    override fun size(): ByteSize {
        return ByteSize(Files.size(toJFile().toPath()))
    }

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
    ) : this(parent.cpath, child, parent.fileSystem)

    constructor(
        uri: URI,
        fileSystem: FileSystem
    ) : this(File(uri), fileSystem)

    constructor(
        fsFile: FsFile
    ) : this(fsFile.fsFilePath, fsFile.fileSystem)

    companion object {


        val separatorChar by lazy { File.separatorChar }
        val separator: String by lazy { File.separator }
        const val unixSeparator: String = "/"

        /*these are colons meant to delimit lists of files*/
        @Deprecated("I can't think of any use case of this other than to cause bugs")
        val pathSeparatorChar by lazy { File.pathSeparatorChar }

        /*these are colons meant to delimit lists of files*/
        @Deprecated("I can't think of any use case of this other than to cause bugs")
        val pathSeparator: String by lazy { File.pathSeparator }


        fun listRoots() = File.listRoots().map { mFile(it.path, thisMachine.fileSystem) }.toTypedArray()

        fun createTempFile(
            prefix: String,
            suffix: String?,
            directory: JioFile?
        ) = with(thisMachine.fileSystem) { mFile(File.createTempFile(prefix, suffix, directory?.toJFile())) }

        fun createTempFile(
            prefix: String,
            suffix: String?
        ) = with(thisMachine.fileSystem) { mFile(File.createTempFile(prefix, suffix)) }

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

    fun readChannel() = FileChannel.open(this.toJFile().toPath())
    fun writeChannel(): FileChannel = RandomAccessFile(toJFile(), "rw").channel

    val fName: String get() = name


//    override fun getParentFile(): FsFile? {
//        return parent
//    }

    /*    override fun getAbsoluteFile(): FsFile {
            return super.getAbsoluteFile().toMFile(caseSensitivity = caseSensitivity)
        }*/


    fun siblings(): List<JioFile> {
        return parent!!.listFiles()!!.filter { it != this }
    }

    fun listFiles(): Array<JioFile>? = with(fileSystem) {
        toJFile().listFiles()?.map { it.toMFile() }?.toTypedArray()
    }

    fun listFiles(fiilenameFilter: FilenameFilter?): Array<JioFile>? = with(fileSystem) {
        return toJFile().listFiles(fiilenameFilter)?.map { it.toMFile() }?.toTypedArray()
    }

    fun listFiles(fileFilter: FileFilter?): Array<JioFile>? = with(fileSystem) {
        return toJFile().listFiles(fileFilter)?.map { it.toMFile() }?.toTypedArray()
    }


    override fun listFilesAsList() = listFiles()?.toList()

    /*must remain lower since in ext.kt i look here for matching with a astring*//*MUST REMAIN LAZY because for android osFun contains a "network" op that blocks the main thread and throws an error*/
    val idFile by lazy { File(identityGetter(filePath)) }


    operator fun compareTo(other: File): Int = idFile.compareTo((other.toMFile(fileSystem).idFile))

    override fun load(): LoadResult<ByteArray> {
        return try {
            Success(bytes)
        } catch (e: FileNotFoundException) {
            NotFound(e)
        }
    }

//    override fun equals(other: Any?): Boolean {
//
//        return if (other is FsFile) {
//            (other.fileSystem == fileSystem
//                    && other.path == path)
//        } else false

//        return if (other is File) {
//            requireIs<FsFile>(other) {
//                "$other is a ${other::class} yes, but its not an MFile"
//            }
//            idFile == other.idFile
//        } else false


//    }

//    override fun hashCode() = fileSystem.hashCode() + path.hashCode()


    /*MUST KEEP THESE METHODS HERE AND NOT AS EXTENSIONS IN ORDER TO ROBUSTLY OVERRIDE KOTLIN.STDLIB'S DEFAULT FILE EXTENSIONS. OTHERWISE, I'D HAVE TO MICROMANAGE MY IMPORTS TO MAKE SURE I'M IMPORTING THE CORRECT EXTENSIONS*/


    fun relativeTo(base: JioFile): JioFile = super.relativeTo(base).toJioFile()
    fun relativeToWithUserFiles(base: JioFile): JioFile = super.relativeTo(base).toJioFile()
    fun resolve(other: JioFile): JioFile = super<FsFileImpl>.resolve(other).toJioFile()
    override fun resolve(other: String) = super<FsFileImpl>.resolve(other).toJioFile()
    fun resolveSibling(relative: JioFile): JioFile = super<FsFileImpl>.resolveSibling(relative.path).toJioFile()
    override fun resolveSibling(other: String): JioFile = super<FsFileImpl>.resolveSibling(other).toJioFile()


    fun setExecutable(b: Boolean) = toJFile().setExecutable(b)
    override var text
        get() = toJFile().readText()
        set(v) {

            mkparents()
            toJFile().writeText(v)
        }

    fun readBytes() = toJFile().readBytes()
    fun writeBytes(value: ByteArray) = toJFile().writeBytes(value)
    override var bytes: ByteArray
        get() = toJFile().readBytes()
        set(value) {
            mkparents()
            toJFile().writeBytes(value)
        }


    fun createNewFile() = toJFile().createNewFile()
    fun createNewFile(child: String): JioFile = resolve(child).apply {
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


    override fun deleteIfExists() {
        if (exists()) {
            if (isDir()) {
                toJFile().deleteRecursively()
            } else {
                toJFile().delete()
            }
        }
    }

    override fun exists(): Boolean {
        return toJFile().exists()
    }


    final override operator fun get(item: String): JioFile {
        return resolve(item)
    }

    //  fun toJavaIOFile() = java.io.File(user)

    operator fun get(item: Char): JioFile {
        return resolve(item.toString())
    }

    override operator fun plus(other: String): JioFile {
        return resolve(other)
    }

    //  fun onModifyRecursive(op: ()->Unit) {
    //
    //	val watchService: WatchService = toPath().fileSystem.newWatchService()
    //
    //	// register all subfolders
    //	Files.walkFileTree(this.toPath(), object: SimpleFileVisitor<Path>() {
    //	  @Throws(IOException::class) override fun preVisitDirectory(
    //		dir: Path,
    //		attrs: BasicFileAttributes
    //	  ): FileVisitResult {
    //		dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
    //		return FileVisitResult.CONTINUE
    //	  }
    //	})
    //
    //	watchService.poll()
    //	watchService.poll(100, MILLISECONDS)
    //	val e = watchService.take()
    //	e.pollEvents().forEach {
    //	  it.context()
    //	}
    //
    //  }


    fun copyTo(
        target: JioFile,
        overwrite: Boolean = false,
        bufferSize: Int = DEFAULT_BUFFER_SIZE
    ): JioFile = with(fileSystem) { userFile.copyTo(target.toJFile(), overwrite, bufferSize).toMFile() }

    override fun mkdirs(): Boolean {
        return toJFile().mkdirs()
    }

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


//    override val partSep = SEP


}

//internal actual val SEP = matt.file.JioFile.separator


//fun matt.file.JioFile.idFolder() = object : IDFolder {
//    override val idFile: File
//        get() = this@idFolder.idFile
//    override val fName: String
//        get() = this@idFolder.fName
//    override val filePath: String
//        get() = this@idFolder.filePath
//    override val partSep: String
//        get() = this@idFolder.partSep
//
//    override fun isDir() = this@idFolder.isDir()
//
//    override fun toString(): String {
//        return "[(IDFolder) ${this@idFolder}]"
//    }
//
//}

interface URLLike {
    fun toJavaURL(): URL
    fun toJavaURI(): URI
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

expect val guessRuntimeFileSystem: FileSystem