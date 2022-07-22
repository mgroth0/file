@file:JavaIoFileIsOk
@file:JvmName("FileJvm")

package matt.file

import matt.file.Extensions
import matt.klib.byte.ByteSize
import matt.klib.commons.thisMachine
import matt.klib.dmap.withStoringDefault
import matt.klib.lang.inlined
import matt.klib.str.lower
import matt.klib.stream.search
import matt.klib.sys.OS
import matt.klib.sys.Unix
import matt.klib.sys.Windows
import matt.klib.tfx.isInt
import matt.stream.recurse.recurse
import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.annotation.AnnotationTarget.FILE
import kotlin.io.path.Path
import kotlin.io.path.useLines
import kotlin.reflect.KClass

@Target(FILE) annotation class JavaIoFileIsOk
@Target(FILE) annotation class UnnamedPackageIsOk

fun File.toMFile() = mFile(this)


/*mac file, matt file, whatever*//*sadly this is necessary. Java.io.file is an absolute failure because it doesn't respect Mac OSX's case sensitivity rules
  I'm actually shocked it took me so long to figure this out*/

/*TODO: SUBCLASS IS PROBABLAMATIC BEACUASE OF THE BUILTIN KOTLIN `RESOLVES` FUNCTION (can I disable or override it? maybe in unnamed package?) WHICH SECRETLY TURNS THIS BACK INTO A REGULAR FILE*//*TODO:  NOT SUBCLASSING JAVA.FILE IS PROBLEMATIC BECAUSE I NEED TONS OF BOILERPLATE SINCE THE FILE CLASS HAS SO MANY METHODS, EXTENSION METHODS, CLASSES, AND LIBRARIES IT WORKS WITH*/
actual sealed class MFile actual constructor(internal actual val userPath: String): File(userPath), CommonFile {


  val userFile = File(this.path)


  final override fun toString() =
	super.toString() /*at least one java standard lib class expects File.toString() to just print the path... (Runtime.exec or something like that)*/


  constructor(file: MFile): this(file.userPath)
  constructor(file: File): this(file.path)
  constructor(parent: String, child: String): this(File(parent, child))
  constructor(parent: MFile, child: String): this(parent.path, child)
  constructor(uri: URI): this(File(uri))

  companion object {

	val osFun by lazy { if (thisMachine.caseSensitive) { s: String -> s } else { s: String -> s.lower() } }

	fun String.osFun() = osFun(this)

	val separatorChar by lazy { File.separatorChar }
	val separator: String by lazy { File.separator }
	val pathSeparatorChar by lazy { File.pathSeparatorChar }
	val pathSeparator by lazy { File.pathSeparator }

	fun listRoots() = File.listRoots().map { mFile(it) }.toTypedArray()
	fun createTempFile(prefix: String, suffix: String?, directory: MFile?) =
	  mFile(File.createTempFile(prefix, suffix, directory))

	fun createTempFile(prefix: String, suffix: String?) = mFile(File.createTempFile(prefix, suffix))
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


  fun resolve(relative: MFile): MFile = userFile.resolve(relative).toMFile()
  fun resolve(relative: String): MFile = userFile.resolve(relative).toMFile()


  fun resolveSibling(relative: MFile) = userFile.resolveSibling(relative).toMFile()


  fun resolveSibling(relative: String): MFile = userFile.resolveSibling(relative).toMFile()

  fun mkparents() = parentFile!!.mkdirs()


  var text
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

  fun write(s: String, mkparents: Boolean = true) {
	if (mkparents) mkparents()
	writeText(s)
  }

  @Suppress("unused") val fname: String
	get() = name
  val abspath: String
	get() = absolutePath


  fun moveInto(newParent: MFile, overwrite: Boolean = false): MFile {
	return (if (overwrite) Files.move(
	  this.toPath(), (newParent + this.name).toPath(), StandardCopyOption.REPLACE_EXISTING
	)
	else Files.move(this.toPath(), (newParent + this.name).toPath())).toFile().toMFile()
  }

  fun getNextSubIndexedFileWork(filename: String, maxN: Int): ()->MFile {
	require(maxN > 0)
	val firstSubIndexFold = this + "1"
	val existingSubIndexFolds = listFiles()!!.filter {
	  it.name.isInt()
	}.sorted()
	val nextSubIndexFold =
	  if (existingSubIndexFolds.isEmpty()) firstSubIndexFold else existingSubIndexFolds.firstOrNull { (it + filename).doesNotExist }
		?: this.resolve((existingSubIndexFolds.last().name.toInt() + 1).toString())


	//	val myBackupI = (allPreviousSubIndexedFiles.keys.maxOrNull() ?: 0) + 1
	//
	//
	//	allPreviousSubIndexedFiles
	//	  .filterKeys { it < (myBackupI - n) }
	//	  .forEach { it.value.delete() }

	//	return backupFolder + "${this.name}.${extraExt}${myBackupI}"

	return {
	  if (nextSubIndexFold.name.toInt() > maxN) {
		(firstSubIndexFold + filename).delete()
		(existingSubIndexFolds - firstSubIndexFold).forEach {
		  (it + filename).moveInto(this + (it.parentFile!!.name.toInt() - 1).toString())
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
	  ""   -> mFile(this.path + "." + ext)
	  else -> mFile(this.path.replace("." + this.extension, ".$ext"))
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

  operator fun plus(item: String): MFile {
	return resolve(item)
  }

  operator fun plus(item: Char): MFile {
	return resolve(item.toString())
  }

  operator fun <F: MFile> plus(item: F): F {
	@Suppress("UNCHECKED_CAST") return resolve(item) as F
  }

}

fun mFile(file: MFile) = mFile(file.userPath)
fun mFile(file: File) = mFile(file.path)
fun mFile(parent: String, child: String) = mFile(File(parent, child))
fun mFile(parent: MFile, child: String) = mFile(parent.path, child)
fun mFile(uri: URI) = mFile(File(uri))

fun KotlinFile.fileAnnotationSimpleClassNames() =
  useLines {    /*there must be a space after package or UnnamedPackageIsOk will not be detected*/
	it.takeWhile { "package " !in it }.filter { KotlinFile.FILE_ANNO_LINE_MARKER in it }.map {
	  it.substringAfter(KotlinFile.FILE_ANNO_LINE_MARKER).substringAfterLast(".").substringBefore("\n")
		.substringBefore("(")
		.trim()
	}.toList()
  }

inline fun <reified A> KotlinFile.hasFileAnnotation() = A::class.simpleName in fileAnnotationSimpleClassNames()

private val fileTypes by lazy {
  mutableMapOf<String, KClass<out MFile>>().withStoringDefault { extension ->
	MFile::class.sealedSubclasses.flatMap { it.recurse { it.sealedSubclasses } }.firstOrNull {
	  val b = it.annotations.filterIsInstance<Extensions>().firstOrNull()?.exts?.let { extension in it } ?: false
	  b
	} ?: UnknownFile::class
  }
}

actual fun mFile(userPath: String): MFile {
  val f = File(userPath)
  if (f.isDirectory) return Folder(userPath)
  return fileTypes[f.extension].constructors.first().call(userPath)

  //  val f = File(userPath)
  //  MFile::class.sealedSubclasses.firstOrNull {
  //	it.annotations.filterIsInstance<Extensions>().firstOrNull()?.exts?.let { f.extension in it } ?: false
  //  }
  //
  //  when (File(userPath).extension) {
  //	"json" -> JsonFile(userPath)
  //	else   -> UnknownFile(userPath)
  //  }
}


fun MFile.size() = ByteSize(Files.size(this.toPath()))

fun MFile.clearIfTooBigThenAppendText(s: String) {
  if (size().kb > 10) {
	write("cleared because over 10KB") /*got an out of memory error when limit was set as 100KB*/
  }
  append(s)

}


fun MFile.recursiveLastModified(): Long {
  var greatest = 0L
  recurse { it.listFiles()?.toList() ?: listOf() }.forEach {
	greatest = listOf(greatest, it.lastModified()).maxOrNull()!!
  }
  return greatest
}


fun MFile.next(): MFile {
  var ii = 0
  while (true) {
	val f = mFile(absolutePath + ii.toString())
	if (!f.exists()) {
	  return f
	}
	ii += 1
  }
}

fun MFile.doubleBackupWrite(s: String, thread: Boolean = false) {

  mkparents()
  createNewFile()

  /*this is important. Extra security is always good.*//*now I'm backing up version before AND after the change. *//*yes, there is redundancy. In some contexts redundancy is good. Safe.*//*Obviously this is a reaction to a mistake I made (that turned out ok in the end, but scared me a lot).*/

  val old = readText()
  val work1 = backupWork(text = old)
  val work2 = backupWork(text = old)

  val work = {
	work1()
	writeText(s)
	work2()
  }

  if (thread) {
	kotlin.concurrent.thread {
	  work()
	}
  } else {
	work()
  }

}


internal fun MFile.backupWork(
  @Suppress("UNUSED_PARAMETER") thread: Boolean = false, text: String? = null
): ()->Unit {

  require(this.exists()) {
	"cannot back up ${this}, which does not exist"
  }


  val backupFolder = toMFile().parentFile!! + "backups"
  backupFolder.mkdir()
  require(backupFolder.isDirectory) { "backupFolder not a dir" }


  val backupFileWork = backupFolder.getNextSubIndexedFileWork(name, 100)

  val realText = text ?: readText()

  return { backupFileWork().text = realText }

}

fun MFile.backup(thread: Boolean = false, text: String? = null) {

  val work = backupWork(thread = thread, text = text)
  if (thread) {
	kotlin.concurrent.thread {
	  work()
	}
  } else {
	work()
  }
}


fun MFile.recursiveChildren() = recurse { it.listFiles()?.toList() ?: listOf() }

val MFile.ensureAbsolute get() = apply { require(isAbsolute) { "$this is not absolute" } }
val MFile.absolutePathEnforced: String get() = ensureAbsolute.absolutePath

fun String.makeFileSeparatorsCompatibleWith(os: OS) = when (os) {
  is Windows -> replace("/", "\\")
  is Unix    -> replace("\\", "/")
}


fun jumpToKotlinSourceString(
  rootProject: MFile,
  s: String,
  packageFilter: String?
): Pair<MFile, Int>? {
  println("matt.kjlib.jumpToKotlinSourceString:${s}:${packageFilter}")
  val packFolder = packageFilter?.replace(".", "/")
  var pair: Pair<MFile, Int>? = null
  inlined {
	rootProject["settings.gradle.kts"]
	  .readLines()
	  .asSequence()
	  .filterNot { it.isBlank() }
	  .map { it.trim() }
	  .filterNot { it.startsWith("//") }
	  .map { it.replace("include(\"", "").replace("\")", "") }
	  .map { it.replace(":", "/") }
	  .map { rootProject[it]["src"] }
	  .toList().forEach search@{ src ->
		println("searching source folder: $src")
		src.recursiveChildren()
		  .filter {
			(packageFilter == null || packFolder!! in it.absolutePath)
		  }
		  .filter { maybekt ->
			maybekt.extension == "kt"
		  }
		  .forEach kt@{ kt ->
			print("searching ${kt}... ")
			var linenum = 0 // I guess ide_open uses indices??
			kt.bufferedReader().lines().use { lines ->
			  for (line in lines) {
				if (s in line) {
				  println("found!")

				  pair = kt to linenum
				  return@inlined
				}
				linenum += 1

			  }
			}
			println("not here.")
		  }
	  }
  }
  println("matt.kjlib.jumpToKotlinSourceString: dur:${System.currentTimeMillis()}ms worked?: ${pair != null}")
  return pair
}

fun MFile.writeIfDifferent(s: String) {
  if (doesNotExist || readText() != s) {
	write(s)
  }
}