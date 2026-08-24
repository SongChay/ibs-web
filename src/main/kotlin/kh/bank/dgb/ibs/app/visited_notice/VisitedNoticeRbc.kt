package kh.bank.dgb.ibs.app.visited_notice

import org.apache.ibatis.annotations.Mapper

/** Port of `VisitNoticeDTO`. Renamed from the old class's "Notice" naming to "VisitedNotice" —
 *  this table (`visited_notify`) tracks whether a user has dismissed a notice popup, it has
 *  nothing to do with notice/announcement *content* (that's `NewsEventRbc`, confusingly, in the
 *  old app too). */
data class VisitedNotice(
	val userId: String,
	val visitYn: String? = null,
	val visitedDate: String? = null,
	val visitedTime: String? = null,
)

/** Port of `NoticeDAO` (bizmob.corpbanking.dao) — despite the old name, this is purely
 *  "has this user dismissed today's notice popup" bookkeeping. Date/time stay as
 *  `TO_CHAR`-formatted strings (`YYYYMMDD`/`HH24MISS000`) in the mapper XML, matching the old
 *  app's own storage format exactly rather than switching to real DATE/TIME columns. */
@Mapper
interface VisitedNoticeRbc {
	fun registerVisitedNotice(userId: String): Int
	fun retrieveVisitedNotice(userId: String): VisitedNotice?
	fun updateVisitedNotice(userId: String): Int
}
