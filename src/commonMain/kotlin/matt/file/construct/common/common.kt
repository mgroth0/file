package matt.file.construct.common

import matt.file.common.SimpleFsFileImpl
import matt.lang.model.file.FileSystem
import matt.lang.model.file.constructFilePath

fun mFile(
    inputPath: String,
    fileSystem: FileSystem
) = SimpleFsFileImpl(fileSystem.constructFilePath(inputPath), fileSystem)

