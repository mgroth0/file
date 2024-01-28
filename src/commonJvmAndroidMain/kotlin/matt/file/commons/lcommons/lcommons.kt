package matt.file.commons.lcommons

import matt.file.JioFile
import matt.file.commons.CACHE_FOLDER
import matt.file.commons.DATA_FOLDER
import matt.file.commons.JPROFILER_APP_FOLDER
import matt.file.commons.REGISTERED_FOLDER
import matt.file.commons.StupidLinuxVOLUMES_FOLDER
import matt.file.commons.YOUR_KIT_APP_FOLDER
import matt.file.context.BriarContextFiles
import matt.lang.anno.SeeURL
import matt.lang.model.file.AnyFsFile
import matt.model.code.jvm.agentpath.MAC_LIBJPROFILERTI_PATH

class LocalComputeContextFiles : BriarContextFiles() {

    private val fakeRemoteFs = REGISTERED_FOLDER["remote"]
    private val fakeOmFs = fakeRemoteFs["om"]
    override val defaultPathPrefix = fakeOmFs
    override val briarDataFolder: AnyFsFile
        get() = StupidLinuxVOLUMES_FOLDER["Untitled"]  /*the hard drive is case-sensitive!*/
    override val briarExtractsFolder
        get() = DATA_FOLDER["BriarExtracts"]
    override val briarGlobalCacheFolder: JioFile
        get() = DATA_FOLDER["BriarExtractsGlobalCache"]
    override val libjprofilertiPath: String
        get() = MAC_LIBJPROFILERTI_PATH

    override val jpenable: AnyFsFile
        get() = JPROFILER_APP_FOLDER["Contents"]["Resources"]["app"]["bin"]["jpenable"]


    @SeeURL("https://www.yourkit.com/docs/java-profiler/2023.5/help/console_attach_wizard.jsp")
    override val yourKitAttachScript: AnyFsFile
        get() = YOUR_KIT_APP_FOLDER["Contents"]["Resources"]["bin"]["attach.sh"]

    override val briarCacheFolder
        get() = CACHE_FOLDER["local_compute_context"]
}