package matt.file.context

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import matt.file.FileOrURL
import matt.file.MFile
import matt.file.commons.lcommons.LocalComputeContextFiles
import matt.file.commons.rcommons.OpenMindComputeContextFiles
import matt.file.commons.rcommons.OpenMindFiles
import matt.file.construct.mFile
import matt.lang.context.DEFAULT_LINUX_PROGRAM_PATH_CONTEXT
import matt.lang.context.DEFAULT_MAC_PROGRAM_PATH_CONTEXT
import matt.lang.context.DEFAULT_WINDOWS_PROGRAM_PATH_CONTEXT
import matt.lang.platform.OsEnum
import matt.lang.platform.OsEnum.Linux
import matt.lang.platform.OsEnum.Mac
import matt.lang.platform.OsEnum.Windows

@Serializable
sealed interface ComputeContext {
    val files: ComputeContextFiles
    val taskLabel: String
    val needsModules: Boolean
    val os: OsEnum
}

val ComputeContext.shellPathContext
    get() = when (os) {
        OsEnum.Linux -> DEFAULT_LINUX_PROGRAM_PATH_CONTEXT
        OsEnum.Mac   -> DEFAULT_MAC_PROGRAM_PATH_CONTEXT
        Windows      -> DEFAULT_WINDOWS_PROGRAM_PATH_CONTEXT
    }



@Serializable
sealed class ComputeContextImpl : ComputeContext {
    override fun toString(): String {
        return this::class.simpleName!!
    }
}

@Serializable
@SerialName("OM")
class OpenMindComputeContext : ComputeContextImpl() {
    override val needsModules = true
    override val taskLabel = "OpenMind"
    override val os = Linux
    override val files by lazy {
        OpenMindComputeContextFiles()
    }

    override fun equals(other: Any?): Boolean {
        return other is OpenMindComputeContext
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}

@Serializable
@SerialName("Local")
class LocalComputeContext : ComputeContextImpl() {
    override val os = Mac
    override val needsModules = false
    override val taskLabel = "Local"
    override val files by lazy {
        LocalComputeContextFiles()
    }

    override fun equals(other: Any?): Boolean {
        return other is LocalComputeContextFiles
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}


interface ComputeContextFiles {

    companion object {
        const val BRIAR_EXTRACT_METADATA_FILE_NAME = "metadata.json"
    }

    val libjprofilertiPath: String
    val defaultPathPrefix: FileOrURL
    val briarDataFolder: MFile
    val om2Home get() = mFile(defaultPathPrefix[OpenMindFiles.OM2_HOME.path.removePrefix(MFile.unixSeparator)].cpath)
    val jProfilerConfigFile: MFile get() = om2Home[".jprofiler_config.xml"]
    val jarsFolder get() = om2Home["jars"]
    val tempFolder get() = om2Home["temp"]
    val snapshotFolder get() = tempFolder["jprofiler"]
    val latestJpSnapshot get() = snapshotFolder["latest.jps"]
    val rTaskOutputs get() = om2Home["rTaskOutputs"]
    val briarExtractsFolder: MFile
    val sbatchOutputFolder get() = om2Home["output"]
    val nvidiaSmiOutput get() = om2Home["nvidia-smi-output"]

    val sBatchScript get() = mFile(defaultPathPrefix["home/mjgroth/extract.sh"].cpath)
    val sBatchScriptJson get() = mFile(sBatchScript.cpath + ".json")

    val brs1Folder get() = briarDataFolder["BRS1"]

    val cacheFolder: MFile

}