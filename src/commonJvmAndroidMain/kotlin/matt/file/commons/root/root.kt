package matt.file.commons.root

import matt.file.construct.mFile
import matt.lang.model.file.MacDefaultFileSystem

val VOLUMES_FOLDER by lazy { mFile("/Volumes", MacDefaultFileSystem) }
val SYS_APPLICATIONS_FOLDER by lazy { mFile("/Applications", MacDefaultFileSystem) }
val YOUR_KIT_APP_FOLDER by lazy {
    /*SYS_APPLICATIONS_FOLDER["YourKit-Java-Profiler-2023.9.app"],*/
    SYS_APPLICATIONS_FOLDER["YourKit-Java-Profiler.app"]
}
val JPROFILER_APP_FOLDER by lazy {
    SYS_APPLICATIONS_FOLDER["JProfiler.app"]
}
