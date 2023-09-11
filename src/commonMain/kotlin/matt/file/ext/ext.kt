package matt.file.ext

import matt.lang.require.requireNotEndsWith
import kotlin.jvm.JvmInline

class FileExtension(input: String) {

    companion object {
        val JSON = FileExtension("json")
        val CBOR = FileExtension("cbor")
        val APP = FileExtension("app")
        val PDF = FileExtension("pdf")
        val BIB = FileExtension("bib")
        val EMLX = FileExtension("emlx")
        val ZIP = FileExtension("zip")
        val FFMPEG = FileExtension("ffmpeg") /*I invented this*/
        val MOG = FileExtension("mog") /*I invented this*/
        val COMMAND = FileExtension("command") /*I invented this*/
        val KT = FileExtension("kt")
        val MP3 = FileExtension("mp3")
        val MP4 = FileExtension("mp4")
        val PORT = FileExtension("port")
        val COMP = FileExtension("comp")
        val COMP_MEM = FileExtension("comp_mem")
        val MODEL = FileExtension("model")
        val TEST = FileExtension("test")
        val PNG = FileExtension("png")
        val JPEG = FileExtension("jpeg")
        val JPG = FileExtension("jpg")
        val SVG = FileExtension("svg")
        val PY = FileExtension("py")
        val JAVA = FileExtension("java")
        val GROOVY = FileExtension("groovy")
        val TXT = FileExtension("txt")
        val DS_Store = FileExtension("DS_Store")
        val OUT = FileExtension("out")
        val ERR = FileExtension("err")
        val TAR = FileExtension("tar")
        val OBJ = FileExtension("obj")
        val HTML = FileExtension("html")

        val IMAGE_EXTENSIONS = ExtensionSet(
            PNG,
            JPEG,
            JPG,
            SVG
        )


        val IML = FileExtension("iml")
        val IPR = FileExtension("ipr")
        val IWS = FileExtension("iws")

    }

    init {
        requireNotEndsWith(input, ".") {
            "file extension \"${input}\" should not end with a dot"
        }
    }

    val id = input.removePrefix(".")

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