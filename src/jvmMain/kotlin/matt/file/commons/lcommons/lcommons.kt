package matt.file.commons.lcommons

import matt.file.MFile
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
        get() = mFile("/Volumes/Untitled/")
    override val briarExtractFolder: MFile
        get() = DATA_FOLDER["BRS1_extract"]
    override val libjprofilertiPath: String
        get() = MAC_LIBJPROFILERTI_PATH
}