package matt.file.commons


import matt.file.JioFile
import matt.file.commons.IdeProject.all
import matt.file.commons.ec2commons.DEFAULT_UBUNTU_HOME_FOLDER
import matt.file.construct.mFile
import matt.file.ext.FileExtension
import matt.file.numbered.NumberedFiles
import matt.file.thismachine.thisMachine
import matt.file.toJioFile
import matt.lang.NOT_IMPLEMENTED
import matt.lang.SubRoots
import matt.lang.anno.SeeURL
import matt.lang.assertions.require.requireIs
import matt.lang.model.file.FsFile
import matt.lang.model.file.FsFileNameImpl
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.types.asFolder
import matt.lang.sysprop.props.UserDir
import matt.lang.sysprop.props.UserHome
import matt.lang.userName
import matt.model.code.idea.ProjectIdea
import matt.model.code.sys.Linux
import matt.model.code.sys.LinuxFileSystem
import matt.model.code.sys.Mac
import matt.model.code.sys.NewMac
import matt.model.code.sys.OpenMind
import matt.model.code.sys.Windows
import matt.model.code.sys.WindowsFileSystem


const val DEFAULT_GITHUB_BRANCH_NAME = "master"


///*need things like this to all be in objects because they are instantiated lazily, and therefore wont be a memory leak issue when trying to have dynamic intelliJ plugins... in general this is definitely the best design and I'm sure this pattern has even broader advantages*/
//object CommonFiles {
val USER_HOME by lazy { mFile(thisMachine.homeDir, thisMachine.fileSystemFor(thisMachine.homeDir)) }

val VOLUMES_FOLDER by lazy { mFile("/Volumes", MacFileSystem) }
val StupidLinuxVOLUMES_FOLDER by lazy { mFile("/Volumes", LinuxFileSystem) }

const val M2_FILE_NAME = ".m2"
val M2 by lazy { USER_HOME + M2_FILE_NAME }
val REGISTERED_FOLDER by lazy {
    with(USER_HOME.fileSystem) {
        thisMachine.registeredDir?.let { USER_HOME[it].toJioFile() }
            ?: matt.file.ext.createTempDir(prefix = "registered")
    }
}
const val BRAINSTORM_KEY_FILE_NAME = ".BRAINSTORM"
val VIDEO_INDEX_FOLDER by lazy { REGISTERED_FOLDER["VideoIndex"] }
val CHROMEDRIVER_FOLDER by lazy {
    REGISTERED_FOLDER["chromedriver"]
}
val CHROMEDRIVER_EXECUTABLE by lazy {
    CHROMEDRIVER_FOLDER["chromedriver"]
}

val CHROMEDRIVER_LAST_DOWNLOAD_FILE by lazy {
    CHROMEDRIVER_FOLDER["last-downloaded-version.json"]
}

val BACKUP_FOLDER by lazy {
    REGISTERED_FOLDER["backup"]
}
val GBUILD_FOLDER by lazy { REGISTERED_FOLDER + "gbuild" }
val GBUILD_JAR_FOLDER by lazy { GBUILD_FOLDER + "jar" }
val GBUILD_DIST_FOLDER by lazy { GBUILD_FOLDER + "dist" }
private val GBUILD_MAVEN_FOLDER by lazy { GBUILD_FOLDER + "maven" }
val GBUILD_MAVEN_CURRENT_FOLDER by lazy { GBUILD_MAVEN_FOLDER + "current" }
private val GBUILD_MAVEN_SNAPSHOTS_FOLDER by lazy { GBUILD_MAVEN_FOLDER + "snapshots" }
fun gbuildMavenSnapshotFolder(num: Int) = GBUILD_MAVEN_SNAPSHOTS_FOLDER[num.toString()]
val DOWNLOADS_FOLDER by lazy { USER_HOME + "Downloads" }
val ICON_FOLDER by lazy { REGISTERED_FOLDER["icon"] }
val BIN_FOLDER by lazy { REGISTERED_FOLDER + "bin" }
val BIN_JS_FOLDER by lazy { BIN_FOLDER + "js" }
val BIN_BIN by lazy { BIN_FOLDER + "bin" }
val BIN_BIN_BIN by lazy { BIN_BIN + "bin" }
val BIN_BIN_BIN_BIN by lazy { BIN_BIN_BIN + "bin" }
val DIST_FOLDER by lazy { BIN_FOLDER + "dist" }
val APP_FOLDER by lazy { BIN_FOLDER + "app" }
val BIN_NATIVE_FOLDER by lazy { BIN_FOLDER + "native" }
val BIN_JAR_FOLDER by lazy { BIN_FOLDER + "jar" }

