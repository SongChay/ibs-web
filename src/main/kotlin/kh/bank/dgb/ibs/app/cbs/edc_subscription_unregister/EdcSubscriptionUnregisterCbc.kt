package kh.bank.dgb.ibs.app.cbs.edc_subscription_unregister

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class ListTransferItem(
	val amount: BigDecimal? = null,
	val toAccountNumber: String? = null,
	val bankCode: String? = null,
	val recipientName: String? = null,
	val receiverAccountRemark: String? = null,
	val transactionTypeCode: String? = null,
	val withdrawalAccountRemark: String? = null,
)

data class ApprovalLineItem(
	val approverTypeCode: String? = null,
	val approverID: String? = null,
)

// TODO: see the identical note in the sibling `edc_subscription_register` folder — these OTP VOs
// (CBS opcodes CIB11000214 / CIB11000211) are duplicated here rather than shared, to keep each
// feature folder self-contained per the two-file convention.
data class VerifyQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int? = null,
	val firstYn: String? = null,
)

data class VerifyQrCodeResponse(
	val verifyYn: String? = null,
	val otpErrorCount: Int? = null,
)

data class OtpCreateRequiredRequest(
	val userID: String? = null,
)

data class OtpCreateRequiredResponse(
	@JsonProperty("oTPCreateRequiredYN") val otpCreateRequiredYn: String? = null,
)

// TODO: see the identical note in the sibling `edc_subscription_register` folder re: the
// grid01/grid02 vs transferList/approvalList asymmetric CBS wire naming.
data class EdcSubscriptionUnregisterRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val transferTypeCode: String? = null,
	val withdrawalAccountNo: String? = null,
	val currencyCode: String? = null,
	val memo: String? = null,
	val scheduleDate: String? = null,
	val scheduleTime: String? = null,
	val previousApprovalNo: Long? = null,
	@JsonProperty("grid01") @JsonAlias("transferList") val transferList: List<ListTransferItem>? = null,
	@JsonProperty("grid02") @JsonAlias("approvalList") val approvalList: List<ApprovalLineItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

data class EdcSubscriptionUnregisterResponse(
	val approvalNo: Long? = null,
	val success: Boolean? = null,
)

/** Port of `TRS2541_Adapter_RegisterUnSubscriptionEDC` — calls CBS opcode `CIB11102541` (via the
 *  old `DGBEBankingService.processCIB11102541`), preceded by an OTP verification flow (CBS opcodes
 *  `CIB11000214` and `CIB11000211`) when the request carries a `verifyQRCodeVo`. See
 *  [EdcSubscriptionUnregisterSbc] for the full logic — this is NOT a single pass-through call. */
@RestController
@RequestMapping("/TRS2541")
class EdcSubscriptionUnregisterCbc(
	private val sbc: EdcSubscriptionUnregisterSbc,
) {
	@PostMapping
	fun unregister(@RequestBody request: RequestData<EdcSubscriptionUnregisterRequest>): ResponseData<EdcSubscriptionUnregisterResponse> =
		sbc.unregister(request)
}
