package kh.bank.dgb.ibs.app.local.visited_notice

import org.springframework.stereotype.Service

@Service
class VisitedNoticeSbc(
	private val visitedNoticeRbc: VisitedNoticeRbc,
) {

	fun register(userId: String) {
		visitedNoticeRbc.registerVisitedNotice(userId)
	}

	fun retrieve(userId: String): VisitedNoticeStatus? {
		val row = visitedNoticeRbc.retrieveVisitedNotice(userId) ?: return null
		return VisitedNoticeStatus(
			userID = row.userId,
			visitYN = row.visitYn,
			visitedDate = row.visitedDate,
			visitedTime = row.visitedTime,
		)
	}
}
