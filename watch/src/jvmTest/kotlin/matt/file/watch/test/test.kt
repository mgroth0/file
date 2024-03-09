package matt.file.watch.test


import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import matt.file.watch.createFileSizeProp
import matt.test.Tests
import matt.test.co.runTestWithTimeoutOnlyIfTestingPerformance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

class WatchTests : Tests() {

    @Test
    fun createAWatchProp() {
        val tempFile = matt.file.JioFile.createTempFile("abc", "efg")
        val goodCancel = "goodCancel"
        try {
            runTestWithTimeoutOnlyIfTestingPerformance {
                tempFile.createFileSizeProp(1.seconds)
                cancel(goodCancel)
            }
        } catch (e: CancellationException) {
            assertEquals(goodCancel, e.message)
            assertNull(e.cause)
        }
    }
}
