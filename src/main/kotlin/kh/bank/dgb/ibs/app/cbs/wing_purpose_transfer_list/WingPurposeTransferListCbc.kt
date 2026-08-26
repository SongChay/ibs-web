package kh.bank.dgb.ibs.app.cbs.wing_purpose_transfer_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** The old `TRS5001_REQ_WingPurposeTransferListVo` has no fields — the request body is empty. */
class WingPurposeTransferListRequest

data class WingPurposeTransferItem(
	val purposeId: String? = null,
	val description: String? = null,
)

data class WingPurposeTransferListResponse(
	val purposeList: List<WingPurposeTransferItem>? = null,
)

/**
 * Port of `TRS5001_Adapter_InquiryWingPurposeTransferList` — calls CBS opcode `CIB11001801` (the
 * old `DGBEBankingService.processWNG001`).
 *
 * Small extra logic beyond a pure pass-through: if CBS comes back with a failed header
 * (`header.result == false`), the old adapter deliberately overrides it to `true` — comment in
 * the old code: "In case get error from external server we will set true to header result".
 * Replicated in `WingPurposeTransferListSbc`.
 */
@RestController
@RequestMapping("/TRS5001")
class WingPurposeTransferListCbc(
	private val wingPurposeTransferListSbc: WingPurposeTransferListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<WingPurposeTransferListRequest>): ResponseData<WingPurposeTransferListResponse> {
		return wingPurposeTransferListSbc.inquire(request)
	}
}
