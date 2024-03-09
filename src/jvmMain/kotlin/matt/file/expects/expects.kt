package matt.file.expects

import matt.file.thismachine.thisMachine
import matt.lang.model.file.FileSystemResolver


actual val guessRuntimeFileSystemResolver: FileSystemResolver by lazy {
    thisMachine
}
