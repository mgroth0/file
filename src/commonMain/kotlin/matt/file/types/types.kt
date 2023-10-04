package matt.file.types

import matt.file.ext.FileExtension
import matt.file.ext.mExtension
import matt.lang.`is`
import matt.lang.model.file.FsFile
import matt.lang.model.file.types.Applescript
import matt.lang.model.file.types.BinaryApplescript
import matt.lang.model.file.types.Cbor
import matt.lang.model.file.types.Dmg
import matt.lang.model.file.types.DsStore
import matt.lang.model.file.types.Exe
import matt.lang.model.file.types.FileType
import matt.lang.model.file.types.Folder
import matt.lang.model.file.types.Groovy
import matt.lang.model.file.types.Html
import matt.lang.model.file.types.Icns
import matt.lang.model.file.types.Ico
import matt.lang.model.file.types.Jar
import matt.lang.model.file.types.Java
import matt.lang.model.file.types.Jpg
import matt.lang.model.file.types.Json
import matt.lang.model.file.types.Kexe
import matt.lang.model.file.types.Kotlin
import matt.lang.model.file.types.Log
import matt.lang.model.file.types.Markdown
import matt.lang.model.file.types.Mp3
import matt.lang.model.file.types.Mp4
import matt.lang.model.file.types.Pdf
import matt.lang.model.file.types.Png
import matt.lang.model.file.types.Properties
import matt.lang.model.file.types.Python
import matt.lang.model.file.types.Svg
import matt.lang.model.file.types.Tiff
import matt.lang.model.file.types.Toml
import matt.lang.model.file.types.Txt
import matt.lang.model.file.types.TypedFile
import matt.lang.model.file.types.Unknown
import matt.lang.model.file.types.UnknownShellType
import matt.lang.model.file.types.Xml
import matt.lang.model.file.types.Yaml
import matt.lang.model.file.types.Zip
import matt.lang.model.file.types.Zsh
import matt.lang.model.file.types.asFolder
import matt.model.obj.text.ReadableFile


fun typedFile(fsFile: FsFile) = fsFile.typed()
fun FsFile.typed() = when (this) {
    is TypedFile<*> -> this
    else            -> getTypedFromExtension()
}


fun <T : FileType> FsFile.checkType(t: T) = typed().checkType(t)
inline fun <reified T : FileType> FsFile.checkType() = typed().checkType<T>()
fun <T : FileType> TypedFile<*>.checkType(t: T): TypedFile<T> {
    check(fileType.`is`(t::class))
    @Suppress("UNCHECKED_CAST")
    return this as TypedFile<T>
}

inline fun <reified T : FileType> TypedFile<*>.checkType(): TypedFile<T> {
    check(fileType.`is`(T::class)) {
        "typecheck failed: ${this} is not a ${T::class} file"
    }
    @Suppress("UNCHECKED_CAST")
    return this as TypedFile<T>
}


fun <T : FileType> FsFile.forceType(t: T): TypedFile<T> {
    @Suppress("UNCHECKED_CAST")
    return (this as? TypedFile<T>)?.takeIf { fileType == t } ?: TypedFile(this, t)
//    if ((this as? TypedFile<*>)?.fileType == t) return this
//    check(fileType.`is`(t::class))
//    @Suppress("UNCHECKED_CAST")
//    return this as TypedFile<T>
}

//inline fun <reified T : FileType> FsFile.forceType(): TypedFile<T> {
//
//
//
//    check(fileType.`is`(T::class)) {
//        "typecheck failed: ${this} is not a ${T::class} file"
//    }
//    @Suppress("UNCHECKED_CAST")
//    return this as TypedFile<T>
//}

private fun FsFile.getTypedFromExtension(): TypedFile<*> {

    val fileType = when (mExtension) {
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
        else                                   -> Unknown
    }

    return TypedFile(this, fileType)
}


/*TODO: INTEGRATE WITH MAGNUM*/
fun ReadableFile.requireIsExistingFolder(): Folder {
    return when {
        this.isDir() -> asFolder()
        else         -> error("$this is not a folder. Does it exist?")
    }
}
