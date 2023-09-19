package matt.file.log

import matt.file.JioFile
import matt.file.toIoFile
import matt.file.toJioFile
import matt.lang.function.Produce
import matt.lang.go
import matt.lang.model.file.FsFile
import matt.log.AppendLogger


class LogFileLogger(val file: FsFile) : AppendLogger(file.toJioFile().bufferedWriter().apply { }) {
    init {
        file.parent?.toIoFile()?.mkdirs()
    }

    override fun postLog() {
        if (file.toJioFile().readLines().size > 1000) {
            file.toJioFile().writeText(
                "overwriting log file since it has > 1000 lines. Did this because I'm experiencing hanging and thought it might be this huge file. Todo: backup before delete"
            )
        }
    }
}


inline fun <R> runAndAndPrintLogFilesOnException(
    vararg files: Pair<String, JioFile>,
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