val APPLESCRIPT_FOLDER by lazy { (BIN_FOLDER + "applescript").apply { mkdirs() } }
val IDE_FOLDER by lazy { REGISTERED_FOLDER["ide"] }
val COMMON_PROJ_FOLDER by lazy { REGISTERED_FOLDER["common"] }
val SYS_APPLICATIONS_FOLDER by lazy { mFile("/Applications", MacFileSystem) }
val YOUR_KIT_APP_FOLDER by lazy {
    /*SYS_APPLICATIONS_FOLDER["YourKit-Java-Profiler-2022.9.app"]*/
    /*SYS_APPLICATIONS_FOLDER["YourKit-Java-Profiler-2023.5.app"]*/
    /*SYS_APPLICATIONS_FOLDER["YourKit-Java-Profiler-2023.9.app"],*/
    SYS_APPLICATIONS_FOLDER["YourKit-Java-Profiler.app"]
}
val JPROFILER_APP_FOLDER by lazy {
    SYS_APPLICATIONS_FOLDER["JProfiler.app"]
}
val DATA_FOLDER by lazy { REGISTERED_FOLDER.resolve("data").toJioFile() }
val DATA_IARPA_FOLDER = DATA_FOLDER["iarpa"]
val DEEPHYS_DATA_FOLDER by lazy { DATA_FOLDER["deephy"] }
val SOUND_FOLDER by lazy { REGISTERED_FOLDER + "sound" }


val LOG_FOLDER by lazy { REGISTERED_FOLDER["log"].apply { mkdir() } }

class LogContext(parentFolder: FsFile) {
    val logFolder by lazy {
        parentFolder["log"].toJioFile().apply { mkdirs() }
    }
    val exceptionFolder by lazy {
        logFolder["errorReports"]
    }
}

val mattLogContext by lazy { LogContext(parentFolder = REGISTERED_FOLDER) }


val USER_DIR by lazy { mFile(UserDir.get(), thisMachine.fileSystemFor(UserDir.get())) }
val TEMP_DIR by lazy { REGISTERED_FOLDER["tmp"].apply { mkdir() } }
val WEB_TMP_DIR by lazy {
    val s = "/tmp"
    mFile(s, thisMachine.fileSystemFor(s)).also { it.toJioFile().mkdir() }
}


//fun ValJson.Companion.load() = Json.decodeFromString<ValJson>(VAL_JSON_FILE.readText())

val VAR_JSON_FILE by lazy { DATA_FOLDER["VAR.json"] }
val SCREENSHOT_FOLDER by lazy { REGISTERED_FOLDER["screenshots"] }
val CACHE_FOLDER by lazy { REGISTERED_FOLDER["cache"] }
val KJG_DATA_FOLDER by lazy { DATA_FOLDER.resolve("kjg") } //}


val GRADLE_PROPERTIES_FILE_NAME by lazy { "gradle.properties" }
val gradlePropertiesFile by lazy {
    all.folder[GRADLE_PROPERTIES_FILE_NAME].toJioFile()
}

//object CommonFileNames {


val DS_STORE = FsFileNameImpl(".DS_Store", MacFileSystem)
const val MODULE_INFO_JAVA_NAME = "module-info.java"
const val ANDROID_MANIFEST_NAME = "AndroidManifest.xml"
const val BUILDSRC_FILE_NAME = "buildSrc"
const val BUILD_GRADLE_GROOVY_NAME = "build.gradle"
const val SETTINGS_GRADLE_GROOVY_NAME = "settings.gradle"
const val BUILD_GRADLE_KTS_NAME = "build.gradle.kts"
const val SETTINGS_GRADLE_KTS_NAME = "settings.gradle.kts"
const val BUILD_JSON_NAME = "build.json"
const val CACHE_INVALIDATOR_TXT = "cache-invalidator.txt"
const val KARMA_CONFIG_D_NAME = "karma.config.d"

//}


val MavenLocalFolder by lazy {
    USER_HOME + ".m2"
}
val RepoFolder by lazy {
    MavenLocalFolder + "repository"
}
val MattRepo by lazy {
    RepoFolder + "matt"
}
val FlowRepo by lazy {
    MattRepo + "flow"
}
//object MavenLocalFolder : FsFile((USER_HOME + ).userPath) {
//    object RepoFolder : FsFile(resolve("repository").userPath) {
//        object MattRepo : FsFile(resolve("matt").userPath) {
//            object FlowRepo : FsFile(resolve("flow").userPath) {
//
//            }
//        }
//    }
//}


val KJG_NAV_KEY = "NAV"

