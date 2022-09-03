package matt.file.log

import matt.file.LogFile
import matt.log.AppendLogger


class LogFileLogger(val file: LogFile): AppendLogger(file.bufferedWriter().apply { }) {
  init {
	file.parentFile?.mkdirs()
  }

  override fun postLog() {
	if (file.readLines().size > 1000) {
	  file.writeText(
		"overwriting log file since it has > 1000 lines. Did this because I'm experiencing hanging and thought it might be this huge file. Todo: backup before delete"
	  )
	}
  }
}

