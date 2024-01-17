package matt.file.hash

import matt.model.data.hash.md5.MD5

internal actual fun myMd5Digest(): MyMd5Digest = JsMd5HashDigest()


class JsMd5HashDigest : MyMd5Digest() {
    override fun update(bytes: ByteArray) {
        TODO()
    }

    override fun digest(): MD5 {
        TODO()
    }
}