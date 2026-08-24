package kh.bank.dgb.ibs.app.faq

import kh.bank.dgb.ibs.common.query.PageQuery
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

/** Port of `FaqDTO`. */
data class Faq(
	val faqId: Int,
	val categoryName: String? = null,
	val categoryNameKh: String? = null,
	val categoryCode: String? = null,
	val titleEn: String? = null,
	val contentEn: String? = null,
	val titleKh: String? = null,
	val contentKh: String? = null,
	val publishDateTime: LocalDateTime? = null,
)

/** Port of `FaqDAO` — `bbs_board` entries under category `015` (FAQ), joined to `bbs_category`
 *  for category display names. */
@Mapper
interface FaqRbc {
	fun getAll(@Param("dataGridDTO") query: PageQuery): List<Faq>
	fun getTop3Faq(): List<Faq>
	fun getFaqById(faqId: Int): Faq?
	fun findNextId(faqId: Int): Faq?
	fun findPreviousId(faqId: Int): Faq?
	fun getCountAll(): Long
	fun getCountFilter(@Param("dataGridDTO") query: PageQuery): Long
}
