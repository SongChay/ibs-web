package kh.bank.dgb.ibs.app.cbs.multi_transfer_list

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class MultiTransferListRequest(
	val userID: String? = null,
	val accountNo: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	val inquireBy: String? = null,
	val sortBy: String? = null,
	val currentPage: Int? = null,
	val pageSize: Int? = null,
)

data class MultiTransferListItem(
	val withdrawalAccountNo: String? = null,
	val approvalRequestDate: String? = null,
	val approvalRequestTime: String? = null,
	val approvalRequestDateTime: String? = null,
	val scheduleTransferDate: String? = null,
	val scheduleTransactionTime: String? = null,
	val scheduleTransferDateTime: String? = null,
	val totalAmount: BigDecimal? = null,
	val totalFeeAmount: BigDecimal? = null,
	val transactionCurrencyCode: String? = null,
	val totalCount: Int? = null,
	val transferSuccessCount: Int? = null,
	val transferProcessedCount: Int? = null,
	val transferSuccessAmount: BigDecimal? = null,
	val transferFailCount: Int? = null,
	val transferFailAmount: BigDecimal? = null,
	val approvalResultDescription: String? = null,
	val approvalNo: Long? = null,
	val withdrawalAccountRemark: String? = null,
	val receiverAccountRemark: String? = null,
)

data class MultiTransferListResponse(
	@JsonAlias("grid01Count") val totalCount: Long? = null,
	@JsonAlias("grid01") val approvalTransferList: List<MultiTransferListItem>? = null,
)

/** Port of `TRS3101_Adapter_InquiryMultiTransferList` — calls CBS opcode `CIB11001411` (via the old
 *  `DGBEBankingService.processTRN0042`). */
@RestController
@RequestMapping("/TRS3101")
class MultiTransferListCbc(
	private val sbc: MultiTransferListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<MultiTransferListRequest>): ResponseData<MultiTransferListResponse> =
		sbc.inquire(request)
}
