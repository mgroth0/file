@file:JavaIoFileIsOk

package matt.file.test

import matt.file.Src
import matt.file.UnknownFileOrURL
import matt.file.construct.toMFile
import matt.lang.anno.ok.JavaIoFileIsOk
import matt.lang.model.file.MacFileSystem
import matt.test.Tests
import java.io.File
import kotlin.test.Test

class FileTests : Tests() {
    @Test
    fun constructClasses() =
        assertRunsInOneMinute {
            Src("fake")
            UnknownFileOrURL("abc")
            @Suppress("ForbiddenMethodCall")
            File("abc").toMFile(MacFileSystem)
        }

    @Test
    fun initObjects() =
        assertRunsInOneMinute {
        }
}
