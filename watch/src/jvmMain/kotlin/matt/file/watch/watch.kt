package matt.file.watch

import io.methvin.watcher.DirectoryChangeEvent.EventType.CREATE
import io.methvin.watcher.DirectoryChangeEvent.EventType.DELETE
import io.methvin.watcher.DirectoryChangeEvent.EventType.MODIFY
import io.methvin.watcher.DirectoryChangeEvent.EventType.OVERFLOW
import io.methvin.watcher.DirectoryWatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.yield
import matt.file.JioFile
import matt.file.ext.recursiveLastModified
import matt.file.ext.recursiveSize
import matt.lang.anno.optin.IncubatingMattCode
import matt.lang.file.toJFile
import matt.lang.function.Op
import matt.lang.model.file.FsFile
import matt.obs.prop.BindableProperty
import matt.obs.prop.ObsVal
import matt.obs.watch.launchWatchProperty
import java.nio.file.StandardWatchEventKinds
import java.util.concurrent.TimeUnit.MILLISECONDS
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
fun JioFile.createFileExistsProp() = launchFileWatchingProperty(parent!!) {
    exists()
}


@IncubatingMattCode
private fun <T> CoroutineScope.launchRecursiveDirWatchingProperty(
    dir: FsFile,
    op: () -> T
): ObsVal<T> {
    val jFile = dir.toJFile()
    check(jFile.exists())
    check(jFile.isDirectory)
    val mutex = Mutex()
    val prop = BindableProperty(op())
    val watcher = DirectoryWatcher.builder().path(jFile.toPath()).listener { event ->
        val type = event.eventType()
        when (type) {
            CREATE   -> Unit
            MODIFY   -> Unit
            DELETE   -> error("I guess the directory might have been deleted causing this watch prop to be auto-closed? IDK.")
            OVERFLOW -> error("not sure how to handle overflow right now. Especially considering that it might contain an event indicating that the registered folder was deleted")
        }
        prop.value = op()
    }.build()
    watcher.watchAsync { runnable ->
        launch(Dispatchers.IO) {
            mutex.withLock {
                runnable.run()
            }
        }
    }
    coroutineContext.job.invokeOnCompletion {
        watcher.close()
    }
    return prop
}


private fun <T> CoroutineScope.launchFileWatchingProperty(
    file: FsFile,
    op: () -> T
): ObsVal<T> {
    val prop = BindableProperty(op())
    val jFile = file.toJFile()
    check(jFile.exists())
    check(jFile.isDirectory)
    val path = jFile.toPath()
    val watchService =
        path.fileSystem.newWatchService() /*eventually, might want to not create a new watch service each time... For now, it is ok though.*/
    val watchKey = path.register(
        watchService,/*eventually might want to watch for specific kinds of events*/
        StandardWatchEventKinds.ENTRY_CREATE,
        StandardWatchEventKinds.ENTRY_DELETE,
        StandardWatchEventKinds.ENTRY_MODIFY,
        StandardWatchEventKinds.OVERFLOW
    )
    launch(Dispatchers.IO) {
        while (true) {
            check(watchKey.isValid) /*idk*/
            yield() /*check for cancellation*/
            val key = watchService.poll(100, MILLISECONDS)
                ?: continue /*adjust poll time as needed. Higher = use less CPU, lower = more responsive to cancellation and faster shutdown. Can make it dynamic or make it an input value as well, though that increases the complexity of this.*/
            check(key == watchKey)
            check(key.isValid) /*idk*/
            val events = key.pollEvents()
            events.forEach {
                when (it.kind()) {
                    StandardWatchEventKinds.OVERFLOW     -> {
                        error("not sure how to handle overflow right now. Especially considering that it might contain an event indicating that the registered folder was deleted")
                    }

                    StandardWatchEventKinds.ENTRY_DELETE -> {
                        val ctx = it.context()!!
                        if (ctx.toString().length <= 1) {
                            error("I guess the directory might have been deleted causing this watch prop to be auto-closed? IDK.")
                        }
                    }
                }
            }
            prop.value = op()
            key.reset()
        }
    }
    coroutineContext.job.invokeOnCompletion {
        watchKey.cancel()
        watchService.close()
    }
    return prop
}




