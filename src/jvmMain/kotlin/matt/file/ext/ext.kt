package matt.file.ext

import matt.file.MFile
import matt.file.toMFile
import java.nio.file.FileSystems
import java.nio.file.Path



@Suppress("DEPRECATION") fun createTempDir(prefix: String = "tmp", suffix: String? = null, directory: MFile? = null) =
  kotlin.io.createTempDir(prefix, suffix, directory).toMFile()


@Suppress("DEPRECATION") fun createTempFile(prefix: String = "tmp", suffix: String? = null, directory: MFile? = null) =
  kotlin.io.createTempFile(prefix, suffix, directory).toMFile()


fun MFile.relativeToOrSelf(base: MFile): MFile = idFile.relativeToOrSelf(base.idFile).toMFile()
fun MFile.relativeToOrNull(base: MFile): MFile? = idFile.relativeToOrNull(base.idFile)?.toMFile()
fun MFile.copyTo(target: MFile, overwrite: Boolean = false, bufferSize: Int = DEFAULT_BUFFER_SIZE): MFile =
  userFile.copyTo(target, overwrite, bufferSize).toMFile()

/*fun MFile.copyRecursively(
  target: MFile,
  overwrite: Boolean = false,
  onError: (MFile, IOException)->OnErrorAction = { _, exception -> throw exception }
): Boolean = userFile.copyRecursively(target.userFile, overwrite) { f, e ->
  onError(MFile(f), e)
}*/


fun String.writeToFile(f: MFile, mkdirs: Boolean = true) {
  if (mkdirs) {
	f.parentFile!!.mkdirs()
  }
  f.writeText(this)
}




@Suppress("unused")
fun Iterable<MFile>.filterHasExtension(ext: String) = filter { it.extension == ext }

@Suppress("unused")
fun Sequence<MFile>.filterHasExtension(ext: String) = filter { it.extension == ext }


@Suppress("unused")
fun Path.startsWithAny(atLeastOne: Path, vararg more: Path): Boolean {
  if (startsWith(atLeastOne)) return true
  more.forEach { if (startsWith(it)) return true }
  return false
}

fun Path.startsWithAny(atLeastOne: MFile, vararg more: MFile): Boolean {
  if (startsWith(atLeastOne.toPath())) return true
  more.forEach { if (startsWith(it.toPath())) return true }
  return false
}

fun String.toPath(): Path = FileSystems.getDefault().getPath(this.trim())


