package matt.file.commons.home

import matt.file.commons.fnames.M2_FILE_NAME
import matt.file.construct.mFile
import matt.file.thismachine.thisMachine

val MavenLocalFolder by lazy {
    USER_HOME + ".m2"
}
val USER_HOME by lazy {
    mFile(thisMachine.homeDir, thisMachine.fileSystemFor(thisMachine.homeDir))
}
val M2 by lazy { USER_HOME + M2_FILE_NAME }
val DOWNLOADS_FOLDER by lazy { USER_HOME + "Downloads" }
val RepoFolder by lazy {
    MavenLocalFolder + "repository"
}
val MattRepo by lazy {
    RepoFolder + "matt"
}
val FlowRepo by lazy {
    MattRepo + "flow"
}
