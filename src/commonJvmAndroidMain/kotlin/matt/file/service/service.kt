package matt.file.service

import matt.model.obj.text.ReadableFile


object DefaultFileReader : FileReader {

    override fun read(file: ReadableFile<*>): String {
        return file.text
    }

}