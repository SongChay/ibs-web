package kh.bank.dgb.ibs.app.cbs.approval_status_statistic

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ApprovalStatusStatisticRequest(
	val userID: String? = null,
)

data class ApprovalStatusStatisticResponse(
	val requestWaiting: Long? = null,
	val requestNeedsMyApproval: Long? = null,
	val requestApproved: Long? = null,
)

/** Port of `MAN1009_Adapter_ApprovalStatusStatistic` — calls CBS opcode `CIB11300413`
 *  (via the old `DGBEBankingService.processAPR0101`). */
@RestController
@RequestMapping("/MAN1009")
class ApprovalStatusStatisticCbc(
	private val approvalStatusStatisticSbc: ApprovalStatusStatisticSbc,
) {
	@PostMapping
	fun inquire(
		@RequestBody request: RequestData<ApprovalStatusStatisticRequest>,
	): ResponseData<ApprovalStatusStatisticResponse> {
		return approvalStatusStatisticSbc.inquire(request)
	}
}
