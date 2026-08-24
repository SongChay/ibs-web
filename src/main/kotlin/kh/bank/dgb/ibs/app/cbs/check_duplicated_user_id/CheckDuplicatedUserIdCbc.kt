package kh.bank.dgb.ibs.app.cbs.check_duplicated_user_id

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CheckDuplicatedUserIdRequest(
	val userID: String? = null,
)

data class CheckDuplicatedUserIdResponse(
	val resultYn: String? = null,
)

/** Port of `INF2101_Adapter_CheckDuplicatedUserID` — calls CBS opcode `CIB11002411` (via the old
 *  `DGBEBankingService.processUSR0022`). Non-pass-through: the old adapter derives
 *  `body.resultYn` ("Y"/"N") from `header.result` after the CBS call; replicated in
 *  `CheckDuplicatedUserIdSbc`. */
@RestController
@RequestMapping("/INF2101")
class CheckDuplicatedUserIdCbc(
	private val sbc: CheckDuplicatedUserIdSbc,
) {
	@PostMapping
	fun check(@RequestBody request: RequestData<CheckDuplicatedUserIdRequest>): ResponseData<CheckDuplicatedUserIdResponse> =
		sbc.check(request)
}
