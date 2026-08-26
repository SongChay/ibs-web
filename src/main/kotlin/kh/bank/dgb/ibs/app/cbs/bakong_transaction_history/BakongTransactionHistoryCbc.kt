package kh.bank.dgb.ibs.app.cbs.bakong_transaction_history

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class BakongTransactionHistoryRequest(
	val pageNumber: Long? = null,
	val pageSize: Long? = null,
	val accountNo: String? = null,
	val startDate: String? = null,
	val endDate: String? = null,
	val userID: String? = null,
)

data class BakongTransactionHistoryItem(
	val withdrawalAmount: BigDecimal? = null,
	val remark: String? = null,
	val slipNo: String? = null,
	val transactionDate: String? = null,
	val transactionTime: String? = null,
	val afterBalance: BigDecimal? = null,
	val depositAmount: BigDecimal? = null,
	val transactionReasonDesc: String? = null,
	val transactionAmount: BigDecimal? = null,
	val transactionDesc: String? = null,
	// NOTE: `transactoinDesc` (sic) is a duplicate of `transactionDesc` in the old
	// `TRS0913_RES_InquiryBakongTransactionHistoryVo` — a typo'd leftover field, kept here for
	// wire fidelity in case CBS actually populates it separately from `transactionDesc`.
	val transactoinDesc: String? = null,
	val accountNo: String? = null,
	val accountName: String? = null,
	val transactionChannelTypeCode: String? = null,
	val depositTransactionTypeCode: String? = null,
	val transactionSeqNo: BigDecimal? = null,
	val detailTransactionSeqNo: BigDecimal? = null,
	val transactionChannelType: String? = null,
)

/** Port of `TRS0913_Adapter_InquiryBakongTransactionHistory` — calls CBS opcode `CIB11300913`
 *  (via the old `DGBEBankingService.processCIB11300913`). Straight pass-through. The old
 *  `TRS0913_RES_WrapperInquiryBakongTransactionHistoryVo` already used `items` as both its wire-in
 *  (`@JsonSetter`) and wire-out (`@JsonGetter`) key, so no renaming/aliasing is needed here. */
@RestController
@RequestMapping("/TRS0913")
class BakongTransactionHistoryCbc(
	private val bakongTransactionHistorySbc: BakongTransactionHistorySbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<BakongTransactionHistoryRequest>): ResponseData<BakongTransactionHistoryResponse> {
		return bakongTransactionHistorySbc.inquire(request)
	}
}

data class BakongTransactionHistoryResponse(
	val items: List<BakongTransactionHistoryItem>? = null,
)
