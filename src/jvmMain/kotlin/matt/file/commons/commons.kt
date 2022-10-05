package matt.file.commons


import matt.file.Folder
import matt.file.construct.mFile
import matt.file.thismachine.thisMachine
import matt.lang.NOT_IMPLEMENTED
import matt.model.sys.NEW_MAC
import matt.model.sys.OLD_MAC
import matt.model.sys.OpenMind
import matt.model.sys.Windows
import java.net.URI
import java.net.URL


const val DEFAULT_GITHUB_BRANCH_NAME = "master"
const val GITHUB_USERNAME = "mgroth0"

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
val APPLICATIONS_FOLDER by lazy { mFile("/Applications") }
val DATA_FOLDER by lazy { REGISTERED_FOLDER.resolve("data") }
val SOUND_FOLDER by lazy { REGISTERED_FOLDER + "sound" }
val LOG_FOLDER by lazy { REGISTERED_FOLDER["log"].apply { mkdir() } }
val exceptionFolder = LOG_FOLDER["errorReports"]
val USER_DIR by lazy { mFile(System.getProperty("user.dir")) }
val TEMP_DIR by lazy { REGISTERED_FOLDER["tmp"].apply { mkdir() } }
val WINDOW_GEOMETRY_FOLDER by lazy { DATA_FOLDER["window"] }
val VAL_JSON_FILE by lazy { DATA_FOLDER.resolve("VAL.json") }


//fun ValJson.Companion.load() = Json.decodeFromString<ValJson>(VAL_JSON_FILE.readText())

val VAR_JSON_FILE by lazy { DATA_FOLDER["VAR.json"] }
val SCREENSHOT_FOLDER by lazy { REGISTERED_FOLDER["screenshots"] }
val CACHE_FOLDER by lazy { REGISTERED_FOLDER["cache"] }
val KJG_DATA_FOLDER by lazy { DATA_FOLDER.resolve("kjg") } //}

//val REL_ROOT_FILES by lazy { mFile("RootFiles") }
val LIBS_VERSIONS_TOML by lazy { "libs.versions.toml" }
//val REL_LIBS_VERSIONS_TOML by lazy { REL_ROOT_FILES + LIBS_VERSIONS_TOML }

val COMMON_LIBS_VERSIONS_FILE by lazy { COMMON_PROJ_FOLDER + LIBS_VERSIONS_TOML }
val GRADLE_PROPERTIES_FILE_NAME by lazy { "gradle.properties" }

val LIBS_VERSIONS_ONLINE_URI by lazy {
  URI(
	"https://raw.githubusercontent.com/$GITHUB_USERNAME/${COMMON_PROJ_FOLDER.name}/$DEFAULT_GITHUB_BRANCH_NAME/$LIBS_VERSIONS_TOML"
  )
}
val LIBS_VERSIONS_ONLINE_URL: URL by lazy { LIBS_VERSIONS_ONLINE_URI.toURL() }


//object CommonFileNames {
const val DS_STORE = ".DS_Store"
const val MODULE_INFO_JAVA_NAME = "module-info.java"
const val BUILDSRC_FILE_NAME = "buildSrc"
const val BUILD_GRADLE_GROOVY_NAME = "build.gradle"
const val SETTINGS_GRADLE_GROOVY_NAME = "settings.gradle"
const val BUILD_GRADLE_KTS_NAME = "build.gradle.kts"
const val SETTINGS_GRADLE_KTS_NAME = "settings.gradle.kts"
const val BUILD_JSON_NAME = "build.json" //}


object MavenLocalFolder: Folder((USER_HOME + ".m2").userPath) {
  object RepoFolder: Folder(resolve("repository").userPath) {
	object MattRepo: Folder(resolve("matt").userPath) {
	  object FlowRepo: Folder(resolve("flow").userPath) {

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

val subRoots = listOf(/*"KJ",*/"k")

enum class IdeProject {
  /*this should be automatically generated*/
  kcomp, all, dnn, hep;

  val folder by lazy { projectFolder + name }
  val subRootFolders by lazy { subRoots.map { folder + it } }
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