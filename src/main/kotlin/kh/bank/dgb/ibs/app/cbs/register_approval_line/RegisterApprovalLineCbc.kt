package kh.bank.dgb.ibs.app.cbs.register_approval_line

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RegisterApprovalLineRequest(
	val userID: String? = null,
	// Old Vo: getter serialized to CBS as "grid01", setter bound from the client as
	// "transferTypeList" — this same object is both the `@RequestBody` shape and the exact body
	// forwarded via `connector.post`, so `@param` controls the inbound (client) key and `@get`
	// controls the outbound-to-CBS key.
	@param:JsonProperty("transferTypeList") @get:JsonProperty("grid01")
	val transferTypeList: List<TransferTypeItem>? = null,
	// Old Vo: getter serialized to CBS as "grid02", setter bound from the client as
	// "approvalLineList" — same treatment.
	@param:JsonProperty("approvalLineList") @get:JsonProperty("grid02")
	val approvalLineList: List<ApprovalLineItem>? = null,
)

data class TransferTypeItem(
	val corpBankingApprovalBizTypeCode: String? = null,
)

data class ApprovalLineItem(
	val transferApproverID: String? = null,
	val corpBankingApproverTypeCode: String? = null,
)

/** Old `INF3004_RES_RegisterApprovalLineVo` has no fields at all — CBS returns an empty body. */
class RegisterApprovalLineResponse

/** Port of `INF3004_Adapter_RegisterApprovalLine` — calls CBS opcode `CIB11302621` (via the old
 *  `DGBEBankingService.processAPR0013`). */
@RestController
@RequestMapping("/INF3004")
class RegisterApprovalLineCbc(
	private val sbc: RegisterApprovalLineSbc,
) {
	@PostMapping
	fun register(@RequestBody request: RequestData<RegisterApprovalLineRequest>): ResponseData<RegisterApprovalLineResponse> =
		sbc.register(request)
}
