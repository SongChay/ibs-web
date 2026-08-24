package kh.bank.dgb.ibs.app.local.faq

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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

/** Port of `ADS3001_Adapter_InquiryFaqList` (list) + `ADS3101_Adapter_InquiryFaqDetail` (detail)
 *  — one feature, two endpoints, matching the visited-notice pattern. Both purely local. */
@RestController
@RequestMapping("/api/faq")
class FaqCbc(
	private val faqSbc: FaqSbc,
) {

	@PostMapping
	fun list(@RequestBody request: RequestData<FaqListRequest>): ResponseData<FaqListResponse> {
		val body = request.body ?: FaqListRequest()
		val result = faqSbc.getList(body.currentPage, body.pageSize, body.searchKeyword)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}

	@PostMapping("/detail")
	fun detail(@RequestBody request: RequestData<FaqDetailRequest>): ResponseData<FaqListItem?> {
		val result = faqSbc.getDetail(request.body!!.id)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}
}
