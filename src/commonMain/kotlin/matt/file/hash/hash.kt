package matt.file.hash

import matt.lang.anno.SeeURL
import matt.model.data.hash.md5.MD5
import matt.model.obj.text.HasBytes


fun ByteArray.md5(): MD5 {
    val md = myMd5Digest()
    md.update(this)
    return md.digest()
}

fun String.md5(): MD5 {
    val md = myMd5Digest()
    md.update(this)
    return md.digest()
}

@SeeURL("https://www.baeldung.com/java-md5")
fun HasBytes.md5(): MD5 {
    val md = myMd5Digest()
    md.update(bytes)
    return md.digest()
}


internal expect fun myMd5Digest(): MyMd5Digest

abstract class MyMd5Digest {


    abstract fun update(bytes: ByteArray)

    fun update(string: String) {
        update(string.encodeToByteArray())
    }


    abstract fun digest(): MD5
}


