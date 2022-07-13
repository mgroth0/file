package matt.file.commons

import matt.file.Folder
import matt.file.mFile
import matt.klib.commons.thisMachine
import matt.klib.sys.NEW_MAC
import matt.klib.sys.OLD_MAC
import matt.klib.sys.OPEN_MIND
import matt.klib.sys.WINDOWS


///*need things like this to all be in objects because they are instantiated lazily, and therefore wont be a memory leak issue when trying to have dynamic intelliJ plugins... in general this is definitely the best design and I'm sure this pattern has even broader advantages*/
//object CommonFiles {
val USER_HOME = mFile(thisMachine.homeDir)
val REGISTERED_FOLDER = USER_HOME[thisMachine.registeredDir]
val ICON_FOLDER by lazy { REGISTERED_FOLDER["icon"] }
val BIN_FOLDER = REGISTERED_FOLDER + "bin"
val APPLESCRIPT_FOLDER = (BIN_FOLDER + "applescript").apply { mkdirs() }
val IDE_FOLDER = REGISTERED_FOLDER["IDE"]
val APPLICATIONS_FOLDER = mFile("/Applications")
val DATA_FOLDER = REGISTERED_FOLDER.resolve("data")
val SOUND_FOLDER = REGISTERED_FOLDER + "sound"
val LOG_FOLDER = REGISTERED_FOLDER["log"].apply { mkdir() }
val exceptionFolder = LOG_FOLDER["errorReports"]
val USER_DIR = mFile(System.getProperty("user.dir"))
val TEMP_DIR by lazy { REGISTERED_FOLDER["tmp"].apply { mkdir() } }
val WINDOW_GEOMETRY_FOLDER = DATA_FOLDER["window"]
val SETTINGS_FOLDER = DATA_FOLDER["settings"]
val VAL_JSON_FILE = DATA_FOLDER.resolve("VAL.json")
val VAR_JSON_FILE = DATA_FOLDER["VAR.json"]
val SCREENSHOT_FOLDER = REGISTERED_FOLDER["screenshots"]
val CACHE_FOLDER = REGISTERED_FOLDER["cache"]
val KJG_DATA_FOLDER = DATA_FOLDER.resolve("kjg")
//}

val REL_ROOT_FILES = mFile("RootFiles")
val LIBS_VERSIONS_TOML = "libs.versions.toml"
val REL_LIBS_VERSIONS_TOML = REL_ROOT_FILES + LIBS_VERSIONS_TOML


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


enum class RootProjects {
  /*not adding more yet because I don't want to select from others in KJG*/
  flow, kcomp;

  val folder = IDE_FOLDER + name
  val subRootFolders = listOf(/*folder + "KJ", */folder + "k")
}

val DNN_FOLDER = when (thisMachine) {
  NEW_MAC            -> IDE_FOLDER + "dnn"
  OLD_MAC            -> REGISTERED_FOLDER["todo/science/dnn"]
  WINDOWS, OPEN_MIND -> null
}
val HEP_FOLDER = when (thisMachine) {
  NEW_MAC            -> IDE_FOLDER + "hep"
  OLD_MAC            -> REGISTERED_FOLDER["todo/science/hep"]
  WINDOWS, OPEN_MIND -> null
}

