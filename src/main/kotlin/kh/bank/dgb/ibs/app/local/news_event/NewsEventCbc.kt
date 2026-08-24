package kh.bank.dgb.ibs.app.local.news_event

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class NoticeListRequest(
	val currentPage: Int = 1,
	val pageSize: Int = 10,
	val searchKeyword: String? = null,
)

data class NoticeListItem(
	val id: Int,
	val titleEn: String?,
	val titleKh: String?,
	val createdDate: String?,
	val viewCount: Long,
	val categoryCode: String?,
	val categoryName: String?,
)

data class NoticeListResponse(
	val recordsTotal: Long,
	val recordsFiltered: Long,
	val noticeList: List<NoticeListItem>,
)

/**
 * Port of `ADS2001_Adapter_InquiryNoticeList` — purely local, no CBS call. Old response VO also
 * had `typeId`/`contentEn`/`contentKh` fields the adapter never actually populated (dead fields,
 * one pair literally commented out); dropped here rather than carried over unused.
 */
@RestController
@RequestMapping("/api/notices")
class NewsEventCbc(
	private val newsEventSbc: NewsEventSbc,
) {

	@PostMapping
	fun list(@RequestBody request: RequestData<NoticeListRequest>): ResponseData<NoticeListResponse> {
		val body = request.body ?: NoticeListRequest()
		val result = newsEventSbc.getNoticeList(body.currentPage, body.pageSize, body.searchKeyword)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}
}
