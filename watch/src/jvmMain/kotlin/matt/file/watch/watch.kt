package matt.file.watch

import matt.file.MFile
import matt.obs.prop.BindableProperty
import kotlin.concurrent.thread

fun MFile.createRecursiveLastModifiedProp(checkFreqMillis: Long): BindableProperty<Long> {
  val prop = BindableProperty(recursiveLastModified())
  thread(isDaemon = true) {
	while (true) {
	  val m = recursiveLastModified()
	  if (m != prop.value) prop.value = m
	  Thread.sleep(checkFreqMillis)
	}
  }
  return prop
}
