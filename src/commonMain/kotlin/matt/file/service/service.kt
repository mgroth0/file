package matt.file.service

import matt.file.MFile
import matt.model.code.idea.ServiceIdea

fun interface FileReader: ServiceIdea {
  fun read(file: MFile): String
}

object NoFileReader: FileReader {
  override fun read(file: MFile) = error("no file reading")
}