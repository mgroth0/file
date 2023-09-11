@file:JavaIoFileIsOk

package matt.file.construct

import matt.file.CaseSensitivity
import matt.file.Folder
import matt.file.MFile
import matt.file.UnknownFile
import matt.file.defaultCaseSensitivity
import matt.lang.anno.Optimization
import matt.lang.anno.ok.JavaIoFileIsOk
import matt.model.data.message.SFile
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URI
import java.nio.file.Path
import kotlin.reflect.KClass

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

fun Path.toMFile() = toFile().toMFile()
fun File.toMFile(
    caseSensitivity: CaseSensitivity? = null,
    cls: KClass<out MFile>? = null
) = mFile(this, cls = cls, caseSensitivity = caseSensitivity)

fun File.toSFile() = SFile(path)

fun mFile(file: MFile) = mFile(file.userPath)
fun mFile(
    file: File,
    caseSensitivity: CaseSensitivity? = null,
    cls: KClass<out MFile>? = null
) =
    mFile(file.path, cls = cls, caseSensitivity = caseSensitivity)

fun mFile(
    parent: String,
    child: String
) = mFile(File(parent, child))

fun mFile(
    parent: MFile,
    child: String
) = mFile(parent.cpath, child)

fun mFile(uri: URI) = mFile(File(uri))

fun unTypedMFile(userPath: String) = UnknownFile(userPath)


actual fun mFile(
    userPath: String,
    caseSensitivity: CaseSensitivity?,
    cls: KClass<out MFile>?
): MFile = UnknownFile(
    userPath,
    caseSensitivity ?: defaultCaseSensitivity
)

fun mFolder(userPath: String): Folder {
    return Folder(userPath)
}