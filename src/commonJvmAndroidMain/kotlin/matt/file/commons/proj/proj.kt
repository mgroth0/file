package matt.file.commons.proj

import matt.collect.mapToSet
import matt.file.JioFile
import matt.file.commons.fnames.GRADLE_PROPERTIES_FILE_NAME
import matt.file.commons.proj.IdeProject.all
import matt.file.commons.reg.IDE_FOLDER
import matt.file.construct.mFile
import matt.file.thismachine.thisMachine
import matt.file.toJioFile
import matt.lang.common.NOT_IMPLEMENTED
import matt.lang.common.SubRoots
import matt.model.code.idea.ProjectIdea
import matt.model.code.sys.NewMac
import matt.model.code.sys.OpenMind
import matt.model.code.sys.Windows

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
    val subRootFolders by lazy { SubRoots.entries.mapToSet { subRootFolder(it) } }

    fun subRootFolder(subRoot: SubRoots): JioFile = folder[subRoot.name]
}

val LocatedIdeProject.gradleFolder get() = folder + "gradle"
val LocatedIdeProject.yarnLockFile get() = folder + "yarn.lock"
val gradlePropertiesFile by lazy {
    all.folder[GRADLE_PROPERTIES_FILE_NAME].toJioFile()
}
