package matt.file.genin

import kotlinx.serialization.Serializable
import matt.lang.anno.Open
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
            data object Resources : SubLocation() {
                data object AndroidProviderPaths : SubLocation()
                data object AndroidShortcuts : SubLocation()
                data object AndroidThemes : SubLocation()
                data object ObsoleteResources: SubLocation()
                data object ModId: SubLocation()
                data object ValuesJson: SubLocation()
                data object ChangelistFile: SubLocation()
                data object NativeBinaries: SubLocation()
                data object IdeaPluginXml: SubLocation()
            }
            data object AndroidManifest: SubLocation() {
                data object MainAndroidManifest : SubLocation()
                data object UnitTestAndroidManifest : SubLocation()
                data object InstrumentedTestAndroidManifest : SubLocation()
            }
        }

        data object Python : SubLocation()
        data object CInterop : SubLocation()
    }

    data object Gradle : SubLocation()
    data object Karma: SubLocation()
}

@Serializable
sealed interface ProjectFilesModificationType {

    @Open val creates: Boolean get() = false
    @Open val edits: Boolean  get() = false
    @Open val deletes: Boolean  get() = false

    fun does(other: ProjectFilesModificationType): Boolean

    @Serializable
    data object Manage: ProjectFilesModificationType {
        override val creates = true
        override val edits = true
        override val deletes = true
        override fun does(other: ProjectFilesModificationType) = true
    }

    @Serializable
    data object Create: ProjectFilesModificationType {
        override val creates = true
        /*does this not also count as editing??? Maybe not... ... maybe so*/
        override fun does(other: ProjectFilesModificationType) = other == Create
    }
    @Serializable
    data object Edit: ProjectFilesModificationType {
        override val edits = true
        override fun does(other: ProjectFilesModificationType) = other == Edit
    }
    @Serializable
    data object Delete: ProjectFilesModificationType {
        override val deletes = true
        /*does this not also count as editing??? Maybe not... ... maybe so*/
        override fun does(other: ProjectFilesModificationType) = other == Delete
    }
}









