package matt.file.context

import matt.file.thismachine.thisMachine
import matt.model.code.sys.OpenMind
import matt.model.code.sys.SiliconMac


val RUNTIME_COMPUTE_CONTEXT by lazy {
    when {
        (thisMachine is SiliconMac) -> LocalComputeContext
        thisMachine is OpenMind     -> OpenMindComputeContext
        else                        -> TODO("detect runtime compute context robustly")
    }
}