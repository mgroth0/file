
package matt.file.test

import matt.file.common.Src
import matt.file.common.UnknownFileOrURL
import matt.file.construct.toMFile
import matt.file.copy.copyPathWithAttributes
import matt.file.macJioFile
import matt.lang.assertions.require.requireEquals
import matt.lang.file.toJFile
import matt.lang.model.file.MacFileSystem
import matt.lang.model.file.exts.inDir
import matt.test.Tests
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileTests : Tests() {
    @Test
    fun constructClasses() =
        assertRunsInOneMinute {
            Src("fake")
            UnknownFileOrURL("abc")
            @Suppress("ForbiddenMethodCall")
            Path.of("abc").toFile().toMFile(MacFileSystem)
        }

    @Test
    fun initObjects() =
        assertRunsInOneMinute {
        }


    @Test
    fun fileContains() {
        assertTrue(
            macJioFile("/Users/matthewgroth/registered/ide/all/k/nn/deephys")
                inDir
                macJioFile("/Users/matthewgroth/registered/ide/all/k")
        )
        assertFalse(
            macJioFile("/Users/matthewgroth/registered/ide/all/other/k/nn/deephysN")
                inDir
                macJioFile("/Users/matthewgroth/registered/ide/all/k")
        )
    }



    @Test
    fun canCopyPathWithAttributes(
        @TempDir source: Path,
        @TempDir destRoot: Path
    ) {
        val realSource =
            source.toMFile(MacFileSystem).apply {
                this["text.txt"].text = "abc"
            }
        val realDest =  destRoot.toMFile(MacFileSystem)["dest"]
        copyPathWithAttributes(
            source = realSource,
            target = realDest,
            definitelyMkDirs = false
        )
    }


    @OptIn(ExperimentalPathApi::class)
    @Test
    fun appDirsCopyWithCorrectCasing(
        @TempDir source: Path,
        @TempDir destRoot: Path
    ) {
        val appDir = source.toMFile(MacFileSystem)["app.app"]
        appDir.mkdir()
        appDir.toJFile().resolve("Contents").mkdir()
        appDir.toJFile().resolve("Contents").resolve("_CodeSignature").mkdir()

        val destAppDir = destRoot.toMFile(MacFileSystem)["app.app"]

        appDir.copyTo(destAppDir)

        requireEquals("Contents", destAppDir.toJFile().list()!!.single())
        requireEquals("_CodeSignature", destAppDir["Contents"].toJFile().list()!!.single())

        destAppDir.deleteRecursively()

        copyPathWithAttributes(
            source = appDir,
            target = destAppDir,
            definitelyMkDirs = false
        )

        requireEquals("Contents", destAppDir.toJFile().list()!!.single())
        requireEquals("_CodeSignature", destAppDir["Contents"].toJFile().list()!!.single())
    }
}
