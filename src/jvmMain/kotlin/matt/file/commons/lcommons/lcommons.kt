package matt.file.commons.lcommons

import matt.file.CaseSensitivity.CaseSensitive
import matt.file.MFile
import matt.file.commons.CACHE_FOLDER
import matt.file.commons.DATA_FOLDER
import matt.file.commons.REGISTERED_FOLDER
import matt.file.construct.mFile
import matt.file.context.ComputeContextFiles
import matt.model.code.jvm.agentpath.MAC_LIBJPROFILERTI_PATH

class LocalComputeContextFiles : ComputeContextFiles {

    private val fakeRemoteFs = REGISTERED_FOLDER["remote"]
    private val fakeOmFs = fakeRemoteFs["om"]
    override val defaultPathPrefix = fakeOmFs
    override val briarDataFolder: MFile
        get() = mFile("/Volumes/Untitled/", caseSensitivity = CaseSensitive)
    override val briarExtractsFolder: MFile
        get() = DATA_FOLDER["BriarExtracts"]
    override val libjprofilertiPath: String
        get() = MAC_LIBJPROFILERTI_PATH

    override val cacheFolder: MFile
        get() = CACHE_FOLDER["local_compute_context"]
}