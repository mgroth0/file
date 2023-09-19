package matt.file.commons.lcommons

import matt.file.FsFileImpl
import matt.file.commons.CACHE_FOLDER
import matt.file.commons.DATA_FOLDER
import matt.file.commons.REGISTERED_FOLDER
import matt.file.construct.mFile
import matt.file.context.ComputeContextFiles
import matt.lang.model.file.MacFileSystem
import matt.model.code.jvm.agentpath.MAC_LIBJPROFILERTI_PATH

class LocalComputeContextFiles : ComputeContextFiles {

    private val fakeRemoteFs = REGISTERED_FOLDER["remote"]
    private val fakeOmFs = fakeRemoteFs["om"]
    override val defaultPathPrefix = fakeOmFs
    override val briarDataFolder: FsFileImpl
        get() = mFile("/Volumes/Untitled/", MacFileSystem)
    override val briarExtractsFolder
        get() = DATA_FOLDER["BriarExtracts"]
    override val libjprofilertiPath: String
        get() = MAC_LIBJPROFILERTI_PATH

    override val cacheFolder
        get() = CACHE_FOLDER["local_compute_context"]
}