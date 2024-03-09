package matt.file.context.desktop

import matt.file.context.LocalComputeContext
import matt.file.context.OpenMindComputeContext
import matt.file.thismachine.thisMachine
import matt.model.code.sys.OpenMind
import matt.model.code.sys.SiliconMac
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


val RUNTIME_COMPUTE_CONTEXT by lazy {
    when {
        (thisMachine is SiliconMac) -> LocalComputeContext
        thisMachine is OpenMind     -> OpenMindComputeContext
        else                        -> TODO("detect runtime compute context robustly")
    }
}


/*

Making these all private for now. Maybe I will use them in the future. But I created them under the assumption that I needed them now, which I don't. And I need to quickly remove all references to them right now.

*/

private interface WorkExecutionContext {
    val workExecutor: ExecutorService
}

private object InPlaceWorkContext: WorkExecutionContext {
    override val workExecutor = InPlaceExecutorService
}

private object InPlaceExecutorService: ExecutorService {



    override fun execute(command: Runnable) {
        command.run()
    }

    override fun shutdown(): Unit = throw UnsupportedOperationException()

    override fun shutdownNow(): MutableList<Runnable> = throw UnsupportedOperationException()

    override fun isShutdown(): Boolean = throw UnsupportedOperationException()

    override fun isTerminated(): Boolean = throw UnsupportedOperationException()

    override fun awaitTermination(
        timeout: Long,
        unit: TimeUnit
    ): Boolean = throw UnsupportedOperationException()

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        val result = task.call()
        return CompletableFuture.completedFuture(result)
    }

    override fun <T : Any?> submit(
        task: Runnable,
        result: T
    ): Future<T> {
        error("Why would I ever use this?")
    }

    override fun submit(task: Runnable): Future<*> {
        task.run()
        return CompletableFuture.completedFuture(Unit)
    }

    override fun <T : Any?> invokeAll(tasks: Collection<Callable<T>>): List<Future<T>> =
        tasks.map {
            CompletableFuture.completedFuture(it.call())
        }

    override fun <T : Any?> invokeAll(
        tasks: Collection<Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): List<Future<T>> {
        TODO()
    }

    override fun <T : Any> invokeAny(tasks: Collection<Callable<T>>): T {
        TODO()
    }

    override fun <T : Any?> invokeAny(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): T {
        TODO()
    }
}
