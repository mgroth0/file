package matt.file.service

import matt.file.MFile

object DefaultFileReader: FileReader {

  override fun read(file: MFile): String {
	return file.readText()
  }

}