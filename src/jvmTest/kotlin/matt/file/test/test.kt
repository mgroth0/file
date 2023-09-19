@file:JavaIoFileIsOk

package matt.file.test


import matt.file.FSRoot
import matt.file.Src
import matt.file.UnknownFileOrURL
import matt.file.construct.toMFile
import matt.lang.anno.ok.JavaIoFileIsOk
import matt.lang.model.file.MacFileSystem
import matt.test.JupiterTestAssertions.assertRunsInOneMinute
import java.io.File
import kotlin.test.Test

class FileTests {
    @Test
    fun constructClasses() = assertRunsInOneMinute {
        Src("fake")
        UnknownFileOrURL("abc")
        File("abc").toMFile(MacFileSystem)
        FSRoot(MacFileSystem)
    }

    @Test
    fun initObjects() = assertRunsInOneMinute {

    }

}