package kh.bank.dgb.ibs.app.cbs.approval_by_final_approver

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ApprovalByFinalApproverRequest(
	val userID: String? = null,
	val approvalNo: Long = 0,
	val verifyCode: Int = 0,
	val overrideScheduleTransferTime: String? = null,
)

data class WingTransferResult(
	val refund: Boolean = false,
	val receiverName: String? = null,
	val receiverAccount: String? = null,
	val receiverPhone: String? = null,
	val code: String? = null,
	val resultMessage: String? = null,
)

data class AccountTransferResult(
	val cancelTransactionYN: String? = null,
	val errorMsgContent: String? = null,
)

/** Port of `APV2105_RES_ApprovalByFinalApproverVo` — no wire renames here (unlike the sibling
 *  `APV2104` response, these two fields are NOT wrapped as `grid01`/`grid02`). */
data class ApprovalByFinalApproverResponse(
	val wingTransferResult: List<WingTransferResult>? = null,
	val accountTransferResult: List<AccountTransferResult>? = null,
)

/** Port of `APV2105_Adapter_ApprovalByFinalApprover` — calls CBS opcode `CIB11001022` (via the old
 *  `DGBEBankingService.processAPR0024`, the same opcode `APV2104_Adapter_RejectByFinalApprover`
 *  calls, here always with a hardcoded "approved" status).
 *
 *  Genuinely complex orchestration beyond a single pass-through call, replicated faithfully in the
 *  Sbc:
 *   1. Reads the "final approver blocking time" service-status window via the local
 *      `ServiceStatusRbc` (port of `ServiceStatusService.getBlockingTime()` — that business logic
 *      never had its own Cbc/Sbc in the old app, so it's ported inline here) to decide whether
 *      final-approval is currently allowed at all.
 *   2. If allowed, verifies the caller's OTP: checks whether OTP creation is required
 *      (opcode `CIB11000214`, old `processUSR0103`) and, if not, verifies the supplied code
 *      (opcode `CIB11000211`, old `processSEC0004`).
 *   3. Only if OTP verification succeeds, calls the final-approver action opcode `CIB11001022`
 *      with a hardcoded `approvalStatusCode = "01"` (Approved) — note this call uses the
 *      **header's** `userID`, whereas the OTP-verification calls above use the **body's**
 *      `userID`, exactly as the old adapter did.
 *  See the final report for why the old code's `catch (Exception e) { /* swallow */ }` safety net
 *  around this flow was deliberately NOT replicated. */
@RestController
@RequestMapping("/APV2105")
class ApprovalByFinalApproverCbc(
	private val approvalByFinalApproverSbc: ApprovalByFinalApproverSbc,
) {
	@PostMapping
	fun approve(@RequestBody request: RequestData<ApprovalByFinalApproverRequest>): ResponseData<ApprovalByFinalApproverResponse> {
		return approvalByFinalApproverSbc.approve(request)
	}
}
