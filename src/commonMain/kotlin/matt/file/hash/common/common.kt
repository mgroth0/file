package matt.file.hash.common

import matt.file.hash.myMd5Digest
import matt.lang.anno.SeeURL
import matt.model.data.hash.md5.MD5
import matt.model.obj.text.HasBytes
import matt.prim.endian.MyByteOrder.BIG
import matt.prim.int.toByteArray

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

abstract class MyMd5Digest {


    abstract fun update(bytes: ByteArray)

    fun update(int: Int) {
        update(int.toByteArray(BIG))
    }

    fun update(string: String) {
        update(string.encodeToByteArray())
    }


    abstract fun digest(): MD5
}
