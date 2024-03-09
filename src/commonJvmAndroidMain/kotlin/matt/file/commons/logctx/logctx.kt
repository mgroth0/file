package matt.file.commons.logctx

import matt.file.toJioFile
import matt.lang.model.file.AnyFsFile

class LogContext(parentFolder: AnyFsFile) {
    val logFolder by lazy {
        parentFolder["log"].toJioFile().apply { mkdirs() }
    }
    val exceptionFolder by lazy {
        logFolder["errorReports"]
    }
    val enforcedLog by lazy {
        parentFolder["enforced_out_and_err.log"]
    }
}
