package matt.file.watch.tail

import matt.async.thread.namedThread
import matt.file.JioFile

fun tail(file: JioFile) = namedThread(isDaemon = true, name = "tail Thread") {
    var got = ""
    while (true) {
        if (file.exists()) {
            val r = file.text
            if (got != r) {
                println(r.removePrefix(got))
                got = r
            }
        }
        Thread.sleep(1000)
    }
}
