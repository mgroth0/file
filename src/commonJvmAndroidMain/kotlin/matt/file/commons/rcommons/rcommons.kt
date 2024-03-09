package matt.file.commons.rcommons

import matt.file.JioFile
import matt.file.common.root
import matt.file.commons.rcommons.OpenMindFiles.OM2_HOME
import matt.file.commons.rcommons.OpenMindUserStorageLocation.om2
import matt.file.commons.rcommons.OpenMindUserStorageLocation.om5
import matt.file.commons.reg.IDE_FOLDER
import matt.file.commons.reg.REGISTERED_FOLDER
import matt.file.construct.common.mFile
import matt.file.context.BriarContextFiles
import matt.file.toJioFile
import matt.lang.anno.SeeURL
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.ResolvableFileOrUrl
import matt.model.code.sys.LinuxFileSystem
import matt.model.data.release.CURRENT_JPROFILER_VERSION


class OpenMindComputeContextFiles : BriarContextFiles(LinuxFileSystem) {
    override val defaultPathPrefix: ResolvableFileOrUrl<*> = LinuxFileSystem.root()
    private val OM_LOCAL_FOLDER by lazy {
        mFile("/local", LinuxFileSystem)
    }
    override val briarDataFolder
        get() = OM_LOCAL_FOLDER["data"]

    override val briarExtractsFolder
        get() = briarDataFolder.toJioFile()
    override val briarGlobalCacheFolder: JioFile
        get() = OM_LOCAL_FOLDER["dataBriarGlobalCache"].toJioFile()

    override val libjprofilertiPath: String
        get() = "/opt/jprofiler${CURRENT_JPROFILER_VERSION.major}/bin/linux-x64/libjprofilerti.so"
    override val jpenable: AnyFsFile
        get() = TODO()
    override val yourKitAttachScript: AnyFsFile
        get() = TODO()

    override val briarCacheFolder
        get() = OM2_HOME["cache"]["remote_compute_context"].toJioFile()
}


const val OM_USER = "mjgroth"

@SeeURL("https://github.mit.edu/MGHPCC/OpenMind/issues/4435")
object OpenMindFiles {

    val OM5_HOME = om5.forMe()
    val OM2_OLD_HOME = mFile("/om2/vast/cbmm/$OM_USER", LinuxFileSystem)
    val OM2_HOME = om2.forMe()
    val OM2_TEMP = OM2_HOME["temp"]
    val OM2_SINHA_TRANSFER = OM2_TEMP["sinha_node_transfer"]
    val OM_SINGULARITY_FOLDER = OM2_HOME["singularity"]
    val OM2_REG = OM2_OLD_HOME + REGISTERED_FOLDER.name
    val OM2_IDE = OM2_REG + IDE_FOLDER.name
    val OM_KCOMP = OM2_OLD_HOME["kcomp"]


    val OM_DATA_FOLD = OM2_OLD_HOME["data"]
}

enum class OpenMindUserStorageLocation {
    om2,
    om5;
    fun forUser(user: String) = mFile("/$name/user/$user", LinuxFileSystem)
    fun forMe() = forUser(OM_USER)
}


