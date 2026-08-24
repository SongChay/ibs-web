package kh.bank.dgb.ibs.app.cbs.change_account_group

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ChangeAccountGroupRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val frequentAccountGroupName: String? = null,
	val changeFrequentAccountGroupNo: String? = null,
	val frequentAccountGroupNo: String? = null,
)

/** Old `INF1103_RES_ChangeAccountGroupVo` has no fields at all — CBS returns an empty body. */
class ChangeAccountGroupResponse

/** Port of `INF1103_Adapter_ChangeAccountGroup` — calls CBS opcode `CIB11002231` (via the old
 *  `DGBEBankingService.processMGR0006`). */
@RestController
@RequestMapping("/INF1103")
class ChangeAccountGroupCbc(
	private val sbc: ChangeAccountGroupSbc,
) {
	@PostMapping
	fun change(@RequestBody request: RequestData<ChangeAccountGroupRequest>): ResponseData<ChangeAccountGroupResponse> =
		sbc.change(request)
}
