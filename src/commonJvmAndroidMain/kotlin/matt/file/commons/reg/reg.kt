package matt.file.commons.reg

import matt.file.commons.home.USER_HOME
import matt.file.commons.logctx.LogContext
import matt.file.thismachine.thisMachine
import matt.file.toJioFile

val REGISTERED_FOLDER by lazy {
    with(USER_HOME.myFileSystem) {
        val reg = thisMachine.registeredDir
        reg?.let {
            val reg2 = USER_HOME[it]
            reg2.toJioFile()
        }
            ?: matt.file.ext.j.createTempDir(prefix = "registered")
    }
}
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
val CONTROLLED_LOCAL_MAVEN_FOLDER = REGISTERED_FOLDER["maven"]["controlled"]
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
val DATA_FOLDER by lazy { REGISTERED_FOLDER.resolve("data").toJioFile() }
val DATA_IARPA_FOLDER = DATA_FOLDER["iarpa"]
val DEEPHYS_DATA_FOLDER by lazy { DATA_FOLDER["deephy"] }
val SOUND_FOLDER by lazy { REGISTERED_FOLDER + "sound" }
val LOG_FOLDER by lazy { REGISTERED_FOLDER["log"].apply { mkdir() } }
val mattLogContext by lazy { LogContext(parentFolder = REGISTERED_FOLDER) }
val VAR_JSON_FILE by lazy { DATA_FOLDER["VAR.json"] }
val SCREENSHOT_FOLDER by lazy { REGISTERED_FOLDER["screenshots"] }
val CACHE_FOLDER by lazy { REGISTERED_FOLDER["cache"] }
val TEMP_DIR by lazy { REGISTERED_FOLDER["tmp"].apply { mkdir() } }
val JAR_FOLDER by lazy { REGISTERED_FOLDER + "jar" }
val TEST_DATA_FOLDER = DATA_FOLDER["test"]
