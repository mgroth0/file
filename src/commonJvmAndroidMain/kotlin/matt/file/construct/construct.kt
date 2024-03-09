@file:JavaIoFileIsOk

package matt.file.construct

import matt.file.JioFile
import matt.file.toJioFile
import matt.lang.anno.Optimization
import matt.lang.anno.ok.JavaIoFileIsOk
import matt.lang.fnf.runCatchingFileTrulyNotFound
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFile
import matt.model.data.message.AbsLinuxFile
import matt.model.data.message.AbsMacFile
import matt.model.data.message.RelLinuxFile
import matt.model.data.message.RelMacFile
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import matt.file.construct.common.mFile as commonMfile

@Optimization
fun fileTextIfItExists(path: String): String? {
    val jioPath = Path.of(path)
    return runCatchingFileTrulyNotFound(
        file = { jioPath.toFile() }
    ) {
        Files.readAllBytes(jioPath).decodeToString()
    }.getOrNull()
}

context(FileSystem)
fun Path.toMFile(): JioFile = toMFile(this@FileSystem)

fun Path.toMFile(fs: FileSystem): JioFile =
    when (this) {
        is FsFile -> withinFileSystem(fs).toJioFile()
        else -> toFile().toMFile(fs)
    }

context(FileSystem)
fun File.toMFile(): JioFile = mFile(path, this@FileSystem).toJioFile()

fun JioFile.toMFile() = this

fun File.toMFile(fs: FileSystem) = with(fs) { toMFile() }


fun File.toRelMacFile() = RelMacFile(path)
fun File.toAbsMacFile() = AbsMacFile(path)
fun File.toRelLinuxFile() = RelLinuxFile(path)
fun File.toAbsLinuxFile() = AbsLinuxFile(path)

context(FileSystem)
fun mFile(
    file: File
): JioFile = commonMfile(file.path, this@FileSystem).toJioFile()

fun mFile(
    file: File,
    fs: FileSystem
): JioFile = commonMfile(file.path, fs).toJioFile()

context(FileSystem)
fun mFile(
    parent: String,
    child: String
): JioFile = commonMfile(parent, this@FileSystem).toJioFile() + child

fun mFile(f: String, fileSystem: FileSystem) = with(fileSystem) { mFile(f) }
fun mFile(f: FsFile, fileSystem: FileSystem) = with(fileSystem) { mFile(f) }

fun mFile(
    parent: AnyFsFile,
    child: String
) = parent.resolve(child)

context(FileSystem)
fun mFile(uri: URI): JioFile = mFile(File(uri))


context(FileSystem)
fun mFile(path: String): JioFile = commonMfile(path, this@FileSystem).toJioFile()

fun mFile(path: AnyFsFile): JioFile = path.toJioFile()
