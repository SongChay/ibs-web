package kh.bank.dgb.ibs.app.cbs.approver_list

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class ApproverListRequest(
	val userID: String? = null,
)

data class ApproverItem(
	val userID: String? = null,
	val userName: String? = null,
)

/** Port of `TRS1103_Adapter_InquiryApproverList` — calls CBS opcode `CIB11001002`
 *  (via the old `DGBEBankingService.processAPR0012`). Straight pass-through. */
@RestController
@RequestMapping("/TRS1103")
class ApproverListCbc(
	private val approverListSbc: ApproverListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<ApproverListRequest>): ResponseData<ApproverListResponse> {
		return approverListSbc.inquire(request)
	}
}

data class ApproverListResponse(
	@param:JsonAlias("grid01")
	val approverList: List<ApproverItem>? = null,
)
