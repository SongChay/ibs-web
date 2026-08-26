package kh.bank.dgb.ibs.app.local.resource_file_info

import kh.bank.dgb.ibs.cbs.client.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.imageio.ImageIO

/** Port of `USR0102_REQ_UserProfileVo` — notifies CBS of the new logo URL. Kept private/descriptive
 *  per this project's naming convention; the wire opcode itself is `CIB11002932`. */
private data class UpdateCorporateLogoRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	val corporateUserProfileImageURL: String? = null,
)

/** Port of `USR0102_RES_UserProfileVo`. */
private data class UpdateCorporateLogoResponse(
	val corporateUserProfileImageURL: String? = null,
)

/** Port of `GNB1004_Adapter_DownloadManual`. Picks the Khmer manual resource (`corporate_manual_kh`)
 *  when the request's language code is `"02"` (Khmer), English (`corporate_manual_en`) otherwise —
 *  same mapping as the old adapter's `rHeader.getLanguageCode()` check. */
@Service
class ResourceFileInfoSbc(
	private val resourceFileInfoRbc: ResourceFileInfoRbc,
	private val coreBankingApiConnector: CoreBankingApiConnector,
	private val resourceFileProperties: ResourceFileProperties,
) {
	fun downloadManual(languageCode: String?): ResponseData<DownloadManualResponse> {
		val resId = if (languageCode.equals("02", ignoreCase = true)) "corporate_manual_kh" else "corporate_manual_en"
		val resource = resourceFileInfoRbc.getResourceById(resId)

		return if (resource != null) {
			ResponseData(
				header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS),
				body = DownloadManualResponse(
					resID = resource.id,
					fileName = resource.fileName,
					fileExt = resource.fileExt,
					downloadUrl = "/download/manual/${resource.id}",
				),
			)
		} else {
			ResponseData(
				header = ResponseResultUtils.makeResponse(false, ResponseResultCodeType.FILE_NOT_FOUND),
				body = DownloadManualResponse(),
			)
		}
	}

	/**
	 * Port of `CompanyProfileController.handleFileUpload` (`/upload/companyProfile`). Stores the
	 * uploaded file locally (reusing the DAO methods ported earlier as building blocks — nothing
	 * ever wired them into an actual upload flow until now), then notifies CBS of the new URL via
	 * opcode `CIB11002932`.
	 *
	 * Two things NOT replicated from the old code, both deliberate:
	 *  - An empty upload old-code path built a control-character-delimited (`0x1C`/`0x1A`/`0x1F`)
	 *    "SaveFileName=..." string into a local `returnMsg` buffer that was never actually returned
	 *    — dead code (the method's real return value was always the JSON `resData`), so it's
	 *    dropped here rather than reproduced.
	 *  - `languageCode` was hardcoded to the literal `"01"` in the old controller (it builds its own
	 *    request header from scratch here, bypassing the normal per-request language normalization
	 *    entirely) — replicated as the same hardcoded value, not silently "fixed" to the real user's
	 *    language, since that's what CBS actually received historically.
	 */
	fun uploadCompanyProfile(
		file: MultipartFile,
		previousImageUrl: String?,
		userId: String,
		customerNo: String,
	): ResponseData<UploadCompanyProfileResponse> {
		if (file.isEmpty) {
			// Old adapter's own quirk: an empty upload is a soft success, not an error, despite
			// logging one — replicated exactly since it isn't clearly a bug, just odd.
			return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS))
		}

		val originalName = file.originalFilename ?: ""
		val dotIndex = originalName.lastIndexOf('.')
		val fileName = if (dotIndex > 0) originalName.substring(0, dotIndex) else originalName
		val fileExt = if (dotIndex > 0) originalName.substring(dotIndex + 1) else ""

		val resourceId = if (!previousImageUrl.isNullOrBlank()) previousImageUrl.substringAfterLast('/') else UUID.randomUUID().toString()

		val resource = ResourceFileInfo(
			id = resourceId,
			fileTypeCode = FILE_TYPE_CODE,
			fileName = fileName,
			fileExt = fileExt,
			fileContentType = file.contentType,
			fileSize = file.size,
			fileData = file.bytes,
			createdBy = userId,
			updatedBy = userId,
		)

		if (!previousImageUrl.isNullOrBlank() && resourceFileInfoRbc.getResourceById(resourceId) != null) {
			resourceFileInfoRbc.updateCompanyProfile(resource)
		} else {
			resourceFileInfoRbc.addCompanyProfile(resource)
		}

		val cbsResult = coreBankingApiConnector.post(
			OPCODE_UPDATE_LOGO,
			HARDCODED_LANGUAGE_CODE,
			UpdateCorporateLogoRequest(
				userID = userId,
				channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING,
				customerNo = customerNo,
				corporateUserProfileImageURL = "${resourceFileProperties.imageBaseUrl}/$resourceId",
			),
			UpdateCorporateLogoResponse::class.java,
		)

		return ResponseData(
			header = cbsResult.header,
			body = UploadCompanyProfileResponse(corporateUserProfileImageURL = cbsResult.body?.corporateUserProfileImageURL),
		)
	}

	/**
	 * Port of `CompanyProfileController.getCompanyProfile` (`GET /api/images/resources/{resID}`).
	 * Resizes down (preserving aspect ratio, larger side capped at 600px) when the stored image
	 * exceeds 200KB, same threshold as the old `imgscalr`-based code — done here with plain JDK
	 * `ImageIO`/`Graphics2D` instead of adding the `imgscalr` dependency back, same visual result
	 * for the normal case (a photo/logo, not something relying on `imgscalr`'s specific resampling
	 * algorithm).
	 *
	 * Old code always returns HTTP 200 regardless of whether the resource/image was found or
	 * resizing failed (empty body in that case) — kept exactly, including on a resize failure
	 * (falls back to the original, unresized bytes rather than failing the whole request).
	 */
	fun getResourceImage(resId: String): ResponseEntity<ByteArray> {
		val resource = resourceFileInfoRbc.getResourceById(resId)
		val data = resource?.fileData

		if (resource == null || data == null || data.isEmpty()) {
			return ResponseEntity.ok().build()
		}

		val bytes = if (data.size > MAX_INLINE_SIZE) scaleDown(data, resource.fileExt) else data
		val mediaType = when {
			resource.fileExt.equals("jpg", ignoreCase = true) -> MediaType.IMAGE_JPEG
			resource.fileExt.equals("png", ignoreCase = true) -> MediaType.IMAGE_PNG
			else -> MediaType.IMAGE_PNG
		}

		return ResponseEntity.ok()
			.contentType(mediaType)
			.contentLength(bytes.size.toLong())
			.body(bytes)
	}

	/**
	 * Port of `DownloadController.downloadManual` (`GET /download/manual/{resID}`) — the actual
	 * byte-serving half of `downloadManual` above, which only returns JSON metadata (a
	 * `downloadUrl` pointing at this exact route). Old code silently did nothing on a missing
	 * resource (200, empty body, no content-type) — kept exactly, matching `getResourceImage`'s
	 * same convention rather than switching to a real 404.
	 */
	fun downloadManualFile(resId: String): ResponseEntity<ByteArray> {
		val resource = resourceFileInfoRbc.getResourceById(resId)
		val data = resource?.fileData ?: return ResponseEntity.ok().build()

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, resource.fileContentType ?: "application/octet-stream")
			.body(data)
	}

	private fun scaleDown(data: ByteArray, ext: String?): ByteArray {
		return runCatching {
			val original = ImageIO.read(ByteArrayInputStream(data)) ?: return data
			val scale = TARGET_SIZE.toDouble() / maxOf(original.width, original.height)
			if (scale >= 1.0) return data

			val newWidth = (original.width * scale).toInt().coerceAtLeast(1)
			val newHeight = (original.height * scale).toInt().coerceAtLeast(1)
			val scaled = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
			val graphics = scaled.createGraphics()
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
			graphics.drawImage(original, 0, 0, newWidth, newHeight, null)
			graphics.dispose()

			val formatName = if (ext.equals("jpg", ignoreCase = true) || ext.equals("jpeg", ignoreCase = true)) "jpg" else "png"
			ByteArrayOutputStream().also { ImageIO.write(scaled, formatName, it) }.toByteArray()
		}.getOrDefault(data)
	}

	companion object {
		/** Port of `DGBEBankingServiceImpl.processUSR0102`'s opcode. */
		private const val OPCODE_UPDATE_LOGO = "CIB11002932"
		/** Port of `BizResultCodeType.FILE_TYPE_CODE`. */
		private const val FILE_TYPE_CODE = "01"
		/** Port of `BizResultCodeType.CHANNEL_TYPE_CODE_CORP_BANKING`. */
		private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"
		private const val HARDCODED_LANGUAGE_CODE = "01"
		private const val MAX_INLINE_SIZE = 204_800L // 200KB
		private const val TARGET_SIZE = 600
	}
}
