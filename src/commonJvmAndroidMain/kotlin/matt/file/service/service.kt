package matt.file.service

import matt.file.service.common.FileReader
import matt.model.obj.text.ReadableFile


object DefaultFileReader : FileReader {

    override fun read(file: ReadableFile<*>): String = file.text
}
