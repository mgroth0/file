package matt.file.commons.rcommons

import matt.file.commons.IDE_FOLDER
import matt.file.commons.REGISTERED_FOLDER
import matt.file.commons.rcommons.OpenMindUserStorageLocation.om2
import matt.file.commons.rcommons.OpenMindUserStorageLocation.om5
import matt.file.construct.mFile
import matt.lang.anno.SeeURL

val OM_LOCAL_DATA_FOLDER by lazy {
    mFile("/local/data")
}


const val OM_USER = "mjgroth"

@SeeURL("https://github.mit.edu/MGHPCC/OpenMind/issues/4435")
object OpenMindFiles {

    val OM5_HOME = om5.forMe()
    val OM2_OLD_HOME = mFile("/om2/vast/cbmm/$OM_USER")
    val OM2_HOME = om2.forMe()
    val OM2_TEMP = OM2_HOME["temp"]
    val OM_SINGULARITY_FOLDER = OM2_HOME["singularity"]
    val OM2_REG = OM2_OLD_HOME + REGISTERED_FOLDER.name
    val OM2_IDE = OM2_REG + IDE_FOLDER.name
    val OM_KCOMP = OM2_OLD_HOME["kcomp"]


    val OM_DATA_FOLD = OM2_OLD_HOME["data"]

    val SBATCH_OUTPUT_FOLDER = OM2_HOME["output"]

}

enum class OpenMindUserStorageLocation {
    om2,
    om5;

    fun forUser(user: String) = mFile("/$name/user/$user")
    fun forMe() = forUser(OM_USER)
}


val BRIAR_EXTRACT_FOLDER = OM_LOCAL_DATA_FOLDER["BRS1_extract"]
val BRIAR_EXTRACT_DATA_FOLDER = BRIAR_EXTRACT_FOLDER["data"]
val BRIAR_EXTRACT_METADATA_FOLDER = BRIAR_EXTRACT_FOLDER["metadata.json"]