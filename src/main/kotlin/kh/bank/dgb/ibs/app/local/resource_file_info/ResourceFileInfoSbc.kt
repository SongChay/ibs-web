package kh.bank.dgb.ibs.app.local.resource_file_info

import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.stereotype.Service

/** Port of `GNB1004_Adapter_DownloadManual`. Picks the Khmer manual resource (`corporate_manual_kh`)
 *  when the request's language code is `"02"` (Khmer), English (`corporate_manual_en`) otherwise —
 *  same mapping as the old adapter's `rHeader.getLanguageCode()` check. */
@Service
class ResourceFileInfoSbc(
	private val resourceFileInfoRbc: ResourceFileInfoRbc,
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
}
