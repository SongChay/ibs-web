package kh.bank.dgb.ibs.app.cbs.wing_transfer_final_approval

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class WingTransferListItem(
	val amount: BigDecimal? = null,
	val recipientName: String? = null,
	val toAccountNumber: String? = null,
	val receiverPhoneNo: String? = null,
	val receiverCountryCode: String? = null,
	val wingTransferTypeCode: String? = null,
	val receiverAccountRemark: String? = null,
	val withdrawalAccountRemark: String? = null,
)

data class WingTransferApprovalLineItem(
	val approverTypeCode: String? = null,
	val approverID: String? = null,
)

data class VerifyQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int? = null,
	val firstYn: String? = null,
)

/** Same request shape as `wing_transfer_registration.WingTransferRegistrationRequest` — the old
 *  adapter (`TRS5102_Adapter_ExecuteWingTransferForFinalApprover`) takes the exact same
 *  `TRS5101_REQ_RegisterWingTransferVo` as TRS5101. Duplicated here per the one-folder-is-
 *  self-contained convention; see that sibling file's doc comment for the same
 *  `transferList`/`approvalList`-to-`grid01`/`grid02` caveat, replicated identically below. */
data class WingTransferFinalApprovalRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val transferTypeCode: String? = null,
	val withdrawalAccountNo: String? = null,
	val currencyCode: String? = null,
	val previousApprovalNo: Long? = null,
	@param:JsonProperty("transferList") @get:JsonProperty("grid01")
	val transferList: List<WingTransferListItem>? = null,
	@param:JsonProperty("approvalList") @get:JsonProperty("grid02")
	val approvalList: List<WingTransferApprovalLineItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

data class WingTransferResultItem(
	val isRefund: Boolean? = null,
	val receiverName: String? = null,
	val receiverAccount: String? = null,
	val receiverPhone: String? = null,
	val code: String? = null,
	val resultMessage: String? = null,
)

/** Port of `TRS5102_RES_RegisterWingTransferVo` — unlike the CBS-facing `grid01`-tagged shape
 *  used internally to talk to CBS, this outward response is built by hand in the old adapter
 *  (`body.setWingTransferResult(...)`) and carries no CBS wire-rename annotation, so the field
 *  is plainly named here too. */
data class WingTransferFinalApprovalResponse(
	val wingTransferResult: List<WingTransferResultItem>? = null,
)

/**
 * Port of `TRS5102_Adapter_ExecuteWingTransferForFinalApprover` — NOT a single pass-through call.
 * Same real multi-step business logic as `oversea_transfer_final_approval` (see that Cbc's doc
 * comment for the general shape), applied to Wing transfers instead:
 *
 *  1. Same final-approver blocking-time service-status check (opcode-free, DB-backed via
 *     `ServiceStatusRbc`).
 *  2. Same OTP verification flow (`CIB11000214` then `CIB11000211`).
 *  3. Only if OTP verification says `verifyYn == "Y"`, registers the Wing transfer (opcode
 *     `CIB11001921`, old `processAPR0040` — same opcode as plain `TRS5101`), force-setting
 *     `receiverCountryCode = "KHM"` on every transfer-list item first, exactly like `TRS5101`.
 *  4. Unlike TRS4102 (which discards the transfer response body entirely), this one DOES copy
 *     `wingTransferResult` from the registration call into its own response body on the success
 *     path — see `WingTransferFinalApprovalSbc`.
 *
 * Flagged prominently per instructions: this is one of the two most complex adapters in the batch.
 */
@RestController
@RequestMapping("/TRS5102")
class WingTransferFinalApprovalCbc(
	private val sbc: WingTransferFinalApprovalSbc,
) {
	@PostMapping
	fun execute(@RequestBody request: RequestData<WingTransferFinalApprovalRequest>): ResponseData<WingTransferFinalApprovalResponse> =
		sbc.execute(request)
}
