package matt.file.log

import matt.file.LogFile
import matt.klib.lang.NOT_IMPLEMENTED
import matt.klib.str.joinWithCommas
import java.io.Flushable

/*inline might matter here. might change the place in the stack where I should look*/
inline fun <R> decorateGlobal(log: Logger, vararg params: Any?, op: ()->R): R {
  val t = Thread.currentThread()
  val stack = t.stackTrace
  val maybeThisFarBack = stack[2]
  val m = maybeThisFarBack.methodName
  log += "starting $m(${params.joinWithCommas()})"
  val r = op()
  log += "finished running $m, result=$r"
  return r
}


open class HasLogger(val log: Logger) {
  inline fun <R> decorate(vararg params: Any?, op: ()->R): R = decorateGlobal(
	log,
	*params,
	op = op
  )
}


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

class MultiLogger(private vararg val loggers: Logger): Logger {
  override var startTime: Long?
	get() = NOT_IMPLEMENTED
	set(value) {
	  loggers.forEach { it.startTime = value }
	}

  override fun printLog(s: String) {
	loggers.forEach { it.printLog(s) }
  }
}