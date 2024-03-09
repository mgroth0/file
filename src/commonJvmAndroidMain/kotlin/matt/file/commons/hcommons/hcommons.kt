package matt.file.commons.hcommons

import matt.file.common.root
import matt.file.commons.fnames.FileNames.REMOTE_JPROFILER_CONFIG
import matt.file.construct.common.mFile
import matt.file.context.ProcessContextFiles
import matt.lang.model.file.AnyFsFile
import matt.model.code.sys.LinuxFileSystem


object HerokuExecutionContextFiles : ProcessContextFiles() {
    override val jpenable = mFile("/usr/local/bin/jpenable", LinuxFileSystem)
    override val libjprofilertiPath: String
        get() = TODO()
    override val jProfilerConfigFile: AnyFsFile
        get() {
            return LinuxFileSystem.root().resolve(REMOTE_JPROFILER_CONFIG)
        }
    override val yourKitAttachScript: AnyFsFile
        get() = TODO()
    override val om2Home: AnyFsFile
        get() = TODO()
}
