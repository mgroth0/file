@file:JvmName("ContextJvmAndroidKt")

package matt.file.context

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import matt.file.JioFile
import matt.file.commons.GRADLE_PROPERTIES_FILE_NAME
import matt.file.commons.JPROFILER_CONFIG_NAME
import matt.file.commons.USER_HOME
import matt.file.commons.ec2commons.Ec2Files
import matt.file.commons.hcommons.HerokuExecutionContextFiles
import matt.file.commons.lcommons.LocalComputeContextFiles
import matt.file.commons.rcommons.OpenMindComputeContextFiles
import matt.file.commons.rcommons.OpenMindFiles
import matt.file.construct.mFile
import matt.file.context.BriarDataSplit.BRS
import matt.file.context.BriarDataSplit.BTS
import matt.file.context.ContainerType.Docker
import matt.file.context.ContainerType.Singularity
import matt.file.props.loadProperties
import matt.file.thismachine.thisMachine
import matt.file.toJioFile
import matt.lang.context.DEFAULT_LINUX_PROGRAM_PATH_CONTEXT
import matt.lang.context.DEFAULT_MAC_PROGRAM_PATH_CONTEXT
import matt.lang.context.DEFAULT_WINDOWS_PROGRAM_PATH_CONTEXT
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.AnyResolvableFileOrUrl
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
import matt.model.code.sys.WindowsFileSystem

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
    val needsModules: Boolean
    val usesJavaIn: ContainerType?
    val javaHome: UnsafeFilePath?
    val taskLabel: String
    override val os: OsEnum

}

val ProcessContext.fileSystem
    get() = when (os) {
        Mac     -> MacFileSystem
        Linux   -> LinuxFileSystem
        Windows -> WindowsFileSystem
    }

enum class ContainerType { Singularity, Docker }

@Serializable
sealed interface ComputeContext : ProcessContext {
    override val files: ComputeContextFiles
}

@Serializable
sealed interface BriarComputeContext : ComputeContext {
    override val files: BriarContextFiles
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
data object OpenMindComputeContext : ComputeContextImpl(), BriarComputeContext {
    override val needsModules = true
    override val javaHome = null
    override val usesJavaIn = Singularity
    override val taskLabel = "OpenMind"
    override val os = Linux
    override val files by lazy {
        OpenMindComputeContextFiles()
    }


}

@Serializable
@SerialName("Local")
data object LocalComputeContext : ComputeContextImpl(), BriarComputeContext {
    override val os = Mac
    override val needsModules = false
    override val usesJavaIn = null
    override val javaHome by lazy {
        GRADLE_JAVA_HOME.toUnsafe()
    }
    override val taskLabel = "Local"
    override val files by lazy {
        LocalComputeContextFiles()
    }

}


@Serializable
@SerialName("Heroku")
data object HerokuProcessContext : ExecutionContextImpl() {
    override val files: ProcessContextFiles get() = HerokuExecutionContextFiles
    override val needsModules = false
    override val usesJavaIn = Docker
    override val javaHome get() = TODO("Not sure what to do here since it is in docker")
    override val taskLabel = "Heroku"
    override val os: OsEnum get() = Linux
}


const val EC2_JAVA_VERSION = 17

@Serializable
@SerialName("Ec2")
data object Ec2ProcessContext : ExecutionContextImpl(), ComputeContext {
    override val files get() = Ec2Files
    override val needsModules = false
    override val usesJavaIn = null
    override val javaHome by lazy {
        UnsafeFilePath("/usr/lib/jvm/java-$EC2_JAVA_VERSION-openjdk-arm64")
    }
    override val os: OsEnum get() = Linux
    override val taskLabel = "EC2"
}

val GRADLE_JAVA_HOME by lazy {
    val s = (USER_HOME + ".gradle" + GRADLE_PROPERTIES_FILE_NAME).loadProperties()["org.gradle.java.home"].toString()
    with(thisMachine.fileSystemFor(s)) {
        mFile(s)
    }
}

abstract class ProcessContextFiles {
    abstract val libjprofilertiPath: String
    abstract val jpenable: AnyFsFile
    abstract val jProfilerConfigFile: AnyFsFile
    abstract val yourKitAttachScript: AnyFsFile
    abstract val om2Home: AnyFsFile
    val tempFolder get() = om2Home["temp"]
    val snapshotFolder get() = tempFolder["jprofiler"]
    val latestJpSnapshot get() = snapshotFolder["latest.jps"]
}

abstract class ComputeContextFiles : ProcessContextFiles() {


    abstract val defaultPathPrefix: AnyResolvableFileOrUrl
    final override val om2Home
        get() = mFile(
            defaultPathPrefix[OpenMindFiles.OM2_HOME.path.removePrefix(JioFile.unixSeparator)].path,
            LinuxFileSystem
        ).toJioFile()

    final override val jProfilerConfigFile: AnyFsFile get() = om2Home[JPROFILER_CONFIG_NAME]
    val jarsFolder get() = om2Home["jars"]

    val rTaskOutputs get() = om2Home["rTaskOutputs"]

    private val batchTaskFolder get() = om2Home["batch"]
    fun batchTaskFiles(batchTaskId: BatchTaskId) = BatchTaskFiles(batchTaskFolder[batchTaskId.name])


}


abstract class BriarContextFiles : ComputeContextFiles() {
    companion object {
        const val BRIAR_EXTRACT_METADATA_FILE_NAME = "metadata.json"
        const val BRIAR_EXTRACT_MINIMAL_METADATA_FILE_NAME = "metadata_minimal.cbor"
    }

    abstract val briarDataFolder: AnyFsFile

    abstract val briarExtractsFolder: JioFile
    abstract val briarGlobalCacheFolder: JioFile


    val brs1Folder get() = briarDataFolder["${BRS.name}1"]
    val bts1Folder get() = briarDataFolder["${BTS.name}1"]

    abstract val briarCacheFolder: JioFile
}


enum class BatchTaskId {
    extract
}

class BatchTaskFiles(private val root: AnyFsFile) {
    val outputFolder by lazy { root["output"] }
    val sBatchScript by lazy { mFile(root["script.sh"].path, LinuxFileSystem) }
    val sBatchScriptJson by lazy { mFile(sBatchScript.path + ".json", LinuxFileSystem) }
}


enum class BriarDataSplit {
    BRS, BTS
}