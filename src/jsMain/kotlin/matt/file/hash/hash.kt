package matt.file.hash

import matt.file.hash.common.MyMd5Digest
import matt.model.data.hash.md5.MD5

internal actual fun myMd5Digest(): MyMd5Digest = JsMd5HashDigest()


private class JsMd5HashDigest : MyMd5Digest() {
    override fun update(bytes: ByteArray) {
        TODO()
    }

    override fun digest(): MD5 {
        TODO()
    }
}
