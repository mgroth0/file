package matt.file.log

import matt.file.MFile

open class Logger(private val logfile: MFile? = null) {
  init {
	logfile?.parentFile?.mkdirs()
  }

  var startTime: Long? = null
  fun printlog(s: String) {
	val now = System.currentTimeMillis()
	val dur = startTime?.let { now - it }
	val line = "[$now][$dur] $s"
	logfile?.appendText("\n" + line) ?: println(line)
	if (logfile != null && logfile.readLines().size > 1000) {
	  logfile.writeText(
		"overwriting log file since it has > 1000 lines. Did this because I'm experiencing hanging and thought it might be this huge file. Todo: backup before delete"
	  )
	}
  }

  operator fun plusAssign(s: String) = printlog(s)
}

object DefaultLogger: Logger(logfile = null)