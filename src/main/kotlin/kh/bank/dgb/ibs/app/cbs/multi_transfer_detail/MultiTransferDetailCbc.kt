package kh.bank.dgb.ibs.app.cbs.multi_transfer_detail

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class MultiTransferDetailRequest(
	val userID: String? = null,
	val approvalNo: Long? = null,
)

data class MultiTransferDetailItem(
	// 001 : Processing, 002 : Failed, 003 : Completed, 000 : unknown
	val transactionStatus: String? = null,
	val transactionStatusDesc: String? = null,
	val receiverBankCode: String? = null,
	val receiverBankName: String? = null,
	val receiverAccountNo: String? = null,
	val receiverName: String? = null,
	val transactionAmount: BigDecimal? = null,
	val transactionCurrencyCode: String? = null,
	val receiverAccountRemark: String? = null,
	val transactionErrorDetailCode: String? = null,
	val transactionErrorDetailCodeDesc: String? = null,
	val withdrawalAccountRemark: String? = null,
)

data class MultiTransferDetailResponse(
	@JsonAlias("grid01Count") val totalCount: Int? = null,
	@JsonAlias("grid01") val transferList: List<MultiTransferDetailItem>? = null,
)

/** Port of `TRS3201_Adapter_InquiryMultiTransferDetail` — calls CBS opcode `CIB11001501` (via the
 *  old `DGBEBankingService.processTRN0043`). */
@RestController
@RequestMapping("/TRS3201")
class MultiTransferDetailCbc(
	private val multiTransferDetailSbc: MultiTransferDetailSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<MultiTransferDetailRequest>): ResponseData<MultiTransferDetailResponse> {
		return multiTransferDetailSbc.inquire(request)
	}
}
