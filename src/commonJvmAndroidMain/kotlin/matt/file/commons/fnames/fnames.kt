package matt.file.commons.fnames

import matt.lang.model.file.FsFileNameImpl
import matt.lang.model.file.MacDefaultFileSystem
import matt.prim.str.lower

object FileNames {
    const val DEFAULT_GIT = ".git"
    const val IDEA_FOLDER = ".idea"
    const val GIT_IGNORE = ".gitignore"
    const val GIT_MODULES = ".gitmodules"
    const val LICENSE = "LICENSE.md"
    const val DEFAULT_FAV_ICO = "default.ico"
    const val FAV_ICO = "favicon.ico"
    const val DOCS_FOLDER = "docs"
    const val STATIC_ROOT = "static"
    const val JPROFILER_CONFIG = "jprofiler_config.xml"
    const val REMOTE_JPROFILER_CONFIG = "jprofiler_config_remote.xml"
    const val PRIV_FOLD = ".private"
    const val HIDDEN_VAGRANT_FOLDER = ".vagrant"
    const val MODULE_INFO_JAVA = "module-info.java"
    const val ANDROID_MANIFEST = "AndroidManifest.xml"
    const val BUILDSRC = "buildSrc"
    const val BUILD_GRADLE_GROOVY = "build.gradle"
    const val SETTINGS_GRADLE_GROOVY = "settings.gradle"
    const val BUILD_GRADLE_KTS = "build.gradle.kts"
    const val SETTINGS_GRADLE_KTS = "settings.gradle.kts"
    const val BUILD_JSON = "build.json"
    const val CACHE_INVALIDATOR_TXT = "cache-invalidator.txt"
    const val KARMA_CONFIG_D = "karma.config.d"
    const val KARMA_CONFIG_JS = "karma.conf.js"
    const val GRADLEW = "gradlew"
    const val CHANGELIST_MD = "changelist.md"
}

val DS_STORE = FsFileNameImpl(".DS_Store", MacDefaultFileSystem)
val DS_STORE_NAME_LOWER = DS_STORE.name.lower()
const val M2_FILE_NAME = ".m2"
val GRADLE_PROPERTIES_FILE_NAME by lazy { "gradle.properties" }
