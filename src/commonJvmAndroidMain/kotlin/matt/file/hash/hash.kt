package matt.file.hash

import matt.file.hash.common.MyMd5Digest
import matt.file.hash.j.JvmMd5HashDigest

actual fun myMd5Digest(): MyMd5Digest = JvmMd5HashDigest()

