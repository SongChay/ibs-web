package kh.bank.dgb.ibs.app.cbs.cancel_approval_request

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CancelApprovalNo(
	val approvalNo: Int = 0,
)

/** Port of `APV1003_REQ_CancelApprovalVo` — its old Java Vo used an asymmetric
 *  `@JsonGetter("grid01")`/`@JsonSetter("cancelApprovalNoList")` pair: the field is read from the
 *  client as `cancelApprovalNoList` but must be sent to CBS as `grid01`. Kept exactly: `@param`
 *  controls what key is accepted from the client, `@get` controls what key is sent onward to CBS. */
data class CancelApprovalRequestRequest(
	val userID: String? = null,
	val remark: String? = null,
	@param:JsonProperty("cancelApprovalNoList") @get:JsonProperty("grid01")
	val cancelApprovalNoList: List<CancelApprovalNo>? = null,
)

data class CancelApprovalRequestResponse(
	val resultYn: String? = null,
)

/** Port of `APV1003_Adapter_CancelApprovalRequest` — calls CBS opcode `CIB11003031` (via the old
 *  `DGBEBankingService.processAPR0025`). */
@RestController
@RequestMapping("/APV1003")
class CancelApprovalRequestCbc(
	private val sbc: CancelApprovalRequestSbc,
) {
	@PostMapping
	fun cancel(@RequestBody request: RequestData<CancelApprovalRequestRequest>): ResponseData<CancelApprovalRequestResponse> =
		sbc.cancel(request)
}
