package matt.file.test.j

import matt.file.construct.toMFile
import matt.file.thismachine.thisMachine
import matt.lang.assertions.require.requireEquals
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.test.Test


class CommonJvmAndroidFileTests {
    /*Verifies part of the contract of [[java.nio.file.Path#relativize]]*/
    @Test
    fun pathEqualsResolvedRelativized(
        @TempDir tmpDir: Path
    ) {
        val parent = tmpDir.toMFile(thisMachine.fileSystemFor(tmpDir.pathString))["a"]
        val child = parent["b"]
        val relative = parent.relativize(child)
        requireEquals(child, parent.resolve(relative))
    }
}
