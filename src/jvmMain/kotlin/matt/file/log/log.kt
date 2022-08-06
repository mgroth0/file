package matt.file.log

import matt.file.LogFile
import matt.klib.lang.NOT_IMPLEMENTED
import java.io.Flushable

interface Logger {
  fun printLog(s: String)
  operator fun plusAssign(s: Any) = printLog(s.toString())
  var startTime: Long?
}

open class AppendLogger internal constructor(
  private val logfile: Appendable? = null,
): Logger {

  override var startTime: Long? = null
  override fun printLog(s: String) {
	val now = System.currentTimeMillis()
	val dur = startTime?.let { now - it }
	val line = "[$now][$dur] $s"
	logfile?.appendLine(line)
	(logfile as? Flushable)?.flush()
	postLog()
  }

  open fun postLog() = Unit


}

val SystemOutLogger by lazy { AppendLogger(System.out) }
val NOPLogger by lazy { AppendLogger(null) }

class LogFileLogger(val file: LogFile): AppendLogger(file.bufferedWriter().apply {  }) {
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
  override var startTime: Long?
	get() = NOT_IMPLEMENTED
	set(value) {
	  loggers.forEach { it.startTime = value }
	}

  override fun printLog(s: String) {
	loggers.forEach { it.printLog(s) }
  }
}