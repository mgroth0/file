package matt.file.commons.lcommons

import matt.file.commons.CACHE_FOLDER
import matt.file.commons.DATA_FOLDER
import matt.file.commons.JPROFILER_APP_FOLDER
import matt.file.commons.REGISTERED_FOLDER
import matt.file.commons.VOLUMES_FOLDER
import matt.file.commons.YOUR_KIT_APP_FOLDER
import matt.file.context.ComputeContextFiles
import matt.lang.anno.SeeURL
import matt.lang.anno.optin.ExperimentalMattCode
import matt.lang.model.file.FsFile
import matt.model.code.jvm.agentpath.MAC_LIBJPROFILERTI_PATH
import matt.model.code.sys.LinuxFileSystem

@OptIn(ExperimentalMattCode::class)
class LocalComputeContextFiles : ComputeContextFiles {

    private val fakeRemoteFs = REGISTERED_FOLDER["remote"]
    private val fakeOmFs = fakeRemoteFs["om"]
    override val defaultPathPrefix = fakeOmFs
    override val briarDataFolder: FsFile
        get() = VOLUMES_FOLDER.withinFileSystem(LinuxFileSystem)["Untitled"]  /*the hard drive is case-sensitive!*/
    override val briarExtractsFolder
        get() = DATA_FOLDER["BriarExtracts"]
    override val libjprofilertiPath: String
        get() = MAC_LIBJPROFILERTI_PATH
    override val jpenable: FsFile
        get() = JPROFILER_APP_FOLDER["Contents"]["Resources"]["app"]["bin"]["jpenable"]


    @SeeURL("https://www.yourkit.com/docs/java-profiler/2023.5/help/console_attach_wizard.jsp")
    override val yourKitAttachScript: FsFile
        get() = YOUR_KIT_APP_FOLDER["Contents"]["Resources"]["bin"]["attach.sh"]

    override val cacheFolder
        get() = CACHE_FOLDER["local_compute_context"]
}