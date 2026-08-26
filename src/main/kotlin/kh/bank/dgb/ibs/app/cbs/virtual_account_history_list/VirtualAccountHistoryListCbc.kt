package kh.bank.dgb.ibs.app.cbs.virtual_account_history_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class VirtualAccountHistoryListRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val virtualAccountNo: String? = null,
	val parentAccountNo: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	val searchBy: String? = null,
	val searchKeyword: String? = null,
)

data class VirtualAccountHistoryItem(
	val virtualAccountNo: String? = null,
	val parentAccountNo: String? = null,
	val paymentName: String? = null,
	val customerUniqueNo: String? = null,
	val customerDescription: String? = null,
	val virtualAccountReceiveTypeCode: String? = null,
	val virtualAccountReceiveTypeName: String? = null,
	val currencyCode: String? = null,
	val paymentInfoDate: String? = null,
	val totalAmount: BigDecimal? = null,
	val depositStartDate: String? = null,
	val depositEndDate1: String? = null,
	val registerAmount1: BigDecimal? = null,
	val depositEndDate2: String? = null,
	val registerAmount2: BigDecimal? = null,
	val accountExpiryDate: String? = null,
	val virtualAccountReceiveStatusCode: String? = null,
	val virtualAccountReceiveStatusDescription: String? = null,
)

data class VirtualAccountHistoryListResponse(
	val totalCount: Long? = null,
	val virtualAccountPaymentHistoryList: List<VirtualAccountHistoryItem>? = null,
)

/** Port of `VAC3001_Adapter_InquiryVirtualAccountHistoryList` — calls CBS opcode `CIB11302111`. */
@RestController
@RequestMapping("/VAC3001")
class VirtualAccountHistoryListCbc(
	private val virtualAccountHistoryListSbc: VirtualAccountHistoryListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<VirtualAccountHistoryListRequest>): ResponseData<VirtualAccountHistoryListResponse> {
		return virtualAccountHistoryListSbc.inquire(request)
	}
}
