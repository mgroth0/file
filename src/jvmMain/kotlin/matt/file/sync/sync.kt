package matt.file.sync

import matt.file.MFile

class SynchronizedFileManager(
  private val file: MFile
) {

  @Synchronized
  fun backup() = file.backup(thread=false)

  @Synchronized
  fun read() = file.text

  @Synchronized
  fun write(text: String) = file.write(text)

}