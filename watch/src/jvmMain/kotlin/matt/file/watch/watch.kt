package matt.file.watch

import kotlinx.coroutines.CoroutineScope
import matt.file.JioFile
import matt.file.ext.recursiveLastModified
import matt.file.ext.recursiveSize
import matt.lang.function.Op
import matt.obs.watch.launchWatchProperty
import kotlin.time.Duration

context(CoroutineScope)
fun JioFile.onChange(
    checkInterval: Duration,
    op: Op
) {
    val prop = createRecursiveLastModifiedProp(checkInterval)
    prop.onChange {
        op()
    }
}

context(CoroutineScope)
fun JioFile.createRecursiveLastModifiedProp(checkInterval: Duration) = launchWatchProperty(checkInterval) {
    recursiveLastModified()
}

context(CoroutineScope)
fun JioFile.createFileSizeProp(checkInterval: Duration) = launchWatchProperty(checkInterval) {
    takeIf { exists() }?.size()
}

context(CoroutineScope)
fun JioFile.createRecursiveFileSizeProp(checkInterval: Duration) = launchWatchProperty(checkInterval) {
    takeIf { exists() }?.recursiveSize()
}

context(CoroutineScope)
fun JioFile.createFileExistsProp(checkInterval: Duration) = launchWatchProperty(checkInterval) {
    exists()
}


