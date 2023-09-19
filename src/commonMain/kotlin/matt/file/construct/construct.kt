package matt.file.construct

import matt.file.FsFileImpl
import matt.lang.model.file.FileSystem
import matt.lang.model.file.constructFilePath

fun mFile(
    inputPath: String,
    fileSystem: FileSystem,
) = FsFileImpl(fileSystem.constructFilePath(inputPath), fileSystem)

