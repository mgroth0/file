package matt.file.commons.hcommons

import matt.file.commons.REMOTE_JPOFILER_CONFIG_FILE_NAME
import matt.file.construct.mFile
import matt.file.context.ProcessContextFiles
import matt.file.root
import matt.lang.model.file.AnyFsFile
import matt.model.code.sys.LinuxFileSystem


object HerokuExecutionContextFiles : ProcessContextFiles() {
    override val jpenable = mFile("/usr/local/bin/jpenable", LinuxFileSystem)
    override val libjprofilertiPath: String
        get() = TODO()
    override val jProfilerConfigFile: AnyFsFile
        get() {
            return LinuxFileSystem.root().resolve(REMOTE_JPOFILER_CONFIG_FILE_NAME)
        }
    override val yourKitAttachScript: AnyFsFile
        get() = TODO()
    override val om2Home: AnyFsFile
        get() = TODO()
}