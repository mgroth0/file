@file:JvmName("FileJvmKt")

package matt.file

import matt.file.thismachine.thisMachine
import matt.lang.model.file.FileSystemResolver

actual val guessRuntimeFileSystemResolver: FileSystemResolver by lazy {

    thisMachine
}


