package matt.file.watch.tail

import matt.file.MFile
import kotlin.concurrent.thread

fun tail(file: MFile) = thread(isDaemon = true) {
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