package matt.file.watch.test


import matt.file.MFile
import matt.file.watch.createFileSizeProp
import matt.test.Tests
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class WatchTests : Tests() {
    @Test
    fun createAWatchProp() {
        MFile.createTempFile("abc", "efg").createFileSizeProp(1.seconds)
    }
}