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

@SeeURL("https://www.baeldung.com/java-md5")
fun MFile.md5(): String {
    val md = MyMd5Digest()
    md.update(bytes)
    return md.digest()
}


fun MFile.recursiveMD5(
    ignoreDSStore: Boolean = true,
    ignoreFileNames: List<String> = listOf(),
    ignoreAllWithPathParts: List<String> = listOf()
): String {
    val md = MyMd5Digest()
    walk().sortedBy { it.absolutePath }.map { it.toMFile() }.filter {
        it != this
                && (!ignoreDSStore || it.name != DS_STORE)
                && it.name !in ignoreFileNames
                && it.path.split(MFile.separator).none { it in ignoreAllWithPathParts }
    }.forEach {
        md.update(it.relativeTo(this@recursiveMD5).path)
        md.update(it.readBytes())
    }
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

    fun digest() = md.digest().encodeToURLBase64WithoutPadding()
}