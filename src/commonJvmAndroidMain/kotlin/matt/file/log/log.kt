package matt.file.log

import matt.file.JioFile
import matt.file.toIoFile
import matt.file.toJioFile
import matt.lang.function.Produce
import matt.lang.go
import matt.lang.model.file.AnyFsFile
import matt.log.AppendLogger


class LogFileLogger(
    val file: AnyFsFile,
    val backupFolder: AnyFsFile
) : AppendLogger(file.toJioFile().bufferedWriter().apply { }) {
    init {
        file.parent?.toIoFile()?.mkdirs()
    }

    override fun postLog() {
        val existingText = file.toJioFile().text
        if (existingText.lines().size > 1000) {
            val previousFile = backupFolder[System.currentTimeMillis().toString() + ".log"]
            previousFile.toJioFile().text = existingText
            file.toJioFile().writeText(
                "overwriting log file since it has > 1000 lines. Did this because I'm experiencing hanging and thought it might be this huge file. Put existing text in $previousFile"
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
