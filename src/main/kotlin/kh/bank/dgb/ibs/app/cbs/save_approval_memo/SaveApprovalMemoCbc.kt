package kh.bank.dgb.ibs.app.cbs.save_approval_memo

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SaveApprovalMemoRequest(
	val userID: String? = null,
	val approvalNo: Long = 0,
	val approvalMemo: String? = null,
)

data class SaveApprovalMemoResponse(
	val seqNo: Long = 0,
)

/** Port of `APV1204_Adapter_SaveApprovalMemo` — calls CBS opcode `CIB11003221` (via the old
 *  `DGBEBankingService.processAPR0021`). */
@RestController
@RequestMapping("/APV1204")
class SaveApprovalMemoCbc(
	private val sbc: SaveApprovalMemoSbc,
) {
	@PostMapping
	fun save(@RequestBody request: RequestData<SaveApprovalMemoRequest>): ResponseData<SaveApprovalMemoResponse> =
		sbc.save(request)
}
