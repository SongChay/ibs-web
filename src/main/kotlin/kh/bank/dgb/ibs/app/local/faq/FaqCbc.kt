package kh.bank.dgb.ibs.app.local.faq

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class FaqListRequest(
	val currentPage: Int = 1,
	val pageSize: Int = 10,
	val searchKeyword: String? = null,
)

data class FaqListItem(
	val faqId: Int,
	val categoryName: String?,
	val categoryNameKh: String?,
	val categoryCode: String?,
	val titleEn: String?,
	val contentEn: String?,
	val titleKh: String?,
	val contentKh: String?,
	val publishDateTime: String?,
)

data class FaqListResponse(
	val recordsTotal: Long,
	val recordsFiltered: Long,
	val faqList: List<FaqListItem>,
)

data class FaqDetailRequest(
	val id: Int,
)

/** Bundles two unrelated old adapters — `ADS3001_Adapter_InquiryFaqList` (list) and
 *  `ADS3101_Adapter_InquiryFaqDetail` (detail) — in one feature file. Both purely local. Each
 *  method carries its own absolute route matching its old adapter (no class-level
 *  `@RequestMapping`, since the two routes share no common prefix). */
@RestController
class FaqCbc(
	private val faqSbc: FaqSbc,
) {

	/** Port of `ADS3001_Adapter_InquiryFaqList`. */
	@PostMapping("/ADS3001")
	fun list(@RequestBody request: RequestData<FaqListRequest>): ResponseData<FaqListResponse> {
		val body = request.body ?: FaqListRequest()
		val result = faqSbc.getList(body.currentPage, body.pageSize, body.searchKeyword)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}

	/** Port of `ADS3101_Adapter_InquiryFaqDetail`. */
	@PostMapping("/ADS3101")
	fun detail(@RequestBody request: RequestData<FaqDetailRequest>): ResponseData<FaqListItem?> {
		val result = faqSbc.getDetail(request.body!!.id)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}
}
