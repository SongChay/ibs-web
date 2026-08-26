package kh.bank.dgb.ibs.app.cbs.account_group_option_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AccountGroupOptionListRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
)

/** Port of `ACI1001_RES_AccountGroupOptionListVo`. */
data class AccountGroupOptionListItem(
	val frequentAccountGroupNo: Long? = null,
	val frequentAccountGroupName: String? = null,
)

/** Port of `ACI1001_RES_WrapperAccountGroupOptionListVo`. */
data class AccountGroupOptionListResponse(
	val frequentAccountGroupList: List<AccountGroupOptionListItem>? = null,
)

/** Port of `ACI1001_Adapter_AccountGroupOptionList` — calls CBS opcode `CIB11002712` (via the old
 *  `DGBEBankingService.processMGR0008`). Plain pass-through. */
@RestController
@RequestMapping("/ACI1001")
class AccountGroupOptionListCbc(
	private val accountGroupOptionListSbc: AccountGroupOptionListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<AccountGroupOptionListRequest>): ResponseData<AccountGroupOptionListResponse> {
		return accountGroupOptionListSbc.inquire(request)
	}
}
