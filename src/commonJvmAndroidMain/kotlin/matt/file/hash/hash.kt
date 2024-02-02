@file:JvmName("HashJvmKt")

package matt.file.hash

import matt.file.JvmMFile
import matt.file.commons.DS_STORE
import matt.file.construct.toMFile
import matt.file.ext.walk
import matt.model.data.hash.md5.MD5
import matt.prim.base64.encodeToURLBase64WithoutPadding
import java.security.MessageDigest

actual fun myMd5Digest(): MyMd5Digest = JvmMd5HashDigest()

private const val DEFAULT_IGNORE_DS_STORE = true
private val DEFAULT_IGNORE_FILE_NAMES = listOf<String>()
private val DEFAULT_IGNORE_ALL_WITH_PATH_PARTS = listOf<String>()
private val DEFAULT_IGNORE_ALL_WITH_PATH_PARTS_CONTAINING = listOf<String>()


fun JvmMFile.recursiveMD5(
    ignoreDSStore: Boolean = DEFAULT_IGNORE_DS_STORE,
    ignoreFileNames: List<String> = DEFAULT_IGNORE_FILE_NAMES,
    ignoreAllWithPathParts: List<String> = DEFAULT_IGNORE_ALL_WITH_PATH_PARTS,
    ignoreAllWithPathPartsContaining: List<String> = DEFAULT_IGNORE_ALL_WITH_PATH_PARTS_CONTAINING
): MD5 {

    val md = myMd5Digest() as JvmMd5HashDigest
    md.updateFromFileRecursively(
        file = this,
        ignoreDSStore = ignoreDSStore,
        ignoreFileNames = ignoreFileNames,
        ignoreAllWithPathParts = ignoreAllWithPathParts,
        ignoreAllWithPathPartsContaining = ignoreAllWithPathPartsContaining
    )
    return md.digest()
}


class JvmMd5HashDigest : MyMd5Digest() {
    private val md: MessageDigest = MessageDigest.getInstance("MD5")

    override fun update(bytes: ByteArray) {
        md.update(bytes)
    }


    fun updateFromFileRecursively(
        file: JvmMFile,
        ignoreDSStore: Boolean = DEFAULT_IGNORE_DS_STORE,
        ignoreFileNames: List<String> = DEFAULT_IGNORE_FILE_NAMES,
        ignoreAllWithPathParts: List<String> = DEFAULT_IGNORE_ALL_WITH_PATH_PARTS,
        ignoreAllWithPathPartsContaining: List<String> = DEFAULT_IGNORE_ALL_WITH_PATH_PARTS_CONTAINING
    ) {
        require(file.isAbsolute)
        file.walk().sortedBy {
            it.path
        }.map {
            it.toMFile()
        }.filter {
            it != file
        }.filter {
            !ignoreDSStore || !it.hasName(DS_STORE)
        }.filter {
            it.name !in ignoreFileNames
        }.filter {
            it.path.split(file.fileSystem.separator).none { it in ignoreAllWithPathParts }
        }.filter {
            it.path.split(file.fileSystem.separator)
                .none { part -> ignoreAllWithPathPartsContaining.any { it in part } }
        }.forEach {
            if (it == file) {
                update("")
            } else {
                update(it.relativeTo(file).path)
            }
            if (!it.isDir()) update(it.readBytes())
        }
    }

    override fun digest(): MD5 = MD5(md.digest().encodeToURLBase64WithoutPadding())


}

/*
java BenchmarkExample
CRC32 time (ms): 531
MD5 time (ms): 1754
* */
