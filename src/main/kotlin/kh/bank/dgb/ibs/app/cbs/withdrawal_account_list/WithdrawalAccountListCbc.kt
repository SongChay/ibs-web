package kh.bank.dgb.ibs.app.cbs.withdrawal_account_list

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class WithdrawalAccountListRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val currencyCode: String? = null,
)

data class WithdrawalAccountItem(
	val accountNo: String? = null,
	val accountName: String? = null,
	val accountNickName: String? = null,
	val balance: BigDecimal? = null,
	val accountBalance: BigDecimal? = null,
	val freezeAmount: BigDecimal? = null,
	val currencyCode: String? = null,
	val depositSubjectCode: String? = null,
	val salaryAccountYN: String? = null,
	val withdrawalDeleteYn: String? = null,
	val inquiryDeleteYn: String? = null,
	val payrollCorporateFeeAmount: String? = null,
	val payrollCorporate: Boolean? = null,
)

/** Port of `TRS1001_Adapter_InquiryWithdrawalAccountList` — calls CBS opcode `CIB11300812`
 *  (via the old `DGBEBankingService.processACC0005`). Straight pass-through.
 *
 *  The old `TRS1001_RES_WrapperWithdrawalAccountNoListVo` exposed this list under `accountList` to
 *  its own client (`@JsonGetter`) but actually read it off the wire from CBS under `grid01`
 *  (`@JsonSetter`) — same dual-key trick the whole "grid01/grid02" CBS convention uses. `@JsonAlias`
 *  on the constructor parameter replicates that: still deserializes `grid01` from CBS, still
 *  serializes back out as `accountList` for our own client. */
@RestController
@RequestMapping("/TRS1001")
class WithdrawalAccountListCbc(
	private val withdrawalAccountListSbc: WithdrawalAccountListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<WithdrawalAccountListRequest>): ResponseData<WithdrawalAccountListResponse> {
		return withdrawalAccountListSbc.inquire(request)
	}
}

data class WithdrawalAccountListResponse(
	@param:JsonAlias("grid01")
	val accountList: List<WithdrawalAccountItem>? = null,
)
