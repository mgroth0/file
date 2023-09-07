@file:JvmName("FileAndroidKt")
package matt.file

import matt.file.CaseSensitivity.CaseSensitive
import matt.lang.anno.SeeURL


/*Careful! Android's OS is case-sensitive yes, but commonly attached file devices like sd cards are often case-insensitive! */
@SeeURL("https://stackoverflow.com/a/6502881/6596010")
actual val defaultCaseSensitivity by lazy {
    CaseSensitive
}