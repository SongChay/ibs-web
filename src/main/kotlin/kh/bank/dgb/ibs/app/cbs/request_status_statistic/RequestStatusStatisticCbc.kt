package kh.bank.dgb.ibs.app.cbs.request_status_statistic

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RequestStatusStatisticRequest(
	val userID: String? = null,
)

data class RequestStatusStatisticResponse(
	val requestDraft: Long? = null,
	val requestProcessing: Long? = null,
	val requestReject: Long? = null,
	val requestApproved: Long? = null,
)

/** Port of `MAN1007_Adapter_RequestStatusStatistic` — calls CBS opcode `CIB11302012`
 *  (via the old `DGBEBankingService.processCIB11302012`). */
@RestController
@RequestMapping("/MAN1007")
class RequestStatusStatisticCbc(
	private val requestStatusStatisticSbc: RequestStatusStatisticSbc,
) {
	@PostMapping
	fun inquire(
		@RequestBody request: RequestData<RequestStatusStatisticRequest>,
	): ResponseData<RequestStatusStatisticResponse> {
		return requestStatusStatisticSbc.inquire(request)
	}
}
