package matt.file.expects.file

import matt.file.common.IoFile
import matt.file.toJioFile
import matt.lang.model.file.AnyFsFile

actual fun AnyFsFile.toIoFile(): IoFile<*> = toJioFile()
