package kh.bank.dgb.ibs.app.cbs.user_access_privilege_list

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class UserAccessPrivilegeListRequest(
	val loginUserID: String? = null,
	val channelTypeCode: String? = null,
	val customerNo: String? = null,
	val userID: String? = null,
)

data class UserAccountAccessInfoItem(
	val accountNo: String? = null,
	// Old Vo wire name from CBS is "depositSubjectCode"; the old adapter's outward field name was
	// "accountType".
	@JsonProperty("depositSubjectCode")
	val accountType: String? = null,
	val registerDate: String? = null,
	val changeDate: String? = null,
	val accountAccessRightTypeCode: String? = null,
	val withdrawalDeleteYn: String? = null,
	val inquiryDeleteYn: String? = null,
)

data class UserMenuAccessInfoItem(
	val seqNo: Int? = null,
	val programTypeCode: String? = null,
	val level1MenuCode: String? = null,
	val level2MenuCode: String? = null,
	val menuUseRightTypeCode: String? = null,
	val level1MenuDescription: String? = null,
	val level2MenuDescription: String? = null,
	val functionName: String? = null,
	val registerDate: String? = null,
	val changeDate: String? = null,
)

data class UserAccessPrivilegeListResponse(
	// Old Vo: getter serialized to the client as "userAccountAccessInfoList", setter bound from
	// CBS as "grid01". `@JsonAlias` accepts the CBS input key while leaving the default property
	// name as the client-facing output key.
	@JsonAlias("grid01")
	val userAccountAccessInfoList: List<UserAccountAccessInfoItem>? = null,
	// Old Vo: getter serialized to the client as "userMenuAccessInfoList", setter bound from CBS
	// as "grid02" — same treatment.
	@JsonAlias("grid02")
	val userMenuAccessInfoList: List<UserMenuAccessInfoItem>? = null,
)

/** Port of `INF2102_Adapter_InquiryUserAccessPrivilegeList` — calls CBS opcode `CIB11302412`
 *  (via the old `DGBEBankingService.processMGR0001`).
 *
 *  The old adapter also looped over `userAccountAccessInfoList` deriving `accountTypeName`,
 *  `accountFullAccessRightTypeCode` and `accountInquiryAccessRightTypeCode` from
 *  `accountType`/`accountAccessRightTypeCode` — but all three target fields were annotated
 *  `@JsonIgnore` on `INF2102_RES_UserAccountAccessInfoListVo`, so that whole loop never affected
 *  the JSON actually sent to the client. Confirmed dead code from the caller's perspective;
 *  intentionally NOT ported (omitted those three fields entirely rather than computing values
 *  nobody can see). Flagged for extra scrutiny per the task instructions. */
@RestController
@RequestMapping("/INF2102")
class UserAccessPrivilegeListCbc(
	private val sbc: UserAccessPrivilegeListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<UserAccessPrivilegeListRequest>): ResponseData<UserAccessPrivilegeListResponse> =
		sbc.inquire(request)
}
