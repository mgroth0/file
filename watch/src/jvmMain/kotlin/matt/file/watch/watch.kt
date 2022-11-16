package matt.file.watch

import matt.file.MFile
import matt.lang.function.Op
import matt.obs.watch.watchProp
import kotlin.time.Duration

fun MFile.createRecursiveLastModifiedProp(checkInterval: Duration) = watchProp(checkInterval) {
  recursiveLastModified()
}

fun MFile.createFileSizeProp(checkInterval: Duration) = watchProp(checkInterval) {
  takeIf { exists() }?.size()
}

fun MFile.createRecursiveFileSizeProp(checkInterval: Duration) = watchProp(checkInterval) {
  takeIf { exists() }?.recursiveSize()
}

fun MFile.createFileExistsProp(checkInterval: Duration) = watchProp(checkInterval) {
  exists()
}

fun MFile.onChange(checkInterval: Duration, op: Op) {
  val prop = createRecursiveLastModifiedProp(checkInterval)
  prop.onChange {
	op()
  }
}