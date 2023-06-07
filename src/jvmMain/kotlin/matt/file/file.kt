@file:JavaIoFileIsOk
@file:JvmName("FileJvmKt")

package matt.file

import matt.collect.itr.filterNotNull
import matt.collect.itr.recurse.recurse
import matt.collect.itr.search
import matt.file.CaseSensitivity.CaseInSensitive
import matt.file.CaseSensitivity.CaseSensitive
import matt.file.construct.mFile
import matt.file.construct.toMFile
import matt.file.ext.FileExtension
import matt.file.thismachine.thisMachine
import matt.lang.NOT_IMPLEMENTED
import matt.lang.userHome
import matt.log.NOPLogger
import matt.log.warn.warn
import matt.model.code.ok.JavaIoFileIsOk
import matt.model.code.report.Reporter
import matt.model.data.byte.ByteSize
import matt.model.data.file.IDFile
import matt.model.data.file.IDFolder
import matt.model.obj.path.PathLike
import matt.model.obj.stream.Streamable
import matt.model.obj.text.MightExistAndWritableText
import matt.model.obj.text.WritableBytes
import matt.prim.str.ensureSuffix
import matt.prim.str.lower
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.io.RandomAccessFile
import java.net.URI
import java.net.URL
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.concurrent.thread
import kotlin.reflect.KClass

actual val defaultCaseSensitivity by lazy {
    if (thisMachine.caseSensitive) CaseSensitive else CaseInSensitive
}


/*mac file, matt file, whatever*//*sadly this is necessary. Java.io.file is an absolute failure because it doesn't respect Mac OSX's case sensitivity rules
  I'm actually shocked it took me so long to figure this out*/

