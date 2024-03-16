package matt.file.model.file.types

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import matt.lang.mime.MimeType
import matt.lang.mime.MimeTypes
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.NewFsFile


object MimeTypeSerializer : KSerializer<MimeType> {
    override val descriptor = serialDescriptor<String>()

    override fun deserialize(decoder: Decoder): MimeType = MimeType.parse(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: MimeType
    ) {
        encoder.encodeString(value.identifier)
    }
}


class TypedFile<out T : FileType, F : NewFsFile<F>>(
    private val fsFile: NewFsFile<F>,
    val fileType: T
) : NewFsFile<F> by fsFile {
    override fun equals(other: Any?): Boolean = fsFile == other

    override fun toString(): String = fsFile.toString()

    override fun hashCode(): Int = fsFile.hashCode()

    fun <R: FileType> castedAsType(newFileType: R): TypedFile<R, F> {
        check(fileType == newFileType)
        return TypedFile(fsFile, newFileType)
    }
}




sealed interface FileType
sealed interface MimeFileType {
    val mimeType: MimeType
}






data object Unknown : FileType
data object FolderType : FileType
typealias AnyFolder = Folder<*>
typealias Folder<F> = TypedFile<FolderType, F>

fun folder(file: AnyFsFile) = file.asFolder()

fun AnyFsFile.asFolder() = TypedFile(this, FolderType)


sealed interface Code : FileType

fun AnyFsFile.asKotlinFile() = TypedFile(this, Kotlin)

data object Kotlin : Code {
    const val FILE_ANNO_LINE_MARKER = "@file:"
}
typealias KotlinFile = TypedFile<Kotlin, *>

data object Python : Code
typealias PythonFile = TypedFile<Python, *>

data object Java : Code
data object Groovy : Code
sealed interface ShellType : FileType
data object UnknownShellType : ShellType
data object Zsh : ShellType
sealed interface Executable : FileType
data object Applescript : Code
data object BinaryApplescript : Executable
sealed interface Archive : FileType
sealed interface BaseZip : Archive
data object Zip : BaseZip
data object Jar : BaseZip
data object Kexe : Executable
data object Exe : Executable
sealed interface DiskImage : FileType
data object Dmg : DiskImage
sealed interface Data : FileType
sealed interface BinaryData : Data
sealed interface HumanReadableData : Data
sealed interface YamlLike : HumanReadableData
data object Json : YamlLike
data object Cbor : BinaryData
sealed interface MarkupLanguage : FileType
data object Xml : HumanReadableData, MarkupLanguage
data object Html : HumanReadableData, MarkupLanguage
data object Markdown : MarkupLanguage
sealed interface ImageType : FileType
sealed interface RasterImage : ImageType
sealed interface VectorImage : ImageType
data object Png : RasterImage, MimeFileType {
    override val mimeType = MimeTypes.PNG
}

data object Jpg : RasterImage
data object Tiff : RasterImage
data object Svg : VectorImage
typealias SvgFile = TypedFile<Svg, *>

data object Icns : RasterImage
data object Ico : RasterImage
typealias ICOFile = TypedFile<Ico, *>

data object Mp3 : FileType
data object Mp4 : FileType
data object Pdf : FileType
sealed interface PowerPointLike : FileType
data object Ppt : PowerPointLike
data object Pptx : PowerPointLike
data object Properties : HumanReadableData
data object Yaml : YamlLike
data object Toml : HumanReadableData
data object Log : FileType
data object Txt : FileType
data object DsStore : HumanReadableData
