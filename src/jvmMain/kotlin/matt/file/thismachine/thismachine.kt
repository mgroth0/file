package matt.file.thismachine

import matt.lang.arch
import matt.lang.hostname
import matt.lang.os
import matt.lang.userHome
import matt.lang.userName
import matt.log.warn.warn
import matt.model.sys.GAMING_WINDOWS
import matt.model.sys.Machine
import matt.model.sys.NEW_MAC
import matt.model.sys.OLD_MAC
import matt.model.sys.OpenMind
import matt.model.sys.OpenMindDTN
import matt.model.sys.OpenMindMainHeadNode
import matt.model.sys.OpenMindSlurmNode
import matt.model.sys.Polestar
import matt.model.sys.UnknownIntelMacMachine
import matt.model.sys.UnknownLinuxMachine
import matt.model.sys.UnknownSiliconMacMachine
import matt.model.sys.UnknownWindowsMachine
import matt.model.sys.VagrantLinuxMachine
import matt.model.sys.WINDOWS_11_PAR_WORK

const val NEW_MAC_USERNAME = "matthewgroth"
const val OLD_MAC_USERNAME = "matt"
const val SLURM_NODE_HOSTNAME_PREFIX = "node"

val thisMachine: Machine by lazy {
  when {


	os == "Linux"        -> {
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
		  node = it, sImgLoc = System.getenv("SINGULARITY_CONTAINER"), slurmJobID = System.getenv("SLURM_JOBID")
		)
	  } ?: UnknownLinuxMachine(hostname = hostname, homeDir = userHome)
	}


	os.startsWith("Mac") -> when (arch) {
	  "aarch64" -> when (userName) {
		NEW_MAC_USERNAME -> NEW_MAC
		else             -> UnknownSiliconMacMachine(homeDir = userHome)
	  }

	  else      -> {
		warn("arch($arch) is not aarch64. add this new value to the when expression and remove the \"else\"")
		when (userName) {
		  OLD_MAC_USERNAME -> OLD_MAC
		  else             -> UnknownIntelMacMachine(homeDir = userHome)
		}
	  }
	}

	else                 -> when (userName) {
	  "mgrot"        -> GAMING_WINDOWS
	  "matthewgroth" -> WINDOWS_11_PAR_WORK
	  else           -> UnknownWindowsMachine()
	}
  }
}