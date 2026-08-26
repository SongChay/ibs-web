package kh.bank.dgb.ibs.app.cbs.reject_by_final_approver

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RejectByFinalApproverRequest(
	val userID: String? = null,
	val approvalStatusCode: String? = null,
	val approvalNo: Long = 0,
	val overrideScheduleTransferTime: String? = null,
)

data class WingTransferResult(
	@JsonProperty("isRefund") val refund: Boolean = false,
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

/** Port of `APV2104_RES_ApprovalByFinalApproverVo` — `wingTransferResult`/`accountTransferResult`
 *  are wire-named `grid01`/`grid02` (field-level `@JsonProperty` in the old Vo, so symmetric in
 *  both directions). */
data class RejectByFinalApproverResponse(
	val resultYn: String? = null,
	val startTime: String? = null,
	val endTime: String? = null,
	@JsonProperty("grid01") val wingTransferResult: List<WingTransferResult>? = null,
	@JsonProperty("grid02") val accountTransferResult: List<AccountTransferResult>? = null,
)

/** Port of `APV2104_Adapter_RejectByFinalApprover` — calls CBS opcode `CIB11001022` (via the old
 *  `DGBEBankingService.processAPR0024`). Despite the adapter's name, this opcode is a generic
 *  "final-approver action" call whose effect (approve vs reject) is driven entirely by the
 *  `approvalStatusCode` the caller supplies — see `APV2105_Adapter_ApprovalByFinalApprover`, which
 *  calls the very same opcode with a hardcoded "approved" status. */
@RestController
@RequestMapping("/APV2104")
class RejectByFinalApproverCbc(
	private val rejectByFinalApproverSbc: RejectByFinalApproverSbc,
) {
	@PostMapping
	fun reject(@RequestBody request: RequestData<RejectByFinalApproverRequest>): ResponseData<RejectByFinalApproverResponse> {
		return rejectByFinalApproverSbc.reject(request)
	}
}
