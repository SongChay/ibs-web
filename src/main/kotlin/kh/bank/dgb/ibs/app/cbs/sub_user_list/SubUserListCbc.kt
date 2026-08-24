package kh.bank.dgb.ibs.app.cbs.sub_user_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SubUserListRequest(
	val customerNo: String? = null,
	val channelTypeCode: String? = null,
	val loginUserID: String? = null,
)

data class SubUserListItem(
	val userID: String? = null,
	val userName: String? = null,
	val corpBankingContactPhoneNo: String? = null,
	val departmentName: String? = null,
	val jobTitleName: String? = null,
	val remark: String? = null,
	val emailAddress: String? = null,
	val openDate: String? = null,
	val changeDate: String? = null,
	val lastLoginDate: String? = null,
	// Old Vo: getter serialized to the client as "lastLoginHms", setter bound from CBS as
	// "lastLoginHMS" (different casing) — kept as the CBS wire name here.
	@JsonProperty("lastLoginHMS")
	val lastLoginHms: String? = null,
	val lastAccessDate: String? = null,
	val serviceStatusCode: String? = null,
	val serviceStatusDesc: String? = null,
)

data class SubUserListResponse(
	// Old Vo: getter serialized to the client as "corporateSubUserInfoList", setter bound from CBS
	// as "grid01" — kept as the CBS wire name here.
	@JsonProperty("grid01")
	val corporateSubUserInfoList: List<SubUserListItem>? = null,
)

/** Port of `INF2001_Adapter_InquirySubUserList` — calls CBS opcode `CIB11002301` (via the old
 *  `DGBEBankingService.processMGR0010`). Non-pass-through: the old adapter formats
 *  `lastLoginDate`/`lastLoginHms` into display strings, builds a combined `lastAccessDate`,
 *  formats `openDate`, and derives `serviceStatusDesc` from `serviceStatusCode` for every row —
 *  ported faithfully in `SubUserListSbc` (see its `TODO` for the one open question). */
@RestController
@RequestMapping("/INF2001")
class SubUserListCbc(
	private val sbc: SubUserListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<SubUserListRequest>): ResponseData<SubUserListResponse> =
		sbc.inquire(request)
}
