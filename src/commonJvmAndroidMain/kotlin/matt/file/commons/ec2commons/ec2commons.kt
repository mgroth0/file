package matt.file.commons.ec2commons

import matt.file.commons.lcommons.LocalComputeContextFiles
import matt.file.commons.reg.REGISTERED_FOLDER
import matt.file.construct.common.mFile
import matt.file.context.ComputeContextFiles
import matt.lang.model.file.AnyFsFile
import matt.model.code.sys.LinuxFileSystem


object Ec2Files : ComputeContextFiles(LinuxFileSystem) {


    override val jpenable get() = TODO()
    override val libjprofilertiPath: String
        get() = TODO()
    override val defaultPathPrefix =
        DEFAULT_UBUNTU_HOME_FOLDER[REGISTERED_FOLDER.name][LocalComputeContextFiles().defaultPathPrefix.name]
    override val yourKitAttachScript: AnyFsFile
        get() = TODO()
}


val DEFAULT_UBUNTU_HOME_FOLDER by lazy {
    mFile("/home/${DEFAULT_UBUNTU_USER}", LinuxFileSystem)
}

const val DEFAULT_UBUNTU_USER = "ubuntu"
