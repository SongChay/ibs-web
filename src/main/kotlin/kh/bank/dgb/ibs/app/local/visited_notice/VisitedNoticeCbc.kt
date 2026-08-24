package kh.bank.dgb.ibs.app.local.visited_notice

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

/** Bundles two unrelated old adapters — `ADS2002_Adapter_RegisterVisitedNotice` and
 *  `ADS2003_Adapter_RetrieveVisitedNotice` — operating on the same entity (has this user
 *  dismissed today's popup), kept as one feature rather than two. Each method carries its own
 *  absolute route matching its old adapter (no class-level `@RequestMapping`, since the two
 *  routes share no common prefix). */
@RestController
class VisitedNoticeCbc(
	private val visitedNoticeSbc: VisitedNoticeSbc,
) {

	/** Port of `ADS2002_Adapter_RegisterVisitedNotice`. */
	@PostMapping("/ADS2002")
	fun register(@RequestBody request: RequestData<VisitedNoticeUserRequest>): ResponseData<Unit> {
		visitedNoticeSbc.register(request.body!!.userID)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS))
	}

	/** Port of `ADS2003_Adapter_RetrieveVisitedNotice`. */
	@PostMapping("/ADS2003")
	fun retrieve(@RequestBody request: RequestData<VisitedNoticeUserRequest>): ResponseData<VisitedNoticeStatus?> {
		val status = visitedNoticeSbc.retrieve(request.body!!.userID)
		return ResponseData(header = ResponseResultUtils.makeResponse(true, ResponseResultCodeType.SUCCESS), body = status)
	}
}
