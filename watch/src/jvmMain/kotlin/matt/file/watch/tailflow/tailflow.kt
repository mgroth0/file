package matt.file.watch.tailflow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import matt.file.toJioFile
import matt.lang.common.forever
import matt.lang.model.file.FsFile
import matt.stream.buffer.NativeBuffer
import matt.stream.buffer.flippedReadView
import matt.stream.buffer.grow
import matt.stream.buffer.readInto
import matt.stream.buffer.readsSameContentAs
import matt.stream.buffer.reset
import matt.stream.common.decode.decodeAssumingSuccessful
import matt.stream.decode.MyCharsetDecoder
import java.nio.channels.FileChannel
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


sealed interface FileTailEvent
data object Reset: FileTailEvent
class NewContent(val content: String): FileTailEvent


fun FsFile.tailFlow() =
    channelFlow {
        val fullRefreshInterval = 1.seconds
        val tailRefreshInterval = 100.milliseconds
        val started = AtomicBoolean(false)
        withContext(Dispatchers.IO) {
            val decoder = MyCharsetDecoder()
            var buffer = NativeBuffer()
            val mutex = Mutex()
            check(!started.getAndSet(true))
            val tailReader: FileChannel = toJioFile().readChannel()
            val scanReader: FileChannel = toJioFile().readChannel()
            coroutineContext.job.invokeOnCompletion {
                tailReader.close()
                scanReader.close()
            }
            launch {
                forever {
                    mutex.withLock {
                        val same =
                            scanReader
                                .reset()
                                .readsSameContentAs(buffer.flippedReadView())
                        if (!same) {
                            decoder.reset()
                            send(Reset)
                            buffer.clear()
                            tailReader.position(0)
                        }
                    }
                    delay(fullRefreshInterval)
                }
            }
            launch {
                var gotSome = true
                forever {
                    if (!gotSome) {
                        delay(tailRefreshInterval)
                    }
                    mutex.withLock {
                        val readView = tailReader.readInto(buffer)
                        if (!buffer.hasRemaining()) {
                            buffer = buffer.grow()
                        } else if (readView == null) {
                            gotSome  = false
                        } else if (!readView.hasRemaining()) {
                            gotSome  = false
                        }
                        if (readView != null) {
                            decoder.feedFrom(readView)
                            decoder.decodeAssumingSuccessful(expectMoreInput = true) {
                                send(NewContent(it.concatToString()))
                            }
                        }
                    }
                }
            }
        }
    }.buffer(Channel.UNLIMITED)



