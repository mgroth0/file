@file:JvmName("FileJvmKt")
package matt.file

import matt.file.CaseSensitivity.CaseInSensitive
import matt.file.CaseSensitivity.CaseSensitive
import matt.file.thismachine.thisMachine


actual val defaultCaseSensitivity by lazy {
    if (thisMachine.caseSensitive) CaseSensitive else CaseInSensitive
}