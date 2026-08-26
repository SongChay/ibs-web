package kh.bank.dgb.ibs.app.cbs.upload_company_logo

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class UploadCompanyLogoRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	val corporateUserProfileImageURL: String? = null,
	val fileName: String? = null,
	val fileExtension: String? = null,
	val fileSize: Long? = null,
	val fileContentType: String? = null,
	val fileBase64: String? = null,
)

data class UploadCompanyLogoResponse(
	val corporateUserProfileImageURL: String? = null,
)

/**
 * Port of `USR2201_Adapter_UploadCompanyLogo` — writes the uploaded logo to local storage
 * (`resource_file_info`) AND calls CBS opcode `CIB11002932` to register the new image URL against
 * the corporate user profile. See `UploadCompanyLogoSbc` for the full behavior; this is NOT a
 * plain CBS pass-through.
 */
@RestController
@RequestMapping("/USR2201")
class UploadCompanyLogoCbc(
	private val uploadCompanyLogoSbc: UploadCompanyLogoSbc,
) {
	@PostMapping
	fun upload(@RequestBody request: RequestData<UploadCompanyLogoRequest>): ResponseData<UploadCompanyLogoResponse> {
		return uploadCompanyLogoSbc.upload(request)
	}
}
