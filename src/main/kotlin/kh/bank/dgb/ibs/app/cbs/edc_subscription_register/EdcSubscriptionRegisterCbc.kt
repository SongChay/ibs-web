package kh.bank.dgb.ibs.app.cbs.edc_subscription_register

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

// TODO: the old `USR2002_REQ_VerifyQRCodeVo`/`USR2002_RES_VerifyQRCodeVo` (CBS opcode CIB11000211)
// and `USR0103_REQ/RES_OTPCreateRequiredVo` (CBS opcode CIB11000214) are shared VOs used by many
// old adapters beyond this batch. They're redefined locally here (and duplicated identically in
// the sibling `edc_subscription_unregister` folder) since each feature folder must stay
// self-contained per the two-file convention — flagged in case a shared `common` type would be
// preferred instead once more OTP-verifying adapters are ported.
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

// CBS returns this field under the oddly-cased key "oTPCreateRequiredYN" (verified against the old
// `USR0103_RES_OTPCreateRequiredVo` @JsonSetter) — this VO is only ever consumed internally by the
// OTP flow below, never re-serialized back out to a REST client, so a single @JsonProperty is
// enough (no asymmetric ser/deser naming needed here, unlike the request-side grid01/grid02 case).
data class OtpCreateRequiredResponse(
	@JsonProperty("oTPCreateRequiredYN") val otpCreateRequiredYn: String? = null,
)

// TODO: the old `TRS2521_REQ_RegisterSubscriptionEDC` serializes `transferList`/`approvalList` to
// CBS under the wire names "grid01"/"grid02" (@JsonGetter) but deserializes incoming requests
// under "transferList"/"approvalList" (@JsonSetter) — i.e. CBS's own field names differ from what
// our REST clients send. @JsonProperty below pins serialization (what gets POSTed to CBS) to the
// CBS name; @JsonAlias additionally accepts the client-facing name on deserialization. Flagged for
// review since this dual-name Jackson setup has no precedent elsewhere in the new project.
data class EdcSubscriptionRegisterRequest(
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

data class EdcSubscriptionRegisterResponse(
	val approvalNo: Long? = null,
	val success: Boolean? = null,
	val subscriptionDate: String? = null,
	val transactionUniqueID: String? = null,
	val templateName: String? = null,
	val accountName: String? = null,
	val accountNo: String? = null,
	val currencyCode: String? = null,
)

/** Port of `TRS2521_Adapter_RegisterSubscriptionEDC` — calls CBS opcode `CIB11102521` (via the old
 *  `DGBEBankingService.processCIB11102521`), preceded by an OTP verification flow (CBS opcodes
 *  `CIB11000214` and `CIB11000211`) when the request carries a `verifyQRCodeVo`. See
 *  [EdcSubscriptionRegisterSbc] for the full logic — this is NOT a single pass-through call. */
@RestController
@RequestMapping("/TRS2521")
class EdcSubscriptionRegisterCbc(
	private val edcSubscriptionRegisterSbc: EdcSubscriptionRegisterSbc,
) {
	@PostMapping
	fun register(@RequestBody request: RequestData<EdcSubscriptionRegisterRequest>): ResponseData<EdcSubscriptionRegisterResponse> {
		return edcSubscriptionRegisterSbc.register(request)
	}
}