/*TODO: SUBCLASS IS PROBLEMATIC BECAUSE OF THE BUILTIN KOTLIN `RESOLVES` FUNCTION (can I disable or override it? maybe in unnamed package?) WHICH SECRETLY TURNS THIS BACK INTO A REGULAR FILE*//*TODO:  NOT SUBCLASSING JAVA.FILE IS PROBLEMATIC BECAUSE I NEED TONS OF BOILERPLATE SINCE THE FILE CLASS HAS SO MANY METHODS, EXTENSION METHODS, CLASSES, AND LIBRARIES IT WORKS WITH*/
actual sealed class MFile actual constructor(
    actual val userPath: String,
    val caseSensitivity: CaseSensitivity
) : File(userPath),
    CommonFile,
    Streamable,
    MightExistAndWritableText,
    WritableBytes,
    IDFile,
    Appendable,
    PathLike<MFile> {

    fun walk(direction: FileWalkDirection = FileWalkDirection.TOP_DOWN) =
        (this as File).walk(direction = direction).map {
            it.toMFile()
        }

    fun walkTopDown() = walk(direction = FileWalkDirection.TOP_DOWN)
    fun walkBottomUp() = walk(direction = FileWalkDirection.BOTTOM_UP)

    override fun resolveNames(names: List<String>): MFile {
        var f = this
        names.forEach {
            f = f[it]
        }
        return f
    }

    override fun relativeNamesFrom(other: MFile): List<String> {
        return this.relativeTo(other).path.split(SEP).filter { it.isNotBlank() }
    }

    override fun append(c: Char): java.lang.Appendable {
        this.appendText(c.toString())
        return this
    }

    override fun append(csq: CharSequence): java.lang.Appendable {
        this.appendText(csq.toString())
        return this
    }

    override fun append(csq: CharSequence, start: Int, end: Int): java.lang.Appendable {
        return append(csq.subSequence(start, end))
    }

    val url get() = toURI().toURL()
    val betterURLIGuess
        get() = "file://${
            absolutePath.replace(
                ' '.toString(),
                "%20"
            )
        }" /*the url above does not include the double slash? so IntelliJ console doesn't recognize it as a url?*/

    actual override val filePath: String get() = super.getPath()
    actual override val cpath: String = path
    val userFile = File(this.cpath)

    override fun inputStream() = userFile.inputStream()

    actual override fun isDir(): Boolean {
        return super.isDirectory()
    }

    actual final override fun toString() =
        super.toString() /*at least one java standard lib class expects File.toString() to just print the path... (Runtime.exec or something like that)*/


    constructor(file: MFile) : this(file.userPath)
    constructor(file: File) : this(file.path)
    constructor(parent: String, child: String) : this(File(parent, child))
    constructor(parent: MFile, child: String) : this(parent.cpath, child)
    constructor(uri: URI) : this(File(uri))

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

        fun listRoots() = File.listRoots().map { mFile(it) }.toTypedArray()
        fun createTempFile(prefix: String, suffix: String?, directory: MFile?) =
            mFile(File.createTempFile(prefix, suffix, directory))

        fun createTempFile(prefix: String, suffix: String?) = mFile(File.createTempFile(prefix, suffix))

    }

    val identityGetter by lazy {
        when (caseSensitivity) {
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

    fun readChannel() = FileChannel.open(this.toPath())
    fun writeChannel(): FileChannel = RandomAccessFile(this, "rw").channel

    actual override val fName: String = name


    fun createIfNecessary(defaultText: String? = null): Boolean {
        var r = false
        if (mkparents()) r = true
        if (createNewFile()) r = true
        if (defaultText != null && text.isBlank()) {
            text = defaultText
            r = true
        }
        return r
    }

    actual override fun getParentFile(): MFile? {
        return super.getParentFile()?.toMFile()
    }

    override fun getAbsoluteFile(): MFile {
        return super.getAbsoluteFile().toMFile(caseSensitivity = caseSensitivity)
    }

    fun listNonDSStoreFiles() = listFiles()?.filter { it !is DSStoreFile }

    fun siblings(): List<MFile> {
        return parentFile!!.listFiles()!!.filter { it != this }
    }

    override fun listFiles(): Array<MFile>? {
        return super.listFiles()?.map { it.toMFile(caseSensitivity = caseSensitivity) }?.toTypedArray()
    }

    override fun listFiles(fiilenameFilter: FilenameFilter?): Array<MFile>? {
        return super.listFiles(fiilenameFilter)?.map { it.toMFile(caseSensitivity = caseSensitivity) }?.toTypedArray()
    }

    override fun listFiles(fileFilter: FileFilter?): Array<MFile>? {
        return super.listFiles(fileFilter)?.map { it.toMFile(caseSensitivity = caseSensitivity) }?.toTypedArray()
    }

    fun listFilesOrEmpty() = listFiles() ?: arrayOf()
    actual fun listFilesAsList() = listFiles()?.toList()

    /*must remain lower since in ext.kt i look here for matching with a astring*/
    /*MUST REMAIN LAZY because for android osFun contains a "network" op that blocks the main thread and throws an error*/
    override val idFile by lazy { File(identityGetter(userPath)) }


    override operator fun compareTo(other: File?): Int = idFile.compareTo((other as MFile).idFile)
    override fun equals(other: Any?): Boolean {
        return if (other is File) {
            require(other is MFile) {
                "$other is a File yes, but its not an MFile"
            }
            idFile == other.idFile
        } else false
    }

    override fun hashCode() = idFile.hashCode()


    operator fun contains(other: MFile): Boolean {
        return other != this && other.search({
            takeIf {
                it == this@MFile
            }
        }, { parentFile?.toMFile() }) != null
    }


    /*MUST KEEP THESE METHODS HERE AND NOT AS EXTENSIONS IN ORDER TO ROBUSTLY OVERRIDE KOTLIN.STDLIB'S DEFAULT FILE EXTENSIONS. OTHERWISE, I'D HAVE TO MICROMANAGE MY IMPORTS TO MAKE SURE I'M IMPORTING THE CORRECT EXTENSIONS*/

    fun wildcardChildrenPath() = path.ensureSuffix(separator) + "*"

    fun relativeTo(base: MFile): MFile = idFile.relativeTo(base.idFile).toMFile(base.caseSensitivity)


    fun startsWith(other: MFile): Boolean = idFile.startsWith(other.idFile)
    fun startsWith(other: String): Boolean = idFile.startsWith(identityGetter(other))
    fun endsWith(other: MFile) = idFile.endsWith(other.idFile)
    fun endsWith(other: String): Boolean = idFile.endsWith(identityGetter(other))


    actual fun resolve(other: MFile, cls: KClass<out MFile>?): MFile =
        userFile.resolve(other).toMFile(cls = cls, caseSensitivity = caseSensitivity)

    actual override fun resolve(other: String): MFile =
        userFile.resolve(other).toMFile(caseSensitivity = caseSensitivity)


    fun resolveSibling(relative: MFile) = userFile.resolveSibling(relative).toMFile(caseSensitivity = caseSensitivity)


    fun resolveSibling(relative: String): MFile =
        userFile.resolveSibling(relative).toMFile(caseSensitivity = caseSensitivity)

    fun mkparents() = parentFile!!.mkdirs()

    fun tildeString() = toString().replace(userHome.removeSuffix(SEP), "~")

    actual override var text
        get() = readText()
        set(v) {
            mkparents()
            writeText(v)
        }

    actual override var bytes: ByteArray
        get() = readBytes()
        set(value) {
            mkparents()
            writeBytes(value)
        }


    fun isBlank() = bufferedReader().run {
        val r = read() == -1
        close()
        r
    }

    fun isImage() = extension in listOf("png", "jpg", "jpeg")

    fun append(s: String, mkdirs: Boolean = true) {
        if (mkdirs) mkparents()
        appendText(s)
    }

    fun createNewFile(child: String) = resolve(child).apply {
        createNewFile()
    }.toMFile()

    fun createNewFile(child: String, text: String) = createNewFile(child).also { it.text = text }


    fun mkdir(child: String) = resolve(child).apply {
        mkdir()
    }.toMFile().requireIsExistingFolder()

    fun mkdir(int: Int) = mkdir(int.toString())

    fun write(s: String, mkparents: Boolean = true) {
        if (mkparents) mkparents()
        writeText(s)
    }


    val abspath: String
        get() = absolutePath

    infix fun withLastNameExtension(s: String) = mFile(abspath.removeSuffix(separator) + s)


    fun moveInto(newParent: MFile, overwrite: Boolean = false): MFile {
        return (if (overwrite) Files.move(
            this.toPath(), (newParent + this.name).toPath(), StandardCopyOption.REPLACE_EXISTING
        )
        else Files.move(this.toPath(), (newParent + this.name).toPath())).toFile().toMFile()
    }

    private class IndexFolder(val f: MFile) {
        val name = f.name
        val index = name.toInt()
        operator fun plus(other: MFile) = f + other
        operator fun plus(other: String) = f + other
        fun next() = IndexFolder(f.parentFile!! + (index + 1).toString())
        fun previous() = IndexFolder(f.parentFile!! + (index - 1).toString())
    }

    fun getNextSubIndexedFileWork(
        filename: String,
        maxN: Int,
        @Suppress("UNUSED_PARAMETER") log: Reporter = NOPLogger
    ): () -> MFile {

        require(maxN > 0) {
            "maxN should be greater than 0 but it is ${maxN}"
        }
        val existingSubIndexFolds = listFiles()!!.mapNotNull { f ->
            f.name.toIntOrNull()?.let { IndexFolder(f) }
        }.sortedBy { it.index }
        val firstSubIndexFold = existingSubIndexFolds.firstOrNull()

        val nextSubIndexFold =
            if (existingSubIndexFolds.isEmpty()) IndexFolder(
                resolve("1")
            ) else existingSubIndexFolds.firstOrNull { (it + filename).doesNotExist }
                ?: existingSubIndexFolds.last().next()


        return {
            if (nextSubIndexFold.index > maxN) {
                firstSubIndexFold?.plus(filename)?.deleteRecursively()
                (existingSubIndexFolds - firstSubIndexFold).filterNotNull().sortedBy { it.index }.forEach {
                    (it + filename).takeIf { it.exists() }?.moveInto(it.previous().f)
                }
            }
            nextSubIndexFold + filename
        }

    }

    fun resRepExt(newExt: FileExtension) =
        mFile(
            parentFile!!.absolutePath + separator + nameWithoutExtension + "." + newExt.afterDot,
            caseSensitivity = caseSensitivity
        )

    actual fun deleteIfExists() {
        if (exists()) {
            if (isDirectory) {
                deleteRecursively()
            } else {
                delete()
            }
        }
    }


    val doesNotExist get() = !exists()


    val mExtension = FileExtension(name.substringAfter("."))

    infix fun withExtension(ext: String): MFile {
        return when (this.extension) {
            ext  -> this
            ""   -> mFile(this.cpath + "." + ext)
            else -> mFile(this.cpath.replace("." + this.extension, ".$ext"))
        }
    }

    fun appendln(line: String) {
        append(line + "\n")
    }

    val unixNlink get() = Files.getAttribute(this.toPath(), "unix:nlink").toString().toInt()
    val hardLinkCount get() = unixNlink

    operator fun get(item: MFile): MFile {
        return resolve(item)
    }

    final override operator fun get(item: String): MFile {
        return resolve(item)
    }

    //  fun toJavaIOFile() = java.io.File(user)

    operator fun get(item: Char): MFile {
        return resolve(item.toString())
    }

    override operator fun plus(other: String): MFile {
        return resolve(other)
    }

    operator fun plus(item: Char): MFile {
        return resolve(item.toString())
    }

    inline operator fun <reified F : MFile> plus(item: F): F {
        return resolve(item, F::class) as F
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


    fun size() = ByteSize(Files.size(this.toPath()))
    fun recursiveSize() = recurse { it.listFilesAsList() }.map { it.size() }.reduce { acc, byteSize -> acc + byteSize }

    fun clearIfTooBigThenAppendText(s: String) {
        if (size().kiB > 10) {
            write("cleared because over 10KB") /*got an out of memory error when limit was set as 100KB*/
        }
        append(s)

    }


    fun recursiveLastModified(): Long {
        var greatest = 0L
        recurse { it.listFiles()?.toList() ?: listOf() }.forEach {
            greatest = listOf(greatest, it.lastModified()).maxOrNull()!!
        }
        return greatest
    }


    fun next(): MFile {
        var ii = 0
        while (true) {
            val f = mFile(absolutePath + ii.toString())
            if (!f.exists()) {
                return f
            }
            ii += 1
        }
    }

    fun doubleBackupWrite(s: String, thread: Boolean = false) {

        mkparents()
        createNewFile()

        /*this is important. Extra security is always good.*/

        /*now I'm backing up version before AND after the change. */

        /*yes, there is redundancy. In some contexts redundancy is good. Safe.*/

        /*Obviously this is a reaction to a mistake I made (that turned out ok in the end, but scared me a lot).*/

        val old = readText()
        val work1 = backupWork(text = old)
        val work2 = backupWork(text = old)

        val work = {
            work1()
            writeText(s)
            work2()
        }

        if (thread) thread { work() }
        else work()

    }


    internal fun backupWork(
        @Suppress("UNUSED_PARAMETER") thread: Boolean = false, text: String? = null
    ): () -> Unit {

        require(this.exists()) {
            "cannot back up ${this}, which does not exist"
        }


        val backupFolder = toMFile().parentFile!! + "backups"
        backupFolder.mkdir()
        require(backupFolder.isDirectory) { "backupFolder not a dir" }


        val backupFileWork = backupFolder.getNextSubIndexedFileWork(name, 100)

        if (isDirectory) {
            return {
                val target = backupFileWork()
                target.deleteIfExists()
                copyRecursively(target)
            }
        }

        val realText = text ?: readText()

        return { backupFileWork().text = realText }

    }

    fun backup(thread: Boolean = false, text: String? = null) {

        val work = backupWork(thread = thread, text = text)
        if (thread) {
            thread {
                work()
            }
        } else work()
    }


    fun recursiveChildren() = recurse { it.listFiles()?.toList() ?: listOf() }

    val ensureAbsolute get() = apply { require(isAbsolute) { "$this is not absolute" } }
    val absolutePathEnforced: String get() = ensureAbsolute.absolutePath

    fun writeIfDifferent(s: String) {
        mkparents()
        if (doesNotExist || readText() != s) {
            write(s)
        }
    }


    fun relativeToOrSelf(base: MFile): MFile = idFile.relativeToOrSelf(base.idFile).toMFile()
    fun relativeToOrNull(base: MFile): MFile? = idFile.relativeToOrNull(base.idFile)?.toMFile()
    fun copyTo(target: MFile, overwrite: Boolean = false, bufferSize: Int = DEFAULT_BUFFER_SIZE): MFile =
        userFile.copyTo(target, overwrite, bufferSize).toMFile()

    actual override fun mkdirs(): Boolean {
        return super.mkdirs()
    }

    var writableForOwner: Boolean
        get() = NOT_IMPLEMENTED
        set(value) {
            val success = idFile.setWritable(value, true)
            if (!success) {
                warn("failure setting $this writable=$value for owner")
            }
        }

    var writableForEveryone: Boolean
        get() = NOT_IMPLEMENTED
        set(value) {
            val success = idFile.setWritable(value, false)
            if (!success) {
                warn("failure setting $this writable=$value for everyone")
            }
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


    actual override val partSep = SEP


}

internal actual val SEP = MFile.separator


fun Folder.idFolder() = object : IDFolder {
    override val idFile: File
        get() = this@idFolder.idFile
    override val fName: String
        get() = this@idFolder.fName
    override val filePath: String
        get() = this@idFolder.filePath
    override val partSep: String
        get() = this@idFolder.partSep

    override fun isDir() = this@idFolder.isDir()

    override fun toString(): String {
        return "[(IDFolder) ${this@idFolder}]"
    }

}

interface URLLike {
    fun toJavaURL(): URL
    fun toJavaURI(): URI
}