private val projectFolder by lazy {
    when (thisMachine) {
        is NewMac, is Windows -> IDE_FOLDER
        is OpenMind           -> mFile(thisMachine.homeDir, thisMachine.fileSystemFor(thisMachine.homeDir)).toJioFile()
        else                  -> NOT_IMPLEMENTED
    }
}


interface LocatedIdeProject : ProjectIdea {
    val folder: JioFile
}

class AnIdeProject(override val folder: JioFile) : LocatedIdeProject

enum class IdeProject : LocatedIdeProject {
    /*this should be automatically generated*/
    kcomp, all, dnn, hep;

    override val folder by lazy { projectFolder + name }
    val subRootFolders by lazy { SubRoots.entries.map { folder + it.name } }
}

val LocatedIdeProject.gradleFolder get() = folder + "gradle"
val LocatedIdeProject.yarnLockFile get() = folder + "yarn.lock"


val JAR_FOLDER by lazy { REGISTERED_FOLDER + "jar" }
val JAR_INSIGHT_FOLDER by lazy { JAR_FOLDER + "insight" }

val DNN_FOLDER by lazy {
    when (thisMachine) {
        NewMac -> IDE_FOLDER + "dnn"
        else   -> null
    }
}
val HEP_FOLDER by lazy {
    when (thisMachine) {
        NewMac -> IDE_FOLDER + "hep"
        else   -> null
    }
}

const val GRADLEW_NAME = "gradlew"


val desktopFolder by lazy { with(thisMachine.fileSystemFor(UserHome.get())) { mFile(UserHome.get())["Desktop"] } }

const val CHANGELIST_MD = "changelist.md"


val FILE_ACCESS_CHECK_FILE by lazy { USER_DIR + "Desktop" + ".FileAccessCheck.txt" }
fun hasFullFileAccess() = FILE_ACCESS_CHECK_FILE.toJioFile().exists()


val TEST_DATA_FOLDER = DATA_FOLDER["test"]
val DEEPHYS_TEST_DATA_FOLDER = TEST_DATA_FOLDER["deephys"]
val DEEPHYS_TEST_RESULT_JSON = DEEPHYS_TEST_DATA_FOLDER["results.json"].toJioFile()
val DEEPHYS_RAM_SAMPLES_FOLDER = DEEPHYS_TEST_DATA_FOLDER["ram"]
val RAM_NUMBERED_FILES by lazy {
    NumberedFiles(
        folder = DEEPHYS_RAM_SAMPLES_FOLDER.asFolder(),
        prefix = "",
        extension = FileExtension.JSON
    )
}

val USER_LIB_FOLDER by lazy {
    requireIs<Mac>(thisMachine)
    USER_HOME["Library"]
}

val APP_SUPPORT_FOLDER by lazy {
    requireIs<Mac>(thisMachine)
    USER_LIB_FOLDER["Application Support"]
}


val PLATFORM_INDEPENDENT_APP_SUPPORT_FOLDER by lazy {
    when (thisMachine) {
        is Mac     -> APP_SUPPORT_FOLDER
        is Linux   -> {
            @SeeURL("https://stackoverflow.com/questions/6561172/find-directory-for-application-data-on-linux-and-macintosh")
            USER_HOME[".matt"].also { it.toJioFile().mkdir() }
        }

        is Windows -> {
            mFile("C:\\Users\\${userName}\\AppData\\Roaming", WindowsFileSystem)
        }
    }
}

val THREE_D_PRINT_FOLDER = REGISTERED_FOLDER["3dprint"]

val DEFAULT_GIT_FILE_NAME = ".git"
val IDEA_FOLDER_NAME = ".idea"
val GIT_IGNORE_FILE_NAME = ".gitignore"
val GIT_MODULES_FILE_NAME = ".gitmodules"

val LICENSE_FILE_NAME = "LICENSE.md"


const val DEFAULT_FAV_ICO_NAME = "default.ico"
const val FAV_ICO_NAME = "favicon.ico"
const val DOCS_FOLDER_NAME = "docs"
const val STATIC_ROOT_NAME = "static"

const val DockerfileName = "Dockerfile"

const val JPROFILER_CONFIG_NAME = "jprofiler_config.xml"
const val REMOTE_JPOFILER_CONFIG_FILE_NAME = "jprofiler_config_remote.xml"


const val PRIV_FOLD_NAME = ".private"


const val HIDDEN_VAGRANT_FOLDER_NAME = ".vagrant"


class RedisCertFiles(private val dir: FsFile) {
    fun mkdirs() = dir.toJioFile().mkdirs()
    val privateKeyFile = dir["redis-private.key"]
    val csrFile = dir["redis.csr"]
    val certFile = dir["redis.cert"]
}

val remoteSharableCertFiles = RedisCertFiles(DEFAULT_UBUNTU_HOME_FOLDER)