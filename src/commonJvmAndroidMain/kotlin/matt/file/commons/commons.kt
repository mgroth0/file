package matt.file.commons


import matt.file.commons.ec2commons.DEFAULT_UBUNTU_HOME_FOLDER
import matt.file.construct.mFile
import matt.file.thismachine.thisMachine
import matt.file.toJioFile
import matt.lang.model.file.AnyFsFile
import matt.lang.sysprop.props.UserDir
import matt.model.code.sys.LinuxFileSystem


const val DEFAULT_GITHUB_BRANCH_NAME = "master"


object AndroidFiles {
    val PUSHED_FOLDER_NAME = "pushed".lowercase() /*case sensitive*/
    val KEYS_FILE_NAME = ".keys".lowercase() /*case sensitive*/
    private val dataFile = mFile("/data", LinuxFileSystem)
    val dataDataFile = dataFile["data"]
    val pushedFolder = dataFile["local/tmp"][PUSHED_FOLDER_NAME]
}


val USER_DIR by lazy {
    val uDir = UserDir.get()
    check("?" !in uDir) {
        "weird thing happened where question mark is in user.dir: $uDir"
    }
    println("CHECKING uDir:$uDir")
    mFile(uDir, thisMachine.fileSystemFor(uDir))
}
val WEB_TMP_DIR by lazy {
    val s = "/tmp"
    mFile(s, thisMachine.fileSystemFor(s)).also { it.toJioFile().mkdir() }
}


class RedisCertFiles(private val dir: AnyFsFile) {
    fun mkdirs() = dir.toJioFile().mkdirs()
    val privateKeyFile = dir["redis-private.key"]
    val csrFile = dir["redis.csr"]
    val certFile = dir["redis.cert"]
}

val remoteSharableCertFiles = RedisCertFiles(DEFAULT_UBUNTU_HOME_FOLDER)
