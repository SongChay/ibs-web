package kh.bank.dgb.ibs.app.local.news_event

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

/** Port of `MAN1005_REQ_LastestNoticeDetailVo`. None of these fields are actually read by the
 *  old adapter (it calls `newsEventService.getTop3Notice()` with no arguments) — kept here only
 *  to represent the wire contract faithfully. */
data class Top3NoticeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
)

data class NoticeDetailRequest(
	val userID: String? = null,
	val id: Int = 0,
)

/** Port of `ADS2101_RES_InquiryNoticeNextlVo` / `ADS2101_RES_InquiryNoticePrelVo` — same shape,
 *  reused for both the next and previous adjacent-notice summaries. */
data class NoticeAdjacent(
	val id: Int,
	val titleEn: String? = null,
	val titleKh: String? = null,
)

/** Port of `ADS2101_RES_InquiryNoticeDetailVo`. `typeId` was a dead field in the old Vo (never
 *  populated), dropped here same as the `list`/`top3` endpoints above. `noticeAttachVo`
 *  (attachment list) is also dropped: it depends on `BbsBoardAttachDTO`/attachment storage that
 *  hasn't been ported to this project yet (no `bbs_board_attach` Rbc exists) — out of scope for
 *  this batch. TODO: port notice attachments (and the `/download/attachment/{id}` file-serving
 *  endpoint) separately, then restore this field. */
data class NoticeDetailResponse(
	val id: Int,
	val titleEn: String? = null,
	val titleKh: String? = null,
	val contentEn: String? = null,
	val contentKh: String? = null,
	val createdDate: String? = null,
	val viewCount: Long = 0,
	val categoryName: String? = null,
	val inquiryNoticeNextlVo: NoticeAdjacent? = null,
	val inquiryNoticePrelVo: NoticeAdjacent? = null,
)

/**
 * Bundles three unrelated old adapters into one feature file — each method below has its own
 * absolute route matching that specific old adapter's `@Adapter(route=...)`, since there is no
 * shared path prefix across them (no class-level `@RequestMapping`).
 */
@RestController
class NewsEventCbc(
	private val newsEventSbc: NewsEventSbc,
) {

	/**
	 * Port of `ADS2001_Adapter_InquiryNoticeList` — purely local, no CBS call. Old response VO also
	 * had `typeId`/`contentEn`/`contentKh` fields the adapter never actually populated (dead fields,
	 * one pair literally commented out); dropped here rather than carried over unused.
	 */
	@PostMapping("/ADS2001")
	fun list(@RequestBody request: RequestData<NoticeListRequest>): ResponseData<NoticeListResponse> {
		val body = request.body ?: NoticeListRequest()
		val result = newsEventSbc.getNoticeList(body.currentPage, body.pageSize, body.searchKeyword)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}

	/** Port of `MAN1005_Adapter_InquiryTop3Notices` — purely local, no CBS call. Delegates to
	 *  `NewsEventService.getTop3Notice()` via `NewsEventRbc.getTop3Notice()`. Old response VO's
	 *  `contentEn`/`contentKh` fields were commented out (never populated) in the old adapter;
	 *  dropped here rather than carried over unused, same as the `list` endpoint above. */
	@PostMapping("/MAN1005")
	fun top3(@RequestBody(required = false) request: RequestData<Top3NoticeRequest>?): ResponseData<List<NoticeListItem>> {
		val result = newsEventSbc.getTop3Notices()
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}

	/** Port of `ADS2101_Adapter_InquiryNoticeDetail` — purely local, no CBS call. Also bumps the
	 *  notice's view count as a side effect (`NewsEventRbc.updateViewCount`), same as the old
	 *  adapter. */
	@PostMapping("/ADS2101")
	fun detail(@RequestBody request: RequestData<NoticeDetailRequest>): ResponseData<NoticeDetailResponse> {
		val id = request.body?.id ?: 0
		val result = newsEventSbc.getNoticeDetail(id)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = result)
	}
}
