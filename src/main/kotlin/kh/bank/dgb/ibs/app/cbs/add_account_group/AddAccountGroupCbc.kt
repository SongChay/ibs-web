package kh.bank.dgb.ibs.app.cbs.add_account_group

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AddAccountGroupRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val frequentAccountGroupName: String? = null,
)

data class AddAccountGroupResponse(
	val frequentAccountGroupNo: Long? = null,
	val frequentAccountGroupName: String? = null,
)

/** Port of `INF1101_Adapter_AddAccountGroup` — calls CBS opcode `CIB11002221` (via the old
 *  `DGBEBankingService.processMGR0005`). */
@RestController
@RequestMapping("/INF1101")
class AddAccountGroupCbc(
	private val sbc: AddAccountGroupSbc,
) {
	@PostMapping
	fun add(@RequestBody request: RequestData<AddAccountGroupRequest>): ResponseData<AddAccountGroupResponse> =
		sbc.add(request)
}
