package kh.bank.dgb.ibs.app.local.news_event

import kh.bank.dgb.ibs.common.query.PageQuery
import org.springframework.stereotype.Service

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
}
