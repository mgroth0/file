package matt.file.construct

import matt.file.MFile
import kotlin.reflect.KClass

expect fun mFile(userPath: String, cls: KClass<out MFile>? = null): MFile