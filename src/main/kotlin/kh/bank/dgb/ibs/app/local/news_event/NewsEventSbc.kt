package kh.bank.dgb.ibs.app.local.news_event

import kh.bank.dgb.ibs.common.query.PageQuery
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter
import java.util.Locale

@Service
class NewsEventSbc(
	private val newsEventRbc: NewsEventRbc,
) {

	fun getNoticeList(currentPage: Int, pageSize: Int, searchKeyword: String?): NoticeListResponse {
		val start = (currentPage - 1) * pageSize
		val query = PageQuery(searchKeyword = searchKeyword, start = start, pageSize = pageSize)

		val items = newsEventRbc.getAll(query)
		val total = newsEventRbc.getCountAll()
		val filtered = newsEventRbc.getCountFilter(query)

		return NoticeListResponse(
			recordsTotal = total,
			recordsFiltered = filtered,
			noticeList = items.map {
				NoticeListItem(
					id = it.id,
					titleEn = it.titleEn,
					titleKh = it.titleKh,
					createdDate = it.createdDate?.toString(),
					viewCount = (it.viewCount ?: 0).toLong(),
					categoryCode = it.categoryCode,
					categoryName = it.categoryName,
				)
			},
		)
	}

	/** Port of `NewsEventService.getTop3Notice()` as called from `MAN1005_Adapter_InquiryTop3Notices`
	 *  — only `id`/`titleEn`/`titleKh` are populated (matching the old adapter, which left
	 *  `contentEn`/`contentKh` commented out); the fields `NoticeListItem` carries for the paged
	 *  `list` endpoint (`createdDate`, `viewCount`, `categoryCode`, `categoryName`) have no
	 *  equivalent in the old MAN1005 response Vo and are left null/zero here. */
	fun getTop3Notices(): List<NoticeListItem> {
		return newsEventRbc.getTop3Notice().map {
			NoticeListItem(
				id = it.id,
				titleEn = it.titleEn,
				titleKh = it.titleKh,
				createdDate = null,
				viewCount = 0,
				categoryCode = null,
				categoryName = null,
			)
		}
	}

	/** Port of `ADS2101_Adapter_InquiryNoticeDetail`. Also fetches the next/previous adjacent
	 *  notices and bumps the view count, same as the old adapter. `createdDate` is formatted
	 *  `dd MMM yyyy, hh:mm:ss a` (port of `DateUtil.toDDMMMYYYYHHMMSSA`), unlike the plain
	 *  `.toString()` used by the paged `list` endpoint above — that's a real difference in the old
	 *  app (detail view formats for display, list view didn't), not an inconsistency introduced
	 *  here. */
	fun getNoticeDetail(id: Int): NoticeDetailResponse {
		val notice = newsEventRbc.findById(id)
		val next = newsEventRbc.findNextId(id)
		val previous = newsEventRbc.findPreviousId(id)

		newsEventRbc.updateViewCount(id)

		return NoticeDetailResponse(
			id = notice?.id ?: id,
			titleEn = notice?.titleEn,
			titleKh = notice?.titleKh,
			contentEn = notice?.contentEn,
			contentKh = notice?.contentKh,
			createdDate = notice?.createdDate?.format(DISPLAY_DATE_FORMAT),
			viewCount = (notice?.viewCount ?: 0).toLong(),
			categoryName = notice?.categoryName,
			inquiryNoticeNextlVo = next?.let { NoticeAdjacent(id = it.id, titleEn = it.titleEn, titleKh = it.titleKh) },
			inquiryNoticePrelVo = previous?.let { NoticeAdjacent(id = it.id, titleEn = it.titleEn, titleKh = it.titleKh) },
		)
	}

	private companion object {
		val DISPLAY_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a", Locale.ENGLISH)
	}
}
