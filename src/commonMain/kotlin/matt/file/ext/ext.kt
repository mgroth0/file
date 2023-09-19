package matt.file.ext

import matt.lang.model.file.FsFile
import matt.lang.model.file.fName
import matt.lang.require.requireNotEndsWith
import matt.model.code.delegate.SimpleGetter
import matt.prim.str.lower
import kotlin.jvm.JvmInline

val FsFile.mExtension
    get() = fName.substringAfter(".", missingDelimiterValue = "").takeIf { it.isNotEmpty() }
        ?.let(::FileExtension)

class FileExtension(input: String) {

    companion object {

        private val ext get() = matt.lang.delegation.provider {
            SimpleGetter(FileExtension(it.lower()))
        }

        val STATUS by ext /*my invention*/

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
        val PORT by ext
        val COMP by ext
        val COMP_MEM by ext
        val MODEL by ext
        val TEST by ext
        val PNG by ext
        val JPEG by ext
        val JPG by ext
        val SVG by ext
        val PY by ext
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

        val TIF by ext
        val TIFF by ext
        val ICN by ext
        val ICO by ext
        val IMAGE_EXTENSIONS = ExtensionSet(
            PNG,
            JPEG,
            JPG,
            SVG,
            TIF,
            TIFF,
            ICN,
            ICO
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



    }

    init {
        requireNotEndsWith(input, ".") {
            "file extension \"${input}\" should not end with a dot"
        }
    }

    val id = input.removePrefix(".").lower()

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