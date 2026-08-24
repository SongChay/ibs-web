package kh.bank.dgb.ibs.app.local.visited_notice

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class VisitedNoticeUserRequest(
	val userID: String,
)

data class VisitedNoticeStatus(
	val userID: String,
	val visitYN: String?,
	val visitedDate: String?,
	val visitedTime: String?,
)

/** Port of `ADS2002_Adapter_RegisterVisitedNotice` + `ADS2003_Adapter_RetrieveVisitedNotice` —
 *  two closely-coupled operations on the same entity (has this user dismissed today's popup),
 *  kept as one feature rather than two. */
@RestController
@RequestMapping("/api/visited-notice")
class VisitedNoticeCbc(
	private val visitedNoticeSbc: VisitedNoticeSbc,
) {

	@PostMapping("/register")
	fun register(@RequestBody request: RequestData<VisitedNoticeUserRequest>): ResponseData<Unit> {
		visitedNoticeSbc.register(request.body!!.userID)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS))
	}

	@PostMapping("/retrieve")
	fun retrieve(@RequestBody request: RequestData<VisitedNoticeUserRequest>): ResponseData<VisitedNoticeStatus?> {
		val status = visitedNoticeSbc.retrieve(request.body!!.userID)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = status)
	}
}
