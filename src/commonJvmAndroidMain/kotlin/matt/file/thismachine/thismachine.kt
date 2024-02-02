package matt.file.thismachine

import matt.lang.function.Op
import matt.lang.hostname
import matt.lang.platform.ARCH
import matt.lang.platform.Linux
import matt.lang.platform.Mac
import matt.lang.platform.OS
import matt.lang.userHome
import matt.lang.userName
import matt.log.warn.warn
import matt.model.code.sys.NewMac
import matt.model.code.sys.OldMac
import matt.model.code.sys.OpenMind
import matt.model.code.sys.OpenMindDTN
import matt.model.code.sys.OpenMindMainHeadNode
import matt.model.code.sys.OpenMindSlurmNode
import matt.model.code.sys.Polestar
import matt.model.code.sys.UnknownIntelMacMachine
import matt.model.code.sys.UnknownLinuxMachine
import matt.model.code.sys.UnknownSiliconMacMachine
import matt.model.code.sys.UnknownWindowsMachine
import matt.model.code.sys.VagrantLinuxMachine

const val NEW_MAC_USERNAME = "matthewgroth"
const val OLD_MAC_USERNAME = "matt"
const val SLURM_NODE_HOSTNAME_PREFIX = "node"

const val PRETEND_NOT_MATT = false

fun isMatt(): Boolean {
    if (PRETEND_NOT_MATT) {
        warn("PRETEND_NOT_MATT=true")
    }
    return !PRETEND_NOT_MATT && userName == NEW_MAC_USERNAME
}

fun ifMatt(op: Op) {
    if (isMatt()) op()
}


val thisMachine by lazy {
    when (OS) {


        Linux -> {
            if (hostname == "vagrant") VagrantLinuxMachine()
            else (if (hostname.startsWith(SLURM_NODE_HOSTNAME_PREFIX)) OpenMindSlurmNode(

                /*sometimes it looks like "node062", other times it looks like "node062.cm.cluster"*/

                hostname.substringAfter(SLURM_NODE_HOSTNAME_PREFIX).substringBefore(".").toInt()
            ) else when (hostname) {
                "polestar"             -> Polestar
                "OPENMIND-DTN.MIT.EDU" -> OpenMindDTN
                "openmind7"            -> OpenMindMainHeadNode
                else                   -> null
            })?.let {
                OpenMind(
                    node = it,
                    sImgLoc = System.getenv("SINGULARITY_CONTAINER"),
                    slurmJobID = System.getenv("SLURM_JOBID")
                )
            } ?: UnknownLinuxMachine(hostname = hostname, homeDir = userHome, isAarch64 = lazy {

                val p = ProcessBuilder("dpkg", "--print-architecture").start()
                try {
                    /*processRepeaper.ensureProcessEndsWithThisJvm(p)*/
                    p.inputStream.readAllBytes()
                        .decodeToString()
                        .trim() in listOf("arm64", "aarch64")
                } finally {
                    /*Dodge requirement to use ProcessReaper here, which would have devastating effects on everything that statically depends on theis variable*/
                    p.destroyForcibly()
                }
            })
        }


        Mac   -> when (ARCH) {
            "aarch64" -> when (userName) {
                NEW_MAC_USERNAME -> NewMac
                else             -> UnknownSiliconMacMachine(homeDir = userHome)
            }

            else      -> {
                warn("arch($ARCH) is not aarch64. add this new value to the when expression and remove the \"else\"")
                when (userName) {
                    OLD_MAC_USERNAME -> OldMac
                    else             -> UnknownIntelMacMachine(homeDir = userHome)
                }
            }
        }

        else  -> when (userName) {
            /*THESE MADE ME TESTING ON WINDOWS MACHINES LESS RELIABLE*/
            /*"mgrot"        -> GAMING_WINDOWS*/
            /*"matthewgroth" -> WINDOWS_11_PAR_WORK*/
            else -> UnknownWindowsMachine()
        }
    }
}
