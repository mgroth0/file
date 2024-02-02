package matt.file.genin

import kotlinx.serialization.Serializable
import matt.lang.statichier.StaticHierarchy


@Serializable
sealed class ModuleLocation : StaticHierarchy<ModuleLocation>() {
    final override val thisTyped get() = this

    abstract inner class SubLocation : ModuleLocation() {
        init {
            @Suppress("LeakingThis")
            this@ModuleLocation.registerChild(this)
        }
    }

    val isSpecific get() = children.isEmpty()
}


data object ProjectDirectory : ModuleLocation() {
    data object GHWorkflows : SubLocation()
    data object Docs : SubLocation()
    data object Src : SubLocation() {
        data object JvmOrKotlinSrc : SubLocation() {
            data object JvmOrKotlinCodeFolders : SubLocation()
            data object Resources : SubLocation()
            data object AndroidManifest: SubLocation()
        }

        data object Python : SubLocation()
        data object CInterop : SubLocation()
    }

    data object Gradle : SubLocation()
    data object Karma: SubLocation()
}


enum class ProjectFilesModificationType {
    Create, Edit, Delete
}








