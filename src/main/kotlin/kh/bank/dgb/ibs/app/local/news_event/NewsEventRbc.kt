package kh.bank.dgb.ibs.app.local.news_event

import kh.bank.dgb.ibs.common.query.PageQuery
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

/** Port of `NewsEventDTO` — flattened: the old mapper nested `isDisplayed` as a whole
 *  `BbsBoardNewsEventDTO` sub-object (itself just `{boardId, isDisplayed}`); that added nothing
 *  a plain field doesn't, so it's `isBannerDisplayed` directly here. */
data class NewsEvent(
	val id: Int,
	val titleEn: String? = null,
	val titleKh: String? = null,
	val contentEn: String? = null,
	val contentKh: String? = null,
	val viewCount: Int? = null,
	val createdDate: LocalDateTime? = null,
	val categoryCode: String? = null,
	val categoryName: String? = null,
	val isBannerDisplayed: Int? = null,
)

/** Port of `NewsEventDAO` — `bbs_board` entries under category `004` (news/events). Also home to
 *  `getLatestNotice`/`getTop3Notice` in the old mapper, kept here despite the name mismatch —
 *  they query this same category, not a separate notice table. */
@Mapper
interface NewsEventRbc {
	fun getAll(@Param("dataGridDTO") query: PageQuery): List<NewsEvent>
	fun findById(id: Int): NewsEvent?
	fun findNextId(id: Int): NewsEvent?
	fun findPreviousId(id: Int): NewsEvent?
	fun getLatestNotice(): NewsEvent?
	fun getTop3Notice(): List<NewsEvent>
	fun updateViewCount(id: Int): Int
	fun getCountAll(): Long
	fun getCountFilter(@Param("dataGridDTO") query: PageQuery): Long
}
