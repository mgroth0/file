package matt.file.ext.weird

import matt.file.JvmMFile
import matt.file.ext.IndexFolder
import matt.file.toJioFile
import matt.lang.assertions.require.requirePositive

fun JvmMFile.getNextSubIndexedFile(
    filename: String,
    maxN: Int,
): JvmMFile {

    requirePositive(maxN)

    val existingSubIndexFolds = listFiles()!!.mapNotNull { f ->
        f.name.toIntOrNull()?.let { IndexFolder(f) }
    }.sortedBy { it.index }


    val firstSubIndexFold = existingSubIndexFolds.firstOrNull()

    val nextSubIndexFold = if (existingSubIndexFolds.isEmpty()) IndexFolder(
        resolve("1")
    ) else if (existingSubIndexFolds.size >= maxN) {
        firstSubIndexFold!!
    } else /*existingSubIndexFolds.firstOrNull { (it + filename).toJioFile().doesNotExist }
        ?:*/ existingSubIndexFolds.last().next()



    /*jeez... what the hell was I doing...*/

    /*
        if (nextSubIndexFold.index > maxN) {


            firstSubIndexFold?.plus(filename)?.toJFile()?.deleteRecursively()
            (existingSubIndexFolds - firstSubIndexFold).filterNotNull().sortedBy { it.index }.forEach {
                (it + filename).takeIf { it.toJioFile().exists() }?.toJioFile()?.moveInto(it.previous().f.toJioFile())
            }




        }


        if (existingSubIndexFolds.size > maxN) {


            firstSubIndexFold?.plus(filename)?.toJFile()?.deleteRecursively()



            (existingSubIndexFolds - firstSubIndexFold).filterNotNull().sortedBy { it.index }.forEach {
                (it + filename).takeIf { it.toJioFile().exists() }?.toJioFile()?.moveInto(it.previous().f.toJioFile())
            }




        }
     */



    return (nextSubIndexFold + filename).toJioFile()

}
