@file:JavaIoFileIsOk

package matt.file.construct

import matt.collect.map.dmap.withStoringDefault
import matt.collect.map.lazyMap
import matt.file.CaseSensitivity
import matt.file.Folder
import matt.file.MFile
import matt.file.UnknownFile
import matt.file.defaultCaseSensitivity
import matt.file.ext.FileExtension
import matt.file.fileClassForExtension
import matt.lang.anno.Optimization
import matt.model.code.ok.JavaIoFileIsOk
import matt.model.data.message.SFile
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.lang.reflect.Constructor
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
): MFile {
    if (cls != null && cls != MFile::class) {
        val constructor = constructorsByCls[cls]
        /*return constructor.call(userPath)*/
        if (caseSensitivity != null) {
            return constructor.newInstance(userPath, caseSensitivity) as MFile
        }
        return constructor.newInstance(userPath, defaultCaseSensitivity) as MFile
    }
    val f = File(userPath)
    if (caseSensitivity != null) {
        return constructors[f.extension].newInstance(userPath, caseSensitivity) as MFile
    }
    return constructors[f.extension].newInstance(userPath, defaultCaseSensitivity) as MFile
    /*.call(userPath)*/
}

fun mFolder(userPath: String): Folder {
    return Folder(userPath)
}


private val fileTypes by lazy {


    mutableMapOf<String, KClass<out MFile>>().withStoringDefault { extension ->

        fileClassForExtension(FileExtension(extension))

        /*	MFile::class.sealedSubclasses.flatMap { it.recurse { it.sealedSubclasses } }.firstOrNull {
              val b = it.annotations.filterIsInstance<Extensions>().firstOrNull()?.exts?.let { extension in it } ?: false
              b
            } ?: UnknownFile::class*/


    }


}
private val constructors = lazyMap<String, Constructor<*>> {
    /*surprisingly getting constructors is expensive so this could have a huge performance benefit and even solve some bugs maybe*/
    constructorsByCls[fileTypes[it]]
}
private val constructorsByCls = lazyMap<KClass<out MFile>, Constructor<*>> {
    /*surprisingly getting constructors is expensive so this could have a huge performance benefit and even solve some bugs maybe*/
    it.java.constructors.first()
    /*it.constructors.first()*/
}

