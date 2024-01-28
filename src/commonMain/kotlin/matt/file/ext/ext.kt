package matt.file.ext

import matt.lang.assertions.require.requireNotEndsWith
import matt.lang.model.file.AnyFsFile
import matt.lang.model.file.fName
import matt.model.code.delegate.SimpleGetter
import matt.prim.str.lower
import kotlin.jvm.JvmInline

fun AnyFsFile.finalExtensionOrNull(): FileExtension? {
    return fName.substringAfterLast(".", missingDelimiterValue = "").takeIf { it.isNotEmpty() }?.let(::FileExtension)
}

val AnyFsFile.singleExtensionOrNullIfNoDots: FileExtension?
    get() {
        val numDots = fName.count { it == '.' }
        if (numDots == 0) return null
        check(numDots == 1) {
            "multiple dots in $fName"
        }
        val afterDot = fName.substringAfter(".", missingDelimiterValue = "")
        check(afterDot.isNotEmpty())
        return FileExtension(afterDot)
    }

val AnyFsFile.mightHaveAnExtension get() = "." in name

val AnyFsFile.singleExtension: FileExtension
    get() {
        check(fName.count { it == '.' } == 1) {
            "multiple or 0 dots in name of $path"
        }
        val afterDot = fName.substringAfter(".", missingDelimiterValue = "")
        check(afterDot.isNotEmpty())
        return FileExtension(afterDot)
    }

val AnyFsFile.finalExtension: FileExtension
    get() {
        check(fName.count { it == '.' } >= 1) {
            "0 dots in name of $path"
        }
        val afterDot = fName.substringAfterLast(".", missingDelimiterValue = "")
        check(afterDot.isNotEmpty())
        return FileExtension(afterDot)
    }


enum class FrameFileType(val extension: FileExtension) {
    Png(FileExtension.PNG), Jpg(FileExtension.JPG)
}

class FileExtension(input: String) {

    companion object {

        private val ext
            get() = matt.lang.delegation.provider {
                SimpleGetter(FileExtension(it.lower()))
            }

        val C by ext
        val O by ext
        val A by ext
        val TEX by ext
        val AU3 by ext
        val JSON by ext
        val CBOR by ext
        val APP by ext
        val PDF by ext
        val BIB by ext
        val EMLX by ext
        val ZIP by ext
        val FFMPEG by ext /*I invented this*/
        val MOG by ext /*I invented this*/
        val COMMAND by ext /*I invented this*/
        val KT by ext
        val KTS by ext
        val CSS by ext
        val LESS by ext
        val JS by ext
        val GRADLE by ext
        val TAGS by ext /*probably was my own invention. not sure. used in fx file node.*/
        val COFFEESCRIPT by ext
        val MP3 by ext
        val MP4 by ext
        val TS by ext
        val PORT by ext
        val POM by ext
        val COMP by ext
        val COMP_MEM by ext
        val MODEL by ext
        val TEST by ext
        val PNG by ext
        val JPEG by ext
        val JPG by ext
        val SVG by ext
        val PY by ext
        val PYC by ext
        val JAVA by ext
        val GROOVY by ext
        val TXT by ext
        val DS_Store = FileExtension("DS_Store")
        val OUT by ext
        val ERR by ext
        val TAR by ext
        val OBJ by ext
        val HTML by ext
        val SH by ext
        val ZSHRC by ext
        val ZSH by ext
        val SCPT by ext
        val SCSS by ext

        val TIF by ext
        val TIFF by ext
        val ICN by ext
        val ICO by ext
        val ICNS by ext
        val NEF by ext
        val IMAGE_EXTENSIONS = ExtensionSet(
            PNG,
            JPEG,
            JPG,
            SVG,
            TIF,
            TIFF,
            ICN,
            ICO,
            ICNS,
            NEF
        )


        val IML by ext
        val IPR by ext
        val IWS by ext

        val APPLESCRIPT by ext
        val JAR by ext
        val KEXE by ext
        val EXE by ext
        val XML by ext
        val PROPERTIES by ext
        val YAML by ext
        val YML by ext
        val TOML by ext
        val LOG by ext
        val DMG by ext
        val MD by ext

        val CRDOWNLOAD by ext

        val IMG by ext
        val ISO by ext

        val PPTX by ext
        val PPT by ext


    }

    init {
        requireNotEndsWith(input, ".") {
            "file extension \"${input}\" should not end with a dot"
        }
    }

    /*todo: allow this to be case sensitive*/
    private val id = input.removePrefix(".").lower()

    override fun equals(other: Any?): Boolean {
        return other is FileExtension && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    val afterDot = id
    val withPrefixDot = ".$id"


    val isImage by lazy {
        this in IMAGE_EXTENSIONS
    }

}

@JvmInline
value class ExtensionSet internal constructor(val extensions: Set<FileExtension>) : Set<FileExtension> by extensions {
    constructor (vararg exts: FileExtension) : this(setOf(*exts))

    init {
        require(extensions.isNotEmpty())
    }
}