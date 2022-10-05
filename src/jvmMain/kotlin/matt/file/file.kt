@file:JavaIoFileIsOk
@file:JvmName("FileJvmKt")

package matt.file

import matt.collect.itr.filterNotNull
import matt.collect.itr.recurse.recurse
import matt.collect.itr.search
import matt.file.construct.mFile
import matt.file.construct.toMFile
import matt.file.ok.JavaIoFileIsOk
import matt.file.thismachine.thisMachine
import matt.lang.NOT_IMPLEMENTED
import matt.lang.userHome
import matt.log.Logger
import matt.log.NOPLogger
import matt.log.warn
import matt.model.byte.ByteSize
import matt.model.stream.Streamable
import matt.model.text.WritableText
import matt.prim.str.lower
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.concurrent.thread
import kotlin.reflect.KClass


/*mac file, matt file, whatever*//*sadly this is necessary. Java.io.file is an absolute failure because it doesn't respect Mac OSX's case sensitivity rules
  I'm actually shocked it took me so long to figure this out*/

/*TODO: SUBCLASS IS PROBABLAMATIC BEACUASE OF THE BUILTIN KOTLIN `RESOLVES` FUNCTION (can I disable or override it? maybe in unnamed package?) WHICH SECRETLY TURNS THIS BACK INTO A REGULAR FILE*//*TODO:  NOT SUBCLASSING JAVA.FILE IS PROBLEMATIC BECAUSE I NEED TONS OF BOILERPLATE SINCE THE FILE CLASS HAS SO MANY METHODS, EXTENSION METHODS, CLASSES, AND LIBRARIES IT WORKS WITH*/
actual sealed class MFile actual constructor(actual val userPath: String): File(userPath),
																		   CommonFile,
																		   Streamable,
																		   WritableText {



  actual override val filePath: String get() = super.getPath()
  actual override val cpath: String = path
  val userFile = File(this.cpath)

  override fun inputStream() = userFile.inputStream()



  actual final override fun toString() =
	super.toString() /*at least one java standard lib class expects File.toString() to just print the path... (Runtime.exec or something like that)*/


  constructor(file: MFile): this(file.userPath)
  constructor(file: File): this(file.path)
  constructor(parent: String, child: String): this(File(parent, child))
  constructor(parent: MFile, child: String): this(parent.cpath, child)
  constructor(uri: URI): this(File(uri))

  companion object {

	val osFun by lazy { if (thisMachine.caseSensitive) { s: String -> s } else { s: String -> s.lower() } }

	fun String.osFun() = osFun(this)

	val separatorChar by lazy { File.separatorChar }
	val separator: String by lazy { File.separator }
	val pathSeparatorChar by lazy { File.pathSeparatorChar }
	val pathSeparator: String by lazy { File.pathSeparator }

	fun listRoots() = File.listRoots().map { mFile(it) }.toTypedArray()
	fun createTempFile(prefix: String, suffix: String?, directory: MFile?) =
	  mFile(File.createTempFile(prefix, suffix, directory))

	fun createTempFile(prefix: String, suffix: String?) = mFile(File.createTempFile(prefix, suffix))

  }

  actual override val fname: String = name


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
	return super.getAbsoluteFile().toMFile()
  }

  override fun listFiles(): Array<MFile>? {
	return super.listFiles()?.map { it.toMFile() }?.toTypedArray()
  }

  override fun listFiles(fiilenameFilter: FilenameFilter?): Array<MFile>? {
	return super.listFiles(fiilenameFilter)?.map { it.toMFile() }?.toTypedArray()
  }

  override fun listFiles(fileFilter: FileFilter?): Array<MFile>? {
	return super.listFiles(fileFilter)?.map { it.toMFile() }?.toTypedArray()
  }

  fun listFilesOrEmpty() = listFiles() ?: arrayOf()
  fun listFilesAsList() = listFiles()?.toList()

  /*must remain lower since in ext.kt i look here for matching with a astring*/
  internal val idFile = File(osFun(userPath))


  override operator fun compareTo(other: File?): Int = idFile.compareTo((other as MFile).idFile)
  override fun equals(other: Any?): Boolean {
	return if (other is File) {
	  require(other is MFile)
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


  fun relativeTo(base: MFile): MFile = idFile.relativeTo(base.idFile).toMFile()


  fun startsWith(other: MFile): Boolean = idFile.startsWith(other.idFile)
  fun startsWith(other: String): Boolean = idFile.startsWith(osFun(other))
  fun endsWith(other: MFile) = idFile.endsWith(other.idFile)
  fun endsWith(other: String): Boolean = idFile.endsWith(other.osFun())


  actual fun resolve(other: MFile, cls: KClass<out MFile>?): MFile = userFile.resolve(other).toMFile(cls = cls)
  actual override fun resolve(other: String): MFile = userFile.resolve(other).toMFile()


  fun resolveSibling(relative: MFile) = userFile.resolveSibling(relative).toMFile()


  fun resolveSibling(relative: String): MFile = userFile.resolveSibling(relative).toMFile()

  fun mkparents() = parentFile!!.mkdirs()

  fun tildeString() = toString().replace(userHome.removeSuffix(SEP), "~")

  actual override var text
	get() = readText()
	set(v) {
	  mkparents()
	  writeText(v)
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
  }.toMFile() as Folder

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
	filename: String, maxN: Int, @Suppress("UNUSED_PARAMETER") log: Logger = NOPLogger
  ): ()->MFile {

	require(maxN > 0)
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

  fun resRepExt(newExt: String) = mFile(parentFile!!.absolutePath + separator + nameWithoutExtension + "." + newExt)

  fun deleteIfExists() {
	if (exists()) {
	  if (isDirectory) {
		deleteRecursively()
	  } else {
		delete()
	  }
	}
  }


  val doesNotExist get() = !exists()


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


  operator fun get(item: String): MFile {
	return resolve(item)
  }

  operator fun get(item: Char): MFile {
	return resolve(item.toString())
  }

  override operator fun plus(other: String): MFile {
	return resolve(other)
  }

  operator fun plus(item: Char): MFile {
	return resolve(item.toString())
  }

  inline operator fun <reified F: MFile> plus(item: F): F {
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
	if (size().kb > 10) {
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
  ): ()->Unit {

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

internal actual val SEP = MFile.pathSeparator