package kh.bank.dgb.ibs.app.cbs.frequently_used_account_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class FrequentlyUsedAccountListRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	val receiverAccountNickName: String? = null,
	val receiverAccountName: String? = null,
	val receiverBankName: String? = null,
	val receiverAccountNumber: String? = null,
	val frequentAccountGroupNo: String? = null,
	val sortColumn: String? = null,
	val sortDirection: String? = null,
	val currencyCode: String? = null,
	val pageSize: Int? = null,
	val currentPage: Int? = null,
	val ebankTransactionTypeCode: String? = null,
)

data class FrequentlyUsedAccountItem(
	val seqNo: Int? = null,
	val frequentAccountGroupNo: Long? = null,
	val frequentAccountGroupName: String? = null,
	val receiverBankCode: String? = null,
	val receiverBankName: String? = null,
	val receiverBankShortName: String? = null,
	val receiverAccountNumber: String? = null,
	val receiverAccountName: String? = null,
	val receiverAccountNickName: String? = null,
	val assignDate: String? = null,
	val currencyCode: String? = null,
)

data class FrequentlyUsedAccountListResponse(
	// Old Vo: getter serialized to the client as "frequentAccountTotal", setter bound from CBS as
	// "grid01Count" — asymmetric, replicated here via separate param/get wire names.
	@param:JsonProperty("grid01Count")
	@get:JsonProperty("frequentAccountTotal")
	val frequentAccountTotal: Long? = null,
	// Old Vo: getter serialized to the client as "frequentAccountList", setter bound from CBS as
	// "grid01" — asymmetric, replicated here via separate param/get wire names.
	@param:JsonProperty("grid01")
	@get:JsonProperty("frequentAccountList")
	val frequentAccountList: List<FrequentlyUsedAccountItem>? = null,
)

/** Port of `INF4001_Adapter_InquiryFrequentlyUsedAccountList` — calls CBS opcode `CIB11302711`
 *  (via the old `DGBEBankingService.processTRN0012`). */
@RestController
@RequestMapping("/INF4001")
class FrequentlyUsedAccountListCbc(
	private val frequentlyUsedAccountListSbc: FrequentlyUsedAccountListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<FrequentlyUsedAccountListRequest>): ResponseData<FrequentlyUsedAccountListResponse> {
		return frequentlyUsedAccountListSbc.inquire(request)
	}
}
