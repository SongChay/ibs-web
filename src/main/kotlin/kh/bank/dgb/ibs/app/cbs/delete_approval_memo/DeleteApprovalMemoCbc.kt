package kh.bank.dgb.ibs.app.cbs.delete_approval_memo

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class DeleteApprovalMemoRequest(
	val userID: String? = null,
	val approvalNo: Long = 0,
	val seqNo: Long = 0,
)

/** Port of the old `APV1205_RES_DeleteApprovalMemoVo` — CBS returns an empty object for this
 *  opcode, so this response type has no fields. */
class DeleteApprovalMemoResponse

/** Port of `APV1205_Adapter_DeleteApprovalMemo` — calls CBS opcode `CIB11003231` (via the old
 *  `DGBEBankingService.processAPR0022`). */
@RestController
@RequestMapping("/APV1205")
class DeleteApprovalMemoCbc(
	private val deleteApprovalMemoSbc: DeleteApprovalMemoSbc,
) {
	@PostMapping
	fun delete(@RequestBody request: RequestData<DeleteApprovalMemoRequest>): ResponseData<DeleteApprovalMemoResponse> {
		return deleteApprovalMemoSbc.delete(request)
	}
}
