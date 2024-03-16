package matt.file.types

import matt.file.ext.FileExtension
import matt.file.ext.mightHaveAnExtension
import matt.file.ext.singleExtension
import matt.file.model.file.types.Applescript
import matt.file.model.file.types.BinaryApplescript
import matt.file.model.file.types.Cbor
import matt.file.model.file.types.Dmg
import matt.file.model.file.types.DsStore
import matt.file.model.file.types.Exe
import matt.file.model.file.types.FileType
import matt.file.model.file.types.Folder
import matt.file.model.file.types.FolderType
import matt.file.model.file.types.Groovy
import matt.file.model.file.types.Html
import matt.file.model.file.types.Icns
import matt.file.model.file.types.Ico
import matt.file.model.file.types.Jar
import matt.file.model.file.types.Java
import matt.file.model.file.types.Jpg
import matt.file.model.file.types.Json
import matt.file.model.file.types.Kexe
import matt.file.model.file.types.Kotlin
import matt.file.model.file.types.Log
import matt.file.model.file.types.Markdown
import matt.file.model.file.types.Mp3
import matt.file.model.file.types.Mp4
import matt.file.model.file.types.Pdf
import matt.file.model.file.types.Png
import matt.file.model.file.types.Ppt
import matt.file.model.file.types.Pptx
import matt.file.model.file.types.Properties
import matt.file.model.file.types.Python
import matt.file.model.file.types.RasterImage
import matt.file.model.file.types.Svg
import matt.file.model.file.types.Tiff
import matt.file.model.file.types.Toml
import matt.file.model.file.types.Txt
import matt.file.model.file.types.TypedFile
import matt.file.model.file.types.Unknown
import matt.file.model.file.types.UnknownShellType
import matt.file.model.file.types.Xml
import matt.file.model.file.types.Yaml
import matt.file.model.file.types.Zip
import matt.file.model.file.types.Zsh
import matt.file.model.file.types.asFolder
import matt.lang.model.file.AnyFsFile
import matt.model.obj.text.ReadableFile


fun typedFile(fsFile: AnyFsFile) = fsFile.typed()
fun AnyFsFile.typed() =
    when (this) {
        is TypedFile<*, *> -> this
        else               -> TypedFile(this, getTypedFromExtension())
    }

fun AnyFsFile.verifyToImagePath() = TypedFile(this, getTypedFromExtension() as RasterImage)


fun <T : FileType> AnyFsFile.checkType(t: T) = typed().castedAsType(t)
/*inline fun <reified T : FileType> AnyFsFile.checkType() = typed().castedAsType<T>()




fun <T : FileType> TypedFile<*, *>.checkType(t: T): TypedFile<T, *> {
    check(t::class.isInstance(fileType))
    return this as TypedFile<T, *>
}

inline fun <reified T : FileType> TypedFile<*, *>.checkType(): TypedFile<T, *> {
    check(T::class.isInstance(fileType)) {
        "typecheck failed: $this is not a ${T::class} file"
    }
    return this as TypedFile<T, *>
}
*/


fun <T : FileType> AnyFsFile.forceType(t: T): TypedFile<T, *> = TypedFile(this, t)

/*(this as? TypedFile<T, *>)?.takeIf { fileType == t } ?: TypedFile(this, t)*/



fun AnyFsFile.getTypedFromExtension(): FileType {

    if (!mightHaveAnExtension) return FolderType

    return when {
        !mightHaveAnExtension -> FolderType
        else                  ->
            when (singleExtension) {
                FileExtension.KT                       -> Kotlin
                FileExtension.PY                       -> Python
                FileExtension.JAVA                     -> Java
                FileExtension.GROOVY                   -> Groovy
                FileExtension.SH                       -> UnknownShellType
                FileExtension.ZSHRC, FileExtension.ZSH -> Zsh
                FileExtension.SCPT                     -> BinaryApplescript
                FileExtension.APPLESCRIPT              -> Applescript
                FileExtension.ZIP                      -> Zip
                FileExtension.JAR                      -> Jar
                FileExtension.KEXE                     -> Kexe
                FileExtension.EXE                      -> Exe
                FileExtension.DMG                      -> Dmg
                FileExtension.JSON                     -> Json
                FileExtension.CBOR                     -> Cbor
                FileExtension.XML                      -> Xml
                FileExtension.HTML                     -> Html
                FileExtension.MD                       -> Markdown
                FileExtension.PNG                      -> Png
                FileExtension.JPG, FileExtension.JPEG  -> Jpg
                FileExtension.TIF, FileExtension.TIFF  -> Tiff
                FileExtension.SVG                      -> Svg
                FileExtension.ICN                      -> Icns
                FileExtension.ICO                      -> Ico
                FileExtension.MP3                      -> Mp3
                FileExtension.MP4                      -> Mp4
                FileExtension.PDF                      -> Pdf
                FileExtension.PROPERTIES               -> Properties
                FileExtension.YAML, FileExtension.YML  -> Yaml
                FileExtension.TOML                     -> Toml
                FileExtension.LOG                      -> Log
                FileExtension.TXT                      -> Txt
                FileExtension.DS_Store                 -> DsStore
                FileExtension.PPT                      -> Ppt
                FileExtension.PPTX                     -> Pptx
                else                                   -> Unknown
            }
    }
}


fun ReadableFile<*>.requireIsExistingFolder(): Folder<*> =
    when {
        isDir() -> asFolder()
        else         -> error("$this is not a folder. Does it exist?")
    }
