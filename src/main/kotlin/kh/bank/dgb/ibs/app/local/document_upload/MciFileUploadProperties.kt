package kh.bank.dgb.ibs.app.local.document_upload

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Port of the old `PropertiesPlaceholderConfiguration.mciFileUploadUrl` (`${mci.server.uploadfile}`)
 * — an external, non-CBS file-upload service ("MCI") that `/upload/docfile` proxies to. Genuinely
 * unreachable/unverified from this environment, same as every other external integration point in
 * this app (CBS, CBS's own RSA endpoint) — no real value was ever in this repo, per the old app's
 * per-environment file-swap convention. Override via `IBS_MCI_UPLOAD_URL`.
 */
@Component
@ConfigurationProperties(prefix = "ibs.mci")
data class MciFileUploadProperties(
	var uploadUrl: String = "http://127.0.0.1:9090/mci/upload",
)
