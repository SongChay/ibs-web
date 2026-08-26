package kh.bank.dgb.ibs.app.local.resource_file_info

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

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

/** Port of `USR0102_RES_UserProfileVo`, as returned to our own client (not CBS — that's the
 *  private `UpdateCorporateLogoResponse` in the Sbc). */
data class UploadCompanyProfileResponse(
	val corporateUserProfileImageURL: String? = null,
)

/**
 * Port of three old `web/view/controller` classes that share this feature's local resource-file
 * storage, none of which fit the usual `{header,body}`-enveloped adapter shape:
 *  - `GNB1004_Adapter_DownloadManual` — the one genuine adapter here, purely local, no CBS call.
 *  - `CompanyProfileController.handleFileUpload` (`/upload/companyProfile`) — a raw multipart
 *    upload endpoint (`@RequestParam`, not `@RequestBody`); response still uses the normal
 *    `ResponseData` envelope. See `ResourceFileInfoSbc.uploadCompanyProfile` for the real logic.
 *  - `CompanyProfileController.getCompanyProfile` (`GET /api/images/resources/{resID}`) — serves
 *    raw image bytes, not JSON at all. Confirmed still actively called by the real client (its
 *    homepage fetches a company logo through exactly this path) — this was a genuine gap where the
 *    old whitelist entry existed in `SecurityBean` but no controller ever backed it.
 *  - `DownloadController.downloadManual` (`GET /download/manual/{resID}`) — the byte-serving half
 *    of `GNB1004`, which only ever returns JSON metadata pointing at this route. Same "whitelist
 *    entry existed, no controller backed it" gap as the image endpoint above.
 *
 * Class-level `@RequestMapping` is dropped (unlike when only `GNB1004` lived here) since these
 * routes don't share a common prefix — each method carries its own full absolute path.
 */
@RestController
class ResourceFileInfoCbc(
	private val resourceFileInfoSbc: ResourceFileInfoSbc,
) {
	@PostMapping("/GNB1004")
	fun downloadManual(@RequestBody request: RequestData<DownloadManualRequest>): ResponseData<DownloadManualResponse> {
		return resourceFileInfoSbc.downloadManual(request.header?.languageCode)
	}

	@PostMapping("/upload/companyProfile")
	fun uploadCompanyProfile(
		@RequestParam("userFile") file: MultipartFile,
		@RequestParam("corporateUserProfileImageURL", required = false) corporateUserProfileImageURL: String?,
		@RequestParam("userID") userID: String,
		@RequestParam("customerNo") customerNo: String,
	): ResponseData<UploadCompanyProfileResponse> {
		return resourceFileInfoSbc.uploadCompanyProfile(file, corporateUserProfileImageURL, userID, customerNo)
	}

	@GetMapping("/api/images/resources/{resID}")
	fun getResourceImage(@PathVariable resID: String): ResponseEntity<ByteArray> {
		return resourceFileInfoSbc.getResourceImage(resID)
	}

	@GetMapping("/download/manual/{resID}")
	fun downloadManualFile(@PathVariable resID: String): ResponseEntity<ByteArray> {
		return resourceFileInfoSbc.downloadManualFile(resID)
	}
}
