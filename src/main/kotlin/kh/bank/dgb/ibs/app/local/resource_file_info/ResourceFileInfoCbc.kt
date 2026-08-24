package kh.bank.dgb.ibs.app.local.resource_file_info

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DownloadManualRequest(
	val version: String? = null,
)

/** Port of `GNB1004_RES_DownloadManualVo`. */
data class DownloadManualResponse(
	val resID: String? = null,
	val fileName: String? = null,
	val fileExt: String? = null,
	val downloadUrl: String? = null,
)

/**
 * Port of `GNB1004_Adapter_DownloadManual` — purely local, no CBS call.
 *
 * NOTE on response shape: the old adapter itself returns JSON metadata (resource id, file name/
 * extension, and a `downloadUrl`) through the normal envelope — it does NOT stream file bytes.
 * The actual byte-serving happens at a *separate* `GET /download/manual/{resID}` endpoint (old
 * `DownloadController.downloadManual`, a plain byte-copy to the servlet response) that isn't part
 * of this port's batch. So this fits the `RequestData`/`ResponseData` JSON envelope pattern as-is;
 * flagged in the batch port report since serving the actual manual bytes still needs a follow-up
 * (non-adapter, no legacy `RequestData`/`ResponseData` envelope) endpoint ported separately.
 *
 * `USR2201_Adapter_UploadCompanyLogo` does NOT live in this file (checked) — it's ported
 * separately under `app/cbs/upload_company_logo/UploadCompanyLogoCbc.kt` since it's CBS-backed.
 * Only one old adapter (`GNB1004`) is present here, so the class-level `@RequestMapping` is kept
 * (rather than removed) and simply pointed at that adapter's single absolute route.
 */
@RestController
@RequestMapping("/GNB1004")
class ResourceFileInfoCbc(
	private val sbc: ResourceFileInfoSbc,
) {
	@PostMapping
	fun downloadManual(@RequestBody request: RequestData<DownloadManualRequest>): ResponseData<DownloadManualResponse> =
		sbc.downloadManual(request.header?.languageCode)
}
