@file:JvmName("FileJvmKt")

package matt.file

import matt.file.thismachine.thisMachine

actual val guessRuntimeFileSystem by lazy {
    thisMachine.fileSystem
}


