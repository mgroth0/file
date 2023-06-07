package matt.file.construct

import matt.file.CaseSensitivity
import matt.file.MFile
import kotlin.reflect.KClass

expect fun mFile(
    userPath: String,
    caseSensitivity: CaseSensitivity? = null,
    cls: KClass<out MFile>? = null
): MFile