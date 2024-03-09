package matt.file.commons.kextract

import kotlinx.serialization.Serializable
import matt.file.commons.proj.IdeProject.all
import matt.model.code.mod.GradleKSubProjectPath
import matt.model.code.mod.uniqueCamelCaseName
import matt.model.data.message.AbsMacFile

@Serializable
data class KotlinFileExtraction(
    val filePath: AbsMacFile,
    val fullPackageName: String,
    val firstNonExpectActualExposedMember: String?,
    val expectActualIds: Set<String>
)

val globalKotlinExtractionBuildFolderRoot = all.folder["build"]["matt"]["global_kotlin_extraction"]
fun globalKotlinExtractionBuildFolder(
    kSub: GradleKSubProjectPath
) = globalKotlinExtractionBuildFolderRoot[kSub.uniqueCamelCaseName]
