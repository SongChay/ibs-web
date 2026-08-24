package kh.bank.dgb.ibs.app.cbs.upload_company_logo

import kh.bank.dgb.ibs.app.local.resource_file_info.ResourceFileInfo
import kh.bank.dgb.ibs.app.local.resource_file_info.ResourceFileInfoRbc
import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.UUID

/** Port of the CBS wire shape for `USR0102_REQ_UserProfileVo` / `USR0102_RES_UserProfileVo`
 *  (`DGBEBankingService.processUSR0102`, opcode `CIB11002932`) — the "corporate user logo
 *  modification" call the old adapter made *after* writing the file locally. */
data class UserProfileCbsRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val channelTypeCode: String? = null,
	val corporateUserProfileImageURL: String? = null,
)

data class UserProfileCbsResponse(
	val corporateUserProfileImageURL: String? = null,
)

/**
 * Port of `USR2201_Adapter_UploadCompanyLogo`.
 *
 * NOT a plain CBS pass-through — two distinct steps, both replicated faithfully:
 *  1. Local storage write (`resource_file_info` table, via `ResourceFileInfoRbc`): decode the
 *     uploaded file's base64 payload and either insert a new row (fresh upload) or update the
 *     existing one (re-upload — detected by the incoming `corporateUserProfileImageURL` already
 *     pointing at a resource id, exactly like the old adapter's `preCorporateUserProfileImageURL`
 *     branch).
 *  2. CBS call (`CIB11002932`) to register the new image URL against the corporate user profile.
 *
 * Old adapter built the new image URL as `conf.getResourceUrl() + "/" + resID`, where
 * `conf.getResourceUrl()` came from `${corp.banking.image.address}` (old
 * `PropertiesPlaceholderConfiguration`). That property hasn't been ported to
 * `CoreBankingProperties`/`application.yml` yet (out of scope for this batch, and this port isn't
 * allowed to touch either file) — `resource-url` below defaults inline via `@Value` so this still
 * runs out of the box, but the default almost certainly needs to be corrected for real
 * SIT/UAT/PROD image hosting. TODO: add a real `ibs.resource-url` (or similar) config key.
 *
 * Old adapter's failure handling had a real gap: when the CBS call's `header.result` was `false`,
 * it left the outer `ResponseData` completely empty (no header, no body) rather than surfacing the
 * CBS failure — almost certainly a bug, not intended behavior. This port instead always propagates
 * `cbsResult.header` so callers get a real result code. Flagged in the batch port report.
 */
@Service
class UploadCompanyLogoSbc(
	private val connector: CoreBankingApiConnector,
	private val resourceFileInfoRbc: ResourceFileInfoRbc,
	@Value("\${ibs.resource-url:http://localhost:8080/resource}") private val resourceUrl: String,
) {
	private val logger = LoggerFactory.getLogger(UploadCompanyLogoSbc::class.java)

	fun upload(request: RequestData<UploadCompanyLogoRequest>): ResponseData<UploadCompanyLogoResponse> {
		val body = request.body ?: return ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.FAILED), body = null)

		return try {
			val resId = storeLogoLocally(body)

			val cbsRequest = UserProfileCbsRequest(
				userID = body.userID,
				customerNo = body.customerNo,
				channelTypeCode = "01",
				corporateUserProfileImageURL = "$resourceUrl/$resId",
			)

			val cbsResult = connector.post(
				"CIB11002932",
				request.header?.languageCode,
				cbsRequest,
				UserProfileCbsResponse::class.java,
			)

			val responseBody = cbsResult.body?.takeIf { cbsResult.header?.result == true }
				?.let { UploadCompanyLogoResponse(corporateUserProfileImageURL = it.corporateUserProfileImageURL) }

			ResponseData(header = cbsResult.header, body = responseBody)
		} catch (e: Exception) {
			logger.error("Failed to upload company logo", e)
			ResponseData(header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.FAILED), body = null)
		}
	}

	/** Returns the resource id the logo was stored under (either the pre-existing one being
	 *  re-uploaded to, or a freshly generated one). */
	private fun storeLogoLocally(body: UploadCompanyLogoRequest): String {
		val newlyGeneratedId = UUID.randomUUID().toString()
		val fileData = decodeDataUrl(body.fileBase64)
		val preExistingUrl = body.corporateUserProfileImageURL?.trim()

		val resId = if (!preExistingUrl.isNullOrEmpty()) {
			preExistingUrl.substringAfterLast("/")
		} else {
			newlyGeneratedId
		}

		val profile = ResourceFileInfo(
			id = resId,
			fileTypeCode = "01", // BizResultCodeType.FILE_TYPE_CODE
			fileContentType = body.fileContentType,
			fileName = body.fileName,
			fileExt = body.fileExtension,
			fileSize = body.fileSize,
			fileData = fileData,
			createdBy = body.userID,
			updatedBy = body.userID,
		)

		if (!preExistingUrl.isNullOrEmpty()) {
			val existing = resourceFileInfoRbc.getResourceById(resId)
			if (existing == null) {
				resourceFileInfoRbc.addCompanyProfile(profile)
			} else {
				resourceFileInfoRbc.updateCompanyProfile(profile)
			}
		} else {
			resourceFileInfoRbc.addCompanyProfile(profile)
		}

		return resId
	}

	private fun decodeDataUrl(fileBase64: String?): ByteArray? {
		val raw = fileBase64 ?: return null
		val commaIndex = raw.indexOf(',')
		val encoded = if (commaIndex >= 0) raw.substring(commaIndex + 1) else raw
		return Base64.getDecoder().decode(encoded)
	}
}
