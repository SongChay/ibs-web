package kh.bank.dgb.ibs.app.local.faq

import kh.bank.dgb.ibs.common.query.PageQuery
import org.springframework.stereotype.Service

@Service
class FaqSbc(
	private val faqRbc: FaqRbc,
) {

	fun getList(currentPage: Int, pageSize: Int, searchKeyword: String?): FaqListResponse {
		val start = (currentPage - 1) * pageSize
		val query = PageQuery(searchKeyword = searchKeyword, start = start, pageSize = pageSize)

		val items = faqRbc.getAll(query)
		val total = faqRbc.getCountAll()
		val filtered = faqRbc.getCountFilter(query)

		return FaqListResponse(
			recordsTotal = total,
			recordsFiltered = filtered,
			faqList = items.map { it.toListItem() },
		)
	}

	fun getDetail(id: Int): FaqListItem? = faqRbc.getFaqById(id)?.toListItem()

	private fun Faq.toListItem() = FaqListItem(
		faqId = faqId,
		categoryName = categoryName,
		categoryNameKh = categoryNameKh,
		categoryCode = categoryCode,
		titleEn = titleEn,
		contentEn = contentEn,
		titleKh = titleKh,
		contentKh = contentKh,
		publishDateTime = publishDateTime?.toString(),
	)
}
