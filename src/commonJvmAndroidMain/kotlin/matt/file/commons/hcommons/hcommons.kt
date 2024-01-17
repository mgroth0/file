package matt.file.commons.hcommons

import matt.file.FSRoot
import matt.file.commons.REMOTE_JPOFILER_CONFIG_FILE_NAME
import matt.file.construct.mFile
import matt.file.context.ProcessContextFiles
import matt.lang.model.file.FsFile
import matt.model.code.sys.LinuxFileSystem


object HerokuExecutionContextFiles : ProcessContextFiles {
    override val jpenable = mFile("/usr/local/bin/jpenable", LinuxFileSystem)
    override val libjprofilertiPath: String
        get() = TODO()
    override val jProfilerConfigFile: FsFile = FSRoot(LinuxFileSystem).resolve(REMOTE_JPOFILER_CONFIG_FILE_NAME)
    override val yourKitAttachScript: FsFile
        get() = TODO()
    override val om2Home: FsFile
        get() = TODO()
}