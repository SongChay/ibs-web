package kh.bank.dgb.ibs.app.local.file_download

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Port of the old `BIZMOB_HOME` JVM system property (used by `DownloadController.doDownload` to
 * build `BIZMOB_HOME/ibs/res/docs/{filename}`) — the base directory the two generic file-download
 * endpoints in `FileDownloadCbc` serve from. No real value was ever in this repo (it was set per
 * WildFly deployment, outside the codebase); override via `IBS_DOWNLOAD_DOCS_BASE_PATH`.
 */
@Component
@ConfigurationProperties(prefix = "ibs.download")
data class FileDownloadProperties(
	var docsBasePath: String = "./docs",
)
