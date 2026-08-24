package kh.bank.dgb.ibs.app.cbs.recent_transfer_account_list

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RecentTransferAccountListRequest(
	val customerNo: String? = null,
	val countMonth: Int? = null,
	val maxCount: Int? = null,
	val ebankTransactionTypeCode: String? = null,
)

// TODO: the old `TRS1201_RES_RecentTransferAccountListVo` deserializes these three fields from CBS
// under different wire names than it serializes them back to the client under (asymmetric
// @JsonGetter/@JsonSetter). @JsonAlias below accepts both the CBS name and the client-facing name
// on the way in; serialization still emits the client-facing (plain) property name, matching the
// old @JsonGetter behavior. Flagged for review since this dual-name mapping has no precedent
// elsewhere in the new project's convention.
data class RecentTransferAccountListItem(
	val transactionDate: String? = null,
	@JsonAlias("opponentAccNumber") val opponentAccountNo: String? = null,
	@JsonAlias("opponentAccName") val opponentAccountName: String? = null,
	@JsonAlias("opponentCustID") val opponentCustomerNo: String? = null,
	val opponentUserID: String? = null,
	val opponentImageURL: String? = null,
	val opponentBankCode: String? = null,
	val opponentCurrencyCode: String? = null,
	val opponentBankName: String? = null,
	val phoneNumber: String? = null,
	val opponentBankShortName: String? = null,
)

data class RecentTransferAccountListResponse(
	@JsonAlias("grid01") val recentTransferFriendList: List<RecentTransferAccountListItem>? = null,
)

/** Port of `TRS1201_Adapter_RecentTransferAccountList` — calls CBS opcode `CIB11301111` (via the old
 *  `DGBEBankingService.processTRN0011`). */
@RestController
@RequestMapping("/TRS1201")
class RecentTransferAccountListCbc(
	private val sbc: RecentTransferAccountListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<RecentTransferAccountListRequest>): ResponseData<RecentTransferAccountListResponse> =
		sbc.inquire(request)
}
