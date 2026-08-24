package kh.bank.dgb.ibs.app.cbs.approve_requests

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ApprovalNo(
	val approvalNo: Long = 0,
)

/** Port of `APV2103_REQ_ApprovalVo` — its old Java Vo used an asymmetric
 *  `@JsonGetter("grid01")`/`@JsonSetter("approvalNoList")` pair: read from the client as
 *  `approvalNoList` but sent to CBS as `grid01`. Kept exactly. */
data class ApproveRequestsRequest(
	val userID: String? = null,
	val approvalStatusCode: String? = null,
	val approvalTypeCode: String? = null,
	@param:JsonProperty("approvalNoList") @get:JsonProperty("grid01")
	val approvalNoList: List<ApprovalNo>? = null,
)

/** Port of the old `APV2103_RES_ApprovalVo` — CBS returns an empty object for this opcode. */
class ApproveRequestsResponse

/** Port of `APV2103_Adapter_Approval` — calls CBS opcode `CIB11303331` (via the old
 *  `DGBEBankingService.processCIB11303331`). */
@RestController
@RequestMapping("/APV2103")
class ApproveRequestsCbc(
	private val sbc: ApproveRequestsSbc,
) {
	@PostMapping
	fun approve(@RequestBody request: RequestData<ApproveRequestsRequest>): ResponseData<ApproveRequestsResponse> =
		sbc.approve(request)
}
