package kh.bank.dgb.ibs.app.cbs.inquiry_visited_menu_list

import com.fasterxml.jackson.annotation.JsonIgnore
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class InquiryVisitedMenuListRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
)

/** Port of `GNB1005_RES_VistedMenuVo` — `userID`, `registerDate`, `deleteYN` were `@JsonIgnore` in
 *  the old Vo (never surfaced to the client even though CBS may return them); kept ignored here. */
data class VisitedMenuItem(
	@get:JsonIgnore val userID: String? = null,
	val visitedMenuCode: String? = null,
	@get:JsonIgnore val registerDate: String? = null,
	@get:JsonIgnore val deleteYN: String? = null,
	val menuDescription: String? = null,
)

/** Port of `GNB1005_RES_WrappterInquiryVistedMenuListVo` — the field is literally named `grid01`
 *  in the old Vo (no rename needed). */
data class InquiryVisitedMenuListResponse(
	val grid01: List<VisitedMenuItem>? = null,
)

/** Port of `GNB1005_Adapter_InquiryVisitedMenuList` — calls CBS opcode `CIB11300611` (via the old
 *  `DGBEBankingService.processCIB11300611`). `channelTypeCode` is forced to the fixed corporate
 *  banking channel code "01" before forwarding, same as the old adapter. */
@RestController
@RequestMapping("/GNB1005")
class InquiryVisitedMenuListCbc(
	private val inquiryVisitedMenuListSbc: InquiryVisitedMenuListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<InquiryVisitedMenuListRequest>): ResponseData<InquiryVisitedMenuListResponse> {
		return inquiryVisitedMenuListSbc.inquire(request)
	}
}
