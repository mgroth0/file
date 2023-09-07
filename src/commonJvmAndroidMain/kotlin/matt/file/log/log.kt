package matt.file.log

import matt.file.LogFile
import matt.file.MFile
import matt.lang.function.Produce
import matt.lang.go
import matt.log.AppendLogger


class LogFileLogger(val file: LogFile) : AppendLogger(file.bufferedWriter().apply { }) {
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


inline fun <R> runAndAndPrintLogFilesOnException(
    vararg files: Pair<String, MFile>,
    op: Produce<R>
): R {
    try {
        return op()
    } catch (e: Exception) {
        println("EXCEPTION: $e")
        e.printStackTrace()
        files.forEach {
            println(it.first + ":")
            println()
            println()
            it.second.takeIf { it.exists() }?.text?.go {
                println(it)
            }
            println()
            println()
        }
        throw e
    }
}
