package kh.bank.dgb.ibs.app.cbs.sub_user_detail

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SubUserDetailRequest(
	val customerNo: String? = null,
	val channelTypeCode: String? = null,
	val loginUserID: String? = null,
	val userID: String? = null,
)

data class SubUserDetailAccountAccessItem(
	val accountNo: String? = null,
	// Old Vo wire name from CBS is "depositSubjectCode"; the old adapter's outward field name was
	// "accountType".
	@JsonProperty("depositSubjectCode")
	val accountType: String? = null,
	val accountAccessRightTypeCode: String? = null,
	val registerDate: String? = null,
	val changeDate: String? = null,
	val inquiryDeleteYn: String? = null,
	val withdrawalDeleteYn: String? = null,
)

data class SubUserDetailMenuAccessItem(
	val seqNo: Long? = null,
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

data class SubUserDetailResponse(
	val userID: String? = null,
	val userName: String? = null,
	val corpBankingContactPhoneNo: String? = null,
	val departmentName: String? = null,
	val jobTitleName: String? = null,
	val remark: String? = null,
	val emailAddress: String? = null,
	val masterUserID: String? = null,
	val masterName: String? = null,
	val openDate: String? = null,
	val changeDate: String? = null,
	val lastLoginDate: String? = null,
	val lastLoginHms: String? = null,
	val serviceStatusCode: String? = null,
	// Old Vo: getter serialized to the client as "userAccountAccessInfoList", setter bound from CBS
	// as "grid01". `@JsonAlias` accepts the CBS input key while leaving the default property name
	// as the client-facing output key.
	@JsonAlias("grid01")
	val userAccountAccessInfoList: List<SubUserDetailAccountAccessItem>? = null,
	// Old Vo: getter serialized to the client as "userMenuAccessInfoList", setter bound from CBS
	// as "grid02" — same treatment.
	@JsonAlias("grid02")
	val userMenuAccessInfoList: List<SubUserDetailMenuAccessItem>? = null,
)

/** Port of `INF2201_Adapter_InquirySubUserDetail` — calls CBS opcode `CIB11302511` (via the old
 *  `DGBEBankingService.processMGR0011`).
 *
 *  Same situation as `UserAccessPrivilegeListCbc`/INF2102: the old adapter looped over
 *  `userAccountAccessInfoList` deriving `accountTypeName`, `accountFullAccessRightTypeCode` and
 *  `accountInquiryAccessRightTypeCode`, but all three were `@JsonIgnore` on
 *  `INF2201_RES_UserAccountAccessInfoListVo` — dead code from the caller's perspective.
 *  Intentionally NOT ported; flagged for extra scrutiny per the task instructions. */
@RestController
@RequestMapping("/INF2201")
class SubUserDetailCbc(
	private val sbc: SubUserDetailSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<SubUserDetailRequest>): ResponseData<SubUserDetailResponse> =
		sbc.inquire(request)
}
