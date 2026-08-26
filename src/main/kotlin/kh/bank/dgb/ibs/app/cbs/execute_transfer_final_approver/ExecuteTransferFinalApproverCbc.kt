package kh.bank.dgb.ibs.app.cbs.execute_transfer_final_approver

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class TransferListItem(
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

/** Port of the old `USR2002_REQ_VerifyQRCodeVo`, as nested inside the shared
 *  `TRS1102_REQ_RegisterTransferVo` request shape — carries the OTP the final approver entered. */
data class VerifyQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int? = null,
	val firstYn: String? = null,
)

/** Same request shape as `TRS1102_Adapter_Transfer`'s (the old code literally reuses
 *  `TRS1102_REQ_RegisterTransferVo` for both adapters) plus `verifyQRCodeVo`, which only this
 *  adapter reads. See `TransferRequest` in the sibling `transfer` feature for the plain-transfer
 *  version without the OTP field. */
data class ExecuteTransferFinalApproverRequest(
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
	@param:JsonProperty("transferList") @get:JsonProperty("grid01")
	val transferList: List<TransferListItem>? = null,
	@param:JsonProperty("approvalList") @get:JsonProperty("grid02")
	val approvalList: List<ApprovalLineItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

data class AccountTransferResultItem(
	val cancelTransactionYN: String? = null,
	val errorMsgContent: String? = null,
)

data class ExecuteTransferFinalApproverResponse(
	val accountTransferResult: List<AccountTransferResultItem>? = null,
)

/** Port of `TRS1104_Adapter_ExecuteTransferForFinalApper` — calls CBS opcode `CIB11001021`
 *  (via the old `DGBEBankingService.processAPR0001`), same as `transfer`, but ONLY as the last step
 *  of a genuinely multi-step flow, not a pass-through:
 *
 *   1. Look up the corporate-banking service status (`serviceStatusTypeCode = "11"`) and check
 *      whether final-approver execution is currently allowed (time-window + status-code rules).
 *   2. If allowed: verify the approver's OTP (`CIB11000214` to check whether OTP verification is
 *      even required, then `CIB11000211` to actually verify it).
 *   3. Only if OTP verification succeeds: register the transfer (`CIB11001021`), enforcing the
 *      `TRANSFER_MAX_LIMIT` (1000 items) cap and clearing the schedule for immediate transfers,
 *      exactly like `transfer`.
 *
 *  This is the highest-stakes adapter in this batch (it's the one that actually moves money on
 *  final approval) — see `ExecuteTransferFinalApproverSbc` for the full port and flagged
 *  simplifications, and give this extra review. */
@RestController
@RequestMapping("/TRS1104")
class ExecuteTransferFinalApproverCbc(
	private val executeTransferFinalApproverSbc: ExecuteTransferFinalApproverSbc,
) {
	@PostMapping
	fun execute(
		@RequestBody request: RequestData<ExecuteTransferFinalApproverRequest>,
	): ResponseData<ExecuteTransferFinalApproverResponse> {
		return executeTransferFinalApproverSbc.execute(request)
	}
}
