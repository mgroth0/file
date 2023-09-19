@file:Suppress("unused", "UNUSED_PARAMETER")

package matt.file.zip

import matt.file.FsFileImpl
import matt.file.toJioFile
import matt.lang.anno.DoesNotAlwaysWork
import matt.lang.err
import matt.lang.file.toJFile
import matt.lang.model.file.FsFile
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Suppress("UNREACHABLE_CODE")
@DoesNotAlwaysWork
class ZipFiles {
    var filesListInDir: MutableList<String> = ArrayList()


    /**
     * This method zips the directory
     * @param dir
     * @param zipDirName
     */
    fun zipDirectory(
        dir: FsFileImpl,
        zipDirName: FsFileImpl
    ) {

        err(problem)

        try {
            populateFilesList(dir)
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            val fos = FileOutputStream(zipDirName.toJFile())
            val zos = ZipOutputStream(fos)
            for (filePath in filesListInDir) {
                println("Zipping $filePath")
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                check(dir.isAbsolute)
                val ze = ZipEntry(filePath.substring(dir.path.length + 1, filePath.length))
                zos.putNextEntry(ze)
                //read the file and write to ZipOutputStream
                val fis = FileInputStream(filePath)
                val buffer = ByteArray(1024)
                var len: Int
                while (fis.read(buffer).also { len = it } > 0) {
                    zos.write(buffer, 0, len)
                }
                zos.closeEntry()
                fis.close()
            }
            zos.close()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * This method populates all the files in a directory to a List
     * @param dir
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun populateFilesList(dir: FsFile) {
        val files: Array<out FsFile> = dir.toJioFile().listFiles()!!
        for (file in files) {
            if (file.toJFile().isFile) filesListInDir.add(file.toJFile().absolutePath) else populateFilesList(file)
        }
    }

    companion object {

        val problem =
            "not copying file attributes or something, because the input.app ran but the output one not and got weird errors. using shell zip for now"

        /**
         * This method compresses the single file to zip format
         * @param file
         * @param zipFileName
         */
        fun zipSingleFile(
            file: FsFileImpl,
            zipFileName: String
        ) {

            err(problem)

            try {
                //create ZipOutputStream to write to the zip file
                val fos = FileOutputStream(zipFileName)
                val zos = ZipOutputStream(fos)
                //add a new Zip Entry to the ZipOutputStream
                val ze: ZipEntry = ZipEntry(file.name)
                zos.putNextEntry(ze)
                //read the file and write to ZipOutputStream
                val fis = FileInputStream(file.toJFile())
                val buffer = ByteArray(1024)
                var len: Int
                while (fis.read(buffer).also { len = it } > 0) {
                    zos.write(buffer, 0, len)
                }

                //Close the zip entry to write to zip file
                zos.closeEntry()
                //Close resources
                zos.close()
                fis.close()
                fos.close()
                println(file.toJFile().canonicalPath + " is zipped to " + zipFileName)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}