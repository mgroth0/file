package matt.file.expects.file

import matt.file.common.IoFile
import matt.lang.model.file.AnyFsFile


expect fun AnyFsFile.toIoFile(): IoFile<*>
