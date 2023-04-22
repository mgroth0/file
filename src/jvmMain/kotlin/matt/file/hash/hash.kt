package matt.file.hash

import matt.file.MFile
import matt.file.commons.DS_STORE
import matt.file.construct.toMFile
import matt.lang.anno.SeeURL
import matt.prim.base64.encodeToURLBase64WithoutPadding
import java.security.MessageDigest

fun ByteArray.md5(): String {
    val md = MyMd5Digest()
    md.update(this)
    return md.digest()
}

fun String.md5(): String {
    val md = MyMd5Digest()
    md.update(this)
    return md.digest()
}

@SeeURL("https://www.baeldung.com/java-md5")
fun MFile.md5(): String {
    val md = MyMd5Digest()
    md.update(bytes)
    return md.digest()
}

private val DEFAULT_IGNORE_DS_STORE = true
private val DEFAULT_IGNORE_FILE_NAMES = listOf<String>()
private val DEFAULT_IGNORE_ALL_WITH_PATH_PARTS = listOf<String>()
private val DEFAULT_IGNORE_ALL_WITH_PATH_PARTS_CONTAINING = listOf<String>()

fun MFile.recursiveMD5(
    ignoreDSStore: Boolean = DEFAULT_IGNORE_DS_STORE,
    ignoreFileNames: List<String> = DEFAULT_IGNORE_FILE_NAMES,
    ignoreAllWithPathParts: List<String> = DEFAULT_IGNORE_ALL_WITH_PATH_PARTS,
    ignoreAllWithPathPartsContaining: List<String> = DEFAULT_IGNORE_ALL_WITH_PATH_PARTS_CONTAINING
): String {
    val md = MyMd5Digest()
    md.updateFromFileRecursively(
        file = this,
        ignoreDSStore = ignoreDSStore,
        ignoreFileNames = ignoreFileNames,
        ignoreAllWithPathParts = ignoreAllWithPathParts,
        ignoreAllWithPathPartsContaining = ignoreAllWithPathPartsContaining
    )
    return md.digest()
}


class MyMd5Digest {
    private val md = MessageDigest.getInstance("MD5")
    fun update(bytes: ByteArray) {
        md.update(bytes)
    }

    fun update(string: String) {
        md.update(string.encodeToByteArray())
    }

    fun updateFromFileRecursively(
        file: MFile,
        ignoreDSStore: Boolean = DEFAULT_IGNORE_DS_STORE,
        ignoreFileNames: List<String> = DEFAULT_IGNORE_FILE_NAMES,
        ignoreAllWithPathParts: List<String> = DEFAULT_IGNORE_ALL_WITH_PATH_PARTS,
        ignoreAllWithPathPartsContaining: List<String> = DEFAULT_IGNORE_ALL_WITH_PATH_PARTS_CONTAINING
    ) {
        file.walk().sortedBy { it.absolutePath }.map { it.toMFile() }.filter {
            it != file
                    && (!ignoreDSStore || it.name != DS_STORE)
                    && it.name !in ignoreFileNames
                    && it.path.split(MFile.separator).none { it in ignoreAllWithPathParts }
                    && it.path.split(MFile.separator).none { part -> ignoreAllWithPathPartsContaining.any { it in part } }
        }.forEach {
//            println("updating from file: $it")
            update(it.relativeTo(file).path)
            if (!it.isDir()) update(it.readBytes())
        }
    }


    fun digest(): String = md.digest().encodeToURLBase64WithoutPadding()
}