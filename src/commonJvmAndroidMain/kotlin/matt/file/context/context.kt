package matt.file.context

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import matt.file.JioFile
import matt.file.commons.GRADLE_PROPERTIES_FILE_NAME
import matt.file.commons.JPROFILER_CONFIG_NAME
import matt.file.commons.USER_HOME
import matt.file.commons.hcommons.HerokuExecutionContextFiles
import matt.file.commons.lcommons.LocalComputeContextFiles
import matt.file.commons.rcommons.OpenMindComputeContextFiles
import matt.file.commons.rcommons.OpenMindFiles
import matt.file.construct.mFile
import matt.file.context.BriarDataSplit.BRS
import matt.file.context.BriarDataSplit.BTS
import matt.file.thismachine.thisMachine
import matt.file.toJioFile
import matt.lang.context.DEFAULT_LINUX_PROGRAM_PATH_CONTEXT
import matt.lang.context.DEFAULT_MAC_PROGRAM_PATH_CONTEXT
import matt.lang.context.DEFAULT_WINDOWS_PROGRAM_PATH_CONTEXT
import matt.lang.model.file.FileOrURL
import matt.lang.model.file.FileSystem
import matt.lang.model.file.FsFile
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.UnsafeFilePath
import matt.lang.model.file.toUnsafe
import matt.lang.platform.HasOs
import matt.lang.platform.OS
import matt.lang.platform.OsEnum
import matt.lang.platform.OsEnum.Linux
import matt.lang.platform.OsEnum.Mac
import matt.lang.platform.OsEnum.Windows
import matt.model.code.sys.LinuxFileSystem
import matt.model.code.sys.OpenMind
import java.util.*

/*needs to be unsealed for class delegation (in Tests for example)*/
interface UnsealedProcessContext : HasOs

/*needs to be sealed for serialization*/
@Serializable
sealed interface ProcessContext : UnsealedProcessContext {

    companion object {
        fun detect() = when (OS) {
            matt.lang.platform.Windows -> TODO()
            matt.lang.platform.Linux   -> when (thisMachine) {
                is OpenMind -> OpenMindComputeContext
                else        -> HerokuProcessContext
            }

            matt.lang.platform.Mac     -> LocalComputeContext
        }
    }

    val files: ProcessContextFiles
}

@Serializable
sealed interface ComputeContext : ProcessContext {
    override val files: ComputeContextFiles
    val taskLabel: String
    val needsModules: Boolean
    val usesJavaInSingularity: Boolean
    val javaHome: UnsafeFilePath?
    override val os: OsEnum
    val fileSystem: FileSystem
}

val ComputeContext.shellPathContext
    get() = when (os) {
        Linux   -> DEFAULT_LINUX_PROGRAM_PATH_CONTEXT
        Mac     -> DEFAULT_MAC_PROGRAM_PATH_CONTEXT
        Windows -> DEFAULT_WINDOWS_PROGRAM_PATH_CONTEXT
    }


@Serializable
sealed class ExecutionContextImpl : ProcessContext {
    final override fun toString(): String {
        return this::class.simpleName!!
    }
}

@Serializable
sealed class ComputeContextImpl : ExecutionContextImpl(), ComputeContext

@Serializable
@SerialName("OM")
data object OpenMindComputeContext : ComputeContextImpl() {
    override val needsModules = true
    override val javaHome = null
    override val usesJavaInSingularity = true
    override val taskLabel = "OpenMind"
    override val os = Linux
    override val files by lazy {
        OpenMindComputeContextFiles()
    }

    override val fileSystem: FileSystem
        get() = LinuxFileSystem

}

@Serializable
@SerialName("Local")
data object LocalComputeContext : ComputeContextImpl() {
    override val os = Mac
    override val needsModules = false
    override val usesJavaInSingularity = false
    override val javaHome by lazy {
        GRADLE_JAVA_HOME.toUnsafe()
    }
    override val taskLabel = "Local"
    override val files by lazy {
        LocalComputeContextFiles()
    }

    override val fileSystem: FileSystem
        get() = MacFileSystem
}


@Serializable
@SerialName("Heroku")
data object HerokuProcessContext : ExecutionContextImpl() {
    override val files: ProcessContextFiles get() = HerokuExecutionContextFiles
    override val os: OsEnum get() = Linux
}

val GRADLE_JAVA_HOME by lazy {
    with(thisMachine.fileSystem) {
        mFile(
            Properties().apply {
                load(
                    (USER_HOME + ".gradle" + GRADLE_PROPERTIES_FILE_NAME).toJioFile().reader()
                )
            }["org.gradle.java.home"].toString(),
        )
    }
}

interface ProcessContextFiles {
    val libjprofilertiPath: String
    val jpenable: FsFile
    val jProfilerConfigFile: FsFile
    val yourKitAttachScript: FsFile
}

interface ComputeContextFiles : ProcessContextFiles {

    companion object {
        const val BRIAR_EXTRACT_METADATA_FILE_NAME = "metadata.json"
        const val BRIAR_EXTRACT_MINIMAL_METADATA_FILE_NAME = "metadata_minimal.json"
    }


    val defaultPathPrefix: FileOrURL
    val briarDataFolder: FsFile
    val om2Home
        get() = mFile(
            defaultPathPrefix[OpenMindFiles.OM2_HOME.path.removePrefix(JioFile.unixSeparator)].cpath,
            LinuxFileSystem
        ).toJioFile()
    override val jProfilerConfigFile: FsFile get() = om2Home[JPROFILER_CONFIG_NAME]
    val jarsFolder get() = om2Home["jars"]
    val tempFolder get() = om2Home["temp"]
    val snapshotFolder get() = tempFolder["jprofiler"]
    val latestJpSnapshot get() = snapshotFolder["latest.jps"]
    val rTaskOutputs get() = om2Home["rTaskOutputs"]
    val briarExtractsFolder: JioFile
    val sbatchOutputFolder get() = om2Home["output"]

    val sBatchScript get() = mFile(defaultPathPrefix["home/mjgroth/extract.sh"].cpath, LinuxFileSystem)
    val sBatchScriptJson get() = mFile(sBatchScript.cpath + ".json", LinuxFileSystem)

    val brs1Folder get() = briarDataFolder["${BRS.name}1"]
    val bts1Folder get() = briarDataFolder["${BTS.name}1"]

    val cacheFolder: JioFile

}


enum class BriarDataSplit {
    BRS, BTS
}