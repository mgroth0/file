package matt.file.commons


import matt.file.Folder
import matt.file.MFile
import matt.file.construct.mFile
import matt.file.ext.FileExtension
import matt.file.numbered.NumberedFiles
import matt.file.thismachine.thisMachine
import matt.lang.NOT_IMPLEMENTED
import matt.lang.anno.SeeURL
import matt.lang.userName
import matt.model.code.idea.ProjectIdea
import matt.model.code.sys.Linux
import matt.model.code.sys.Mac
import matt.model.code.sys.NEW_MAC
import matt.model.code.sys.OLD_MAC
import matt.model.code.sys.OpenMind
import matt.model.code.sys.Windows


const val DEFAULT_GITHUB_BRANCH_NAME = "master"


///*need things like this to all be in objects because they are instantiated lazily, and therefore wont be a memory leak issue when trying to have dynamic intelliJ plugins... in general this is definitely the best design and I'm sure this pattern has even broader advantages*/
//object CommonFiles {
val USER_HOME by lazy { mFile(thisMachine.homeDir) }


const val M2_FILE_NAME = ".m2"
val M2 by lazy { USER_HOME + M2_FILE_NAME }
val REGISTERED_FOLDER by lazy {
    thisMachine.registeredDir?.let { USER_HOME[it] }
        ?: matt.file.ext.createTempDir(prefix = "registered")
}
val GBUILD_FOLDER by lazy { REGISTERED_FOLDER + "gbuild" }
val GBUILD_JAR_FOLDER by lazy { GBUILD_FOLDER + "jar" }
val GBUILD_DIST_FOLDER by lazy { GBUILD_FOLDER + "dist" }
val DOWNLOADS_FOLDER by lazy { USER_HOME + "Downloads" }
val ICON_FOLDER by lazy { REGISTERED_FOLDER["icon"] }
val BIN_FOLDER by lazy { REGISTERED_FOLDER + "bin" }
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
val SYS_APPLICATIONS_FOLDER by lazy { mFile("/Applications") }
val YOUR_KIT_APP_FOLDER by lazy {
    /*SYS_APPLICATIONS_FOLDER["YourKit-Java-Profiler-2022.9.app"]*/
    SYS_APPLICATIONS_FOLDER["YourKit-Java-Profiler-2023.5.app"]
}
val JPROFILER_APP_FOLDER by lazy {
    SYS_APPLICATIONS_FOLDER["JProfiler.app"]
}
val DATA_FOLDER by lazy { REGISTERED_FOLDER.resolve("data") }
val DEEPHYS_DATA_FOLDER by lazy { DATA_FOLDER["deephy"] }
val SOUND_FOLDER by lazy { REGISTERED_FOLDER + "sound" }


private val LOG_FOLDER by lazy { REGISTERED_FOLDER["log"].apply { mkdir() } }

class LogContext(parentFolder: MFile) {
    val logFolder by lazy {
        parentFolder["log"].apply { mkdirs() }
    }
    val exceptionFolder by lazy {
        logFolder["errorReports"]
    }
}

val mattLogContext by lazy { LogContext(parentFolder = REGISTERED_FOLDER) }


val USER_DIR by lazy { mFile(System.getProperty("user.dir")) }
val TEMP_DIR by lazy { REGISTERED_FOLDER["tmp"].apply { mkdir() } }


//fun ValJson.Companion.load() = Json.decodeFromString<ValJson>(VAL_JSON_FILE.readText())

val VAR_JSON_FILE by lazy { DATA_FOLDER["VAR.json"] }
val SCREENSHOT_FOLDER by lazy { REGISTERED_FOLDER["screenshots"] }
val CACHE_FOLDER by lazy { REGISTERED_FOLDER["cache"] }
val KJG_DATA_FOLDER by lazy { DATA_FOLDER.resolve("kjg") } //}


val GRADLE_PROPERTIES_FILE_NAME by lazy { "gradle.properties" }


//object CommonFileNames {


const val DS_STORE = ".DS_Store"
const val MODULE_INFO_JAVA_NAME = "module-info.java"
const val ANDROID_MANIFEST_NAME = "AndroidManifest.xml"
const val BUILDSRC_FILE_NAME = "buildSrc"
const val BUILD_GRADLE_GROOVY_NAME = "build.gradle"
const val SETTINGS_GRADLE_GROOVY_NAME = "settings.gradle"
const val BUILD_GRADLE_KTS_NAME = "build.gradle.kts"
const val SETTINGS_GRADLE_KTS_NAME = "settings.gradle.kts"
const val BUILD_JSON_NAME = "build.json"
const val CACHE_INVALIDATOR_TXT = "cache-invalidator.txt"

//}


object MavenLocalFolder : Folder((USER_HOME + ".m2").userPath) {
    object RepoFolder : Folder(resolve("repository").userPath) {
        object MattRepo : Folder(resolve("matt").userPath) {
            object FlowRepo : Folder(resolve("flow").userPath) {

            }
        }
    }
}


val KJG_NAV_KEY = "NAV"

private val projectFolder by lazy {
    when (thisMachine) {
        is NEW_MAC, is Windows -> IDE_FOLDER
        is OpenMind            -> mFile(thisMachine.homeDir)
        else                   -> NOT_IMPLEMENTED
    }
}

enum class SubRoots {
    k
}

//val subRoots = listOf(/*"KJ",*/"k")

enum class IdeProject : ProjectIdea {
    /*this should be automatically generated*/
    kcomp, all, dnn, hep;

    val folder by lazy { projectFolder + name }
    val subRootFolders by lazy { SubRoots.values().map { folder + it.name } }
}


val JAR_FOLDER by lazy { REGISTERED_FOLDER + "jar" }
val JAR_INSIGHT_FOLDER by lazy { JAR_FOLDER + "insight" }

val DNN_FOLDER by lazy {
    when (thisMachine) {
        NEW_MAC -> IDE_FOLDER + "dnn"
        OLD_MAC -> REGISTERED_FOLDER["todo/science/dnn"]
        else    -> null
    }
}
val HEP_FOLDER by lazy {
    when (thisMachine) {
        NEW_MAC -> IDE_FOLDER + "hep"
        OLD_MAC -> REGISTERED_FOLDER["todo/science/hep"]
        else    -> null
    }
}

const val GRADLEW_NAME = "gradlew"


val desktopFile by lazy { mFile(System.getProperty("user.home")).resolve("Desktop") }

const val CHANGELIST_MD = "changelist.md"


val FILE_ACCESS_CHECK_FILE by lazy { USER_DIR + "Desktop" + ".FileAccessCheck.txt" }
fun hasFullFileAccess() = FILE_ACCESS_CHECK_FILE.exists()


val TEST_DATA_FOLDER = DATA_FOLDER["test"]
val DEEPHYS_TEST_DATA_FOLDER = TEST_DATA_FOLDER["deephys"]
val DEEPHYS_TEST_RESULT_JSON = DEEPHYS_TEST_DATA_FOLDER["results.json"]
val DEEPHYS_RAM_SAMPLES_FOLDER = DEEPHYS_TEST_DATA_FOLDER["ram"]
val RAM_NUMBERED_FILES by lazy {
    NumberedFiles(
        folder = DEEPHYS_RAM_SAMPLES_FOLDER,
        prefix = "",
        extension = FileExtension.JSON
    )
}

val USER_LIB_FOLDER by lazy {
    require(thisMachine is Mac)
    USER_HOME["Library"]
}

val APP_SUPPORT_FOLDER by lazy {
    require(thisMachine is Mac)
    USER_LIB_FOLDER["Application Support"]
}


val PLATFORM_INDEPENDENT_APP_SUPPORT_FOLDER by lazy {
    when (thisMachine) {
        is Mac     -> APP_SUPPORT_FOLDER
        is Linux   -> {
            @SeeURL("https://stackoverflow.com/questions/6561172/find-directory-for-application-data-on-linux-and-macintosh")
            USER_HOME[".matt"].also { it.mkdir() }
        }

        is Windows -> {
            mFile("C:\\Users\\${userName}\\AppData\\Roaming")
        }
    }
}

val THREE_D_PRINT_FOLDER = REGISTERED_FOLDER["3dprint"]

val DEFAULT_GIT_FILE_NAME = ".git"
val GIT_IGNORE_FILE_NAME = ".gitignore"
val GIT_MODULES_FILE_NAME = ".gitmodules"

val LICENSE_FILE_NAME = "LICENSE.md"


const val DEFAULT_FAV_ICO_NAME = "default.ico"
const val FAV_ICO_NAME = "favicon.ico"
const val DOCS_FOLDER_NAME = "docs"
const val STATIC_ROOT_NAME = "static"