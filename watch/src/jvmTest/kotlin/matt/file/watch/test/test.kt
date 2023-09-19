package matt.file.watch.test


import matt.file.watch.createFileSizeProp
import matt.test.Tests
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class WatchTests : Tests() {
    @Test
    fun createAWatchProp() {
        matt.file.JioFile.createTempFile("abc", "efg").createFileSizeProp(1.seconds)
    }
}