@file:JvmName("ConstructJvmAndroidKt")
@file:JavaIoFileIsOk

package matt.file.construct

import matt.file.JioFile
import matt.file.toJioFile
import matt.lang.anno.Optimization
import matt.lang.anno.ok.JavaIoFileIsOk
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.FileSystem
import matt.model.data.message.AbsLinuxFile
import matt.model.data.message.AbsMacFile
import matt.model.data.message.RelLinuxFile
import matt.model.data.message.RelMacFile
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URI
import java.nio.file.Path

@Optimization
fun fileTextIfItExists(path: String): String? {
    val reader = BufferedReader(InputStreamReader(FileInputStream(path)))
    return try {
        reader.readText()
    } catch (e: FileNotFoundException) {
        null
    } finally {
        reader.close()
    }
}

context(FileSystem)
fun Path.toMFile(): JioFile = toFile().toMFile()

fun Path.toMFile(fs: FileSystem): JioFile = toFile().toMFile(fs)

context(FileSystem)
fun File.toMFile(): JioFile = mFile(this.path, this@FileSystem).toJioFile()

fun JioFile.toMFile() = this

fun File.toMFile(fs: FileSystem) = with(fs) { toMFile() }


fun File.toRelMacFile() = RelMacFile(path)
fun File.toAbsMacFile() = AbsMacFile(path)
fun File.toRelLinuxFile() = RelLinuxFile(path)
fun File.toAbsLinuxFile() = AbsLinuxFile(path)

context(FileSystem)
fun mFile(
    file: File,
): JioFile = mFile(file.path, this@FileSystem).toJioFile()

fun mFile(
    file: File,
    fs: FileSystem
): JioFile = mFile(file.path, fs).toJioFile()

context(FileSystem)
fun mFile(
    parent: String,
    child: String
): JioFile = mFile(parent, this@FileSystem).toJioFile() + child

fun mFile(
    parent: AnyFsFile,
    child: String
) = parent.resolve(child)

context(FileSystem)
fun mFile(uri: URI): JioFile = mFile(File(uri))


context(FileSystem)
fun mFile(path: String): JioFile = mFile(path, this@FileSystem).toJioFile()

fun mFile(path: AnyFsFile): JioFile = path.toJioFile()