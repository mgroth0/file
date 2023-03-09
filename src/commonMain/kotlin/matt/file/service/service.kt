package matt.file.service

import matt.file.MFile
import matt.model.code.idea.ServiceIdea

interface FileReader: ServiceIdea {
  fun read(file: MFile): String
}