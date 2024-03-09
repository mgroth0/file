package matt.file.expects

import matt.file.GUESS_FS_WARNING
import matt.file.thismachine.thisMachine
import matt.lang.model.file.FileSystemResolver
import matt.log.warn.common.warn


actual val guessRuntimeFileSystemResolver: FileSystemResolver by lazy {
    warn("Android file case-sensitivity depends on where the file is located. (on a SD card or not etc.)")
    warn(GUESS_FS_WARNING)
    thisMachine
}
