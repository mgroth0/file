@file:JvmName("FileAndroidKt")

package matt.file

import matt.lang.model.file.FileSystem
import matt.log.warn.warn
import matt.model.code.sys.LinuxFileSystem


actual val guessRuntimeFileSystem: FileSystem by lazy {
    warn(GUESS_FS_WARNING)
    LinuxFileSystem
}