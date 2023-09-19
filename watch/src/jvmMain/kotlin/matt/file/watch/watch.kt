package matt.file.watch

import matt.file.JioFile
import matt.file.ext.recursiveLastModified
import matt.file.ext.recursiveSize
import matt.lang.function.Op
import matt.obs.watch.watchProp
import kotlin.time.Duration

fun JioFile.createRecursiveLastModifiedProp(checkInterval: Duration) = watchProp(checkInterval) {
    recursiveLastModified()
}

fun JioFile.createFileSizeProp(checkInterval: Duration) = watchProp(checkInterval) {
    takeIf { exists() }?.size()
}

fun JioFile.createRecursiveFileSizeProp(checkInterval: Duration) = watchProp(checkInterval) {
    takeIf { exists() }?.recursiveSize()
}

fun JioFile.createFileExistsProp(checkInterval: Duration) = watchProp(checkInterval) {
    exists()
}

fun JioFile.onChange(
    checkInterval: Duration,
    op: Op
) {
    val prop = createRecursiveLastModifiedProp(checkInterval)
    prop.onChange {
        op()
    }
}