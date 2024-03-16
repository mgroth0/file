package matt.file.commons.desktop

import matt.file.commons.home.USER_HOME
import matt.file.commons.reg.IDE_FOLDER
import matt.file.commons.reg.REGISTERED_FOLDER
import matt.file.commons.reg.TEST_DATA_FOLDER
import matt.file.construct.mFile
import matt.file.ext.FileExtension
import matt.file.model.file.types.asFolder
import matt.file.numbered.NumberedFiles
import matt.file.thismachine.thisMachine
import matt.file.toJioFile
import matt.lang.anno.SeeURL
import matt.lang.assertions.require.requireIs
import matt.lang.j.userName
import matt.lang.sysprop.props.UserHome
import matt.model.code.sys.Linux
import matt.model.code.sys.Mac
import matt.model.code.sys.NewMac
import matt.model.code.sys.Windows
import matt.model.code.sys.WindowsFileSystem


val THREE_D_PRINT_FOLDER = REGISTERED_FOLDER["3dprint"]
val DNN_FOLDER by lazy {
    when (thisMachine) {
        NewMac -> IDE_FOLDER + "dnn"
        else   -> null
    }
}
val HEP_FOLDER by lazy {
    when (thisMachine) {
        NewMac -> IDE_FOLDER + "hep"
        else   -> null
    }
}
val desktopFolder by lazy { with(thisMachine.fileSystemFor(UserHome.get())) { mFile(UserHome.get())["Desktop"] } }
val FILE_ACCESS_CHECK_FILE by lazy { USER_HOME + "Desktop" + ".FileAccessCheck.txt" }
fun hasFullFileAccess() = FILE_ACCESS_CHECK_FILE.toJioFile().exists()
val DEEPHYS_TEST_DATA_FOLDER = TEST_DATA_FOLDER["deephys"]
val DEEPHYS_TEST_RESULT_JSON = DEEPHYS_TEST_DATA_FOLDER["results.json"].toJioFile()
val DEEPHYS_RAM_SAMPLES_FOLDER = DEEPHYS_TEST_DATA_FOLDER["ram"]
val USER_LIB_FOLDER by lazy {
    requireIs<Mac>(thisMachine)
    USER_HOME["Library"]
}
val APP_SUPPORT_FOLDER by lazy {
    requireIs<Mac>(thisMachine)
    USER_LIB_FOLDER["Application Support"]
}
val RAM_NUMBERED_FILES by lazy {
    NumberedFiles(
        folder = DEEPHYS_RAM_SAMPLES_FOLDER.asFolder(),
        extension = FileExtension.JSON
    )
}

val PLATFORM_INDEPENDENT_APP_SUPPORT_FOLDER by lazy {
    when (thisMachine) {
        is Mac     -> APP_SUPPORT_FOLDER
        is Linux   -> {
            @SeeURL("https://stackoverflow.com/questions/6561172/find-directory-for-application-data-on-linux-and-macintosh")
            USER_HOME[".matt"].also { it.toJioFile().mkdir() }
        }

        is Windows -> {
            mFile("C:\\Users\\$userName\\AppData\\Roaming", WindowsFileSystem)
        }
    }
}
