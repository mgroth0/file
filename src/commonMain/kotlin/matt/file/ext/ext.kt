package matt.file.ext

import matt.lang.not

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
    }

    init {
        require(not(input.endsWith("."))) {
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

}