package matt.file.service

import matt.model.code.idea.ServiceIdea
import matt.model.obj.text.ReadableFile

fun interface FileReader : ServiceIdea {
    fun read(file: ReadableFile<*>): String
}

object NoFileReader : FileReader {
    override fun read(file: ReadableFile<*>) = error("no file reading")
}
