@file:JavaIoFileIsOk

package matt.file.test


import matt.file.FSRoot
import matt.file.Src
import matt.file.UnknownFileOrURL
import matt.file.construct.toMFile
import matt.model.code.ok.JavaIoFileIsOk
import matt.test.JupiterTestAssertions.assertRunsInOneMinute
import java.io.File
import kotlin.test.Test

class FileTests {
    @Test
    fun constructClasses() = assertRunsInOneMinute {
        Src("fake")
        UnknownFileOrURL("abc")
        File("abc").toMFile()
    }

    @Test
    fun initObjects() = assertRunsInOneMinute {
        FSRoot
    }

}