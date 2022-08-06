package matt.file.log

import matt.file.MFile

interface Logger {
  fun printLog(s: String)
  operator fun plusAssign(s: Any) = printLog(s.toString())
}

open class AppendLogger(
  private val logfile: Appendable? = null,
): Logger {

  constructor(logfile: MFile): this(
	logfile.also { it.parentFile!!.mkdirs() }.writer()
  )

  init {
	if (logfile is MFile) {
	  logfile.parentFile?.mkdirs()
	}
  }

  var startTime: Long? = null
  override fun printLog(s: String) {
	val now = System.currentTimeMillis()
	val dur = startTime?.let { now - it }
	val line = "[$now][$dur] $s"
	logfile?.appendLine(line)
	postLog()

  }

  open fun postLog() = Unit


}

val SystemOutLogger by lazy { AppendLogger(System.out) }
val NOPLogger by lazy { AppendLogger(null) }

class LogFileLogger(val file: MFile): AppendLogger(file.writer()) {
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

class MultiLogger(vararg val loggers: Logger): Logger {
  override fun printLog(s: String) {
	loggers.forEach { it.printLog(s) }
  }
}