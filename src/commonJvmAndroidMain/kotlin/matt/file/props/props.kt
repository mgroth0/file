
package matt.file.props

import matt.file.props.propthing.saveConvertForProps
import matt.file.toJioFile
import matt.file.toMacJioFile
import matt.lang.model.file.AnyFsFile
import java.util.Properties
import kotlin.io.path.outputStream


fun Properties(file: AnyFsFile): Properties = matt.collect.props.Properties(file.toMacJioFile().inputStream())

fun AnyFsFile.loadProperties() = Properties(this)


fun Properties.writeToSortedWithoutTimestampComments(file: AnyFsFile) {
    val rawProps = this
    /*
     * See: `${}` Properties::store
     * https://stackoverflow.com/a/6184414/6596010
     * Don't want timestamp comment. Also I want the entries sorted to avoid pointless commits
     * */
    file.toJioFile().outputStream().use { os ->

        val bw = os.bufferedWriter()
        val escUnicode = false
        val entriesSorted = rawProps.entries.sortedBy { it.key as String }

        for ((key1, value) in entriesSorted) {
            var key = key1 as String
            var stringValue = value as String
            key =
                saveConvertForProps(
                    key,
                    true,
                    escUnicode
                )/* No need to escape embedded and trailing spaces for value, hence
                 * pass false to flag.
                 */
            stringValue =
                saveConvertForProps(
                    stringValue,
                    false,
                    escUnicode
                )
            bw.write("$key=$stringValue")
            bw.newLine()
        }

        os.flush()
        bw.flush()
        bw.close()
    }
}
