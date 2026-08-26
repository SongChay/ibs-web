package kh.bank.dgb.ibs.app.cbs.oversea_transfer_final_approval

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class OverseaTransferApprovalItem(
	val approverID: String? = null,
	val approverTypeCode: String? = null,
)

data class OverseaTransferFileItem(
	val filePath: String? = null,
	val fileName: String? = null,
	val originalFileName: String? = null,
	val fileExtension: String? = null,
)

data class VerifyQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int? = null,
	val firstYn: String? = null,
)

/**
 * Same request shape as `oversea_transfer_registration.OverseaTransferRegistrationRequest` — the
 * old adapter (`TRS4102_Adapter_RegisterOverseaTransferByFinalApprover`) takes the exact same
 * `TRS4101_REQ_RegisterOverseaTransferVo` as TRS4101. Duplicated here rather than shared across
 * feature folders per the one-folder-is-self-contained convention — see that sibling file's doc
 * comment for the `approvalList`/`fileList`-to-`grid01`/`grid02` and `purposeTransfer` caveats,
 * replicated identically below.
 */
data class OverseaTransferFinalApprovalRequest(
	val customerNo: String? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val transferTypeCode: String? = null,
	val withdrawalAccountNo: String? = null,
	val currencyCode: String? = null,
	val senderAddress: String? = null,
	val senderPhoneNo: String? = null,
	val senderCardID: String? = null,
	val receiverBankAddress: String? = null,
	val receiverBankSwiftCode: String? = null,
	val receiverBankPostalCode: String? = null,
	val receiverBankTownName: String? = null,
	val overseaReceiverAccountNo: String? = null,
	val counterpartBankAccountNumber: String? = null,
	val receiverAddress: String? = null,
	val receiverName: String? = null,
	val receiverPostalCode: String? = null,
	val receiverTownName: String? = null,
	val receiverRemark: String? = null,
	val amount: BigDecimal? = null,
	val overseaCommissionTypeCode: String? = null,
	@get:JsonIgnore
	val purposeTransfer: String? = null,
	val invoiceNo: String? = null,
	val purposeCode: String? = null,
	val remark: String? = null,
	val overseaReceiverBankName: String? = null,
	val overseaReceiverBankCountryID: String? = null,
	val approvalMemo: String? = null,
	@param:JsonProperty("approvalList") @get:JsonProperty("grid01")
	val approvalList: List<OverseaTransferApprovalItem>? = null,
	@param:JsonProperty("fileList") @get:JsonProperty("grid02")
	val fileList: List<OverseaTransferFileItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

/** The old `TRS4102_RES_RegisterOverseaTransferVo` has NO fields at all (`@JsonIgnoreProperties`
 *  over an otherwise-empty class) — the adapter always returns an empty body object and signals
 *  success/failure purely through the response header. Modeled here the same way. */
class OverseaTransferFinalApprovalResponse

/**
 * Port of `TRS4102_Adapter_RegisterOverseaTransferByFinalApprover` — NOT a single pass-through
 * call. Real multi-step business logic, replicated faithfully in `OverseaTransferFinalApprovalSbc`:
 *
 *  1. Look up the "final approver blocking time" service status window (old `ServiceStatusService
 *     .getBlockingTime()`, reused here via the existing `ServiceStatusRbc` DAO plus the same
 *     time-window math the old `ServiceStatusServiceImpl` did — that math wasn't ported into any
 *     shared service yet, so it's replicated locally in `OverseaTransferFinalApprovalSbc`).
 *  2. If the status row is missing, or the current time is outside the allowed window, short-
 *     circuit with the corresponding failure header and an empty body — no CBS calls made.
 *  3. Otherwise verify OTP: check whether OTP creation is required (opcode `CIB11000214`, old
 *     `processUSR0103`), and if not, verify the QR/OTP code (opcode `CIB11000211`, old
 *     `processSEC0004`, forcing `firstYn = "N"` first exactly like the old code).
 *  4. Only if that verification says `verifyYn == "Y"` does it actually register the transfer
 *     (opcode `CIB11301721`, old `processAPR0030` — same opcode as plain `TRS4101`). The transfer
 *     response body is discarded either way; only its header is propagated.
 *
 * Flagged prominently per instructions: this is one of the two most complex adapters in the batch.
 */
@RestController
@RequestMapping("/TRS4102")
class OverseaTransferFinalApprovalCbc(
	private val overseaTransferFinalApprovalSbc: OverseaTransferFinalApprovalSbc,
) {
	@PostMapping
	fun execute(@RequestBody request: RequestData<OverseaTransferFinalApprovalRequest>): ResponseData<OverseaTransferFinalApprovalResponse> {
		return overseaTransferFinalApprovalSbc.execute(request)
	}
}
