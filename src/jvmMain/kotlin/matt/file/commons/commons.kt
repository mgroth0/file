package matt.file.commons

import matt.file.Folder
import matt.file.mFile
import matt.klib.commons.DEFAULT_GITHUB_BRANCH_NAME
import matt.klib.commons.GITHUB_USERNAME
import matt.klib.commons.thisMachine
import matt.klib.lang.NOT_IMPLEMENTED
import matt.klib.sys.NEW_MAC
import matt.klib.sys.OLD_MAC
import matt.klib.sys.OPEN_MIND
import matt.klib.sys.Windows
import java.net.URI


///*need things like this to all be in objects because they are instantiated lazily, and therefore wont be a memory leak issue when trying to have dynamic intelliJ plugins... in general this is definitely the best design and I'm sure this pattern has even broader advantages*/
//object CommonFiles {
val USER_HOME by lazy { mFile(thisMachine.homeDir) }
val REGISTERED_FOLDER by lazy { USER_HOME[thisMachine.registeredDir] }
val ICON_FOLDER by lazy { REGISTERED_FOLDER["icon"] }
val BIN_FOLDER by lazy { REGISTERED_FOLDER + "bin" }
val BIN_JAR_FOLDER by lazy { BIN_FOLDER + "jar" }
val APPLESCRIPT_FOLDER by lazy { (BIN_FOLDER + "applescript").apply { mkdirs() } }
val IDE_FOLDER by lazy { REGISTERED_FOLDER["IDE"] }
val COMMON_PROJ_FOLDER by lazy { REGISTERED_FOLDER["common"] }
val APPLICATIONS_FOLDER by lazy { mFile("/Applications") }
val DATA_FOLDER by lazy { REGISTERED_FOLDER.resolve("data") }
val SOUND_FOLDER by lazy { REGISTERED_FOLDER + "sound" }
val LOG_FOLDER by lazy { REGISTERED_FOLDER["log"].apply { mkdir() } }
val exceptionFolder = LOG_FOLDER["errorReports"]
val USER_DIR by lazy { mFile(System.getProperty("user.dir")) }
val TEMP_DIR by lazy { REGISTERED_FOLDER["tmp"].apply { mkdir() } }
val WINDOW_GEOMETRY_FOLDER by lazy { DATA_FOLDER["window"] }
val SETTINGS_FOLDER by lazy { DATA_FOLDER["settings"] }
val VAL_JSON_FILE by lazy { DATA_FOLDER.resolve("VAL.json") }
val VAR_JSON_FILE by lazy { DATA_FOLDER["VAR.json"] }
val SCREENSHOT_FOLDER by lazy { REGISTERED_FOLDER["screenshots"] }
val CACHE_FOLDER by lazy { REGISTERED_FOLDER["cache"] }
val KJG_DATA_FOLDER by lazy { DATA_FOLDER.resolve("kjg") }
//}

val REL_ROOT_FILES by lazy { mFile("RootFiles") }
val LIBS_VERSIONS_TOML by lazy { "libs.versions.toml" }
val REL_LIBS_VERSIONS_TOML by lazy { REL_ROOT_FILES + LIBS_VERSIONS_TOML }

val COMMON_LIBS_VERSIONS_FILE by lazy{ COMMON_PROJ_FOLDER + LIBS_VERSIONS_TOML }

val LIBS_VERSIONS_ONLINE_URI by lazy {
  URI(
	"https://raw.githubusercontent.com/$GITHUB_USERNAME/${COMMON_PROJ_FOLDER.name}/$DEFAULT_GITHUB_BRANCH_NAME/$LIBS_VERSIONS_TOML"
  )
}
val LIBS_VERSIONS_ONLINE_URL by lazy { LIBS_VERSIONS_ONLINE_URI.toURL() }


//object CommonFileNames {
const val DS_STORE = ".DS_Store"
const val MODULE_INFO_JAVA_NAME = "module-info.java"
const val BUILDSRC_FILE_NAME = "buildSrc"
const val BUILD_JSON_NAME = "build.json"
//}


object MavenLocalFolder: Folder((USER_HOME + ".m2").userPath) {
  object RepoFolder: Folder(resolve("repository").userPath) {
	object MattRepo: Folder(resolve("matt").userPath) {
	  object FlowRepo: Folder(resolve("flow").userPath) {

	  }
	}
  }
}

//object KNCommandKeys {
val OPEN_KEY = "OPEN"
val OPEN_RELATIVE_KEY = "OPEN_REL"
val OPEN_NEAREST_GRADLE_BUILDSCRIPT = "OPEN_NEAREST_GRADLE_BUILDSCRIPT"
val OPEN_NEAREST_BUILD_JSON = "OPEN_NEAREST_BUILD_JSON"
val OPEN_NEARST_KOTLIN_DESCENDENT = "OPEN_NEARST_KOTLIN_DESCENDENT"
//}


val KJG_NAV_KEY = "NAV"

private val projectFolder by lazy {
  when (thisMachine) {
	is NEW_MAC, is Windows -> IDE_FOLDER
	is OPEN_MIND           -> mFile(OPEN_MIND.homeDir)
	else                   -> NOT_IMPLEMENTED
  }
}

val subRoots = listOf(/*"KJ",*/"k")

enum class IdeProject {
  /*not adding more yet because I don't want to select from others in KJG*/
  flow, kcomp, all;

  val folder by lazy { projectFolder + name }
  val subRootFolders by lazy { subRoots.map { folder + it } }
}

val JAR_FOLDER by lazy { REGISTERED_FOLDER + "jar" }

val DNN_FOLDER by lazy {
  when (thisMachine) {
	NEW_MAC               -> IDE_FOLDER + "dnn"
	OLD_MAC               -> REGISTERED_FOLDER["todo/science/dnn"]
	is Windows, OPEN_MIND -> null
  }
}
val HEP_FOLDER by lazy {
  when (thisMachine) {
	NEW_MAC               -> IDE_FOLDER + "hep"
	OLD_MAC               -> REGISTERED_FOLDER["todo/science/hep"]
	is Windows, OPEN_MIND -> null
  }
}

const val GRADLEW_NAME = "gradlew"