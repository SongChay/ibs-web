package kh.bank.dgb.ibs.app.cbs.account_no_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.app.cbs.all_account_inquiry_list.AllAccountInquiryListRequest
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** Port of `ACI1002_RES_AccountNoListVo`. */
data class AccountNoListItem(
	val accountNo: String? = null,
	val accountName: String? = null,
	val accountNickName: String? = null,
	val currencyCode: String? = null,
)

/** Port of `ACI1002_RES_WrapperAccountNoListVo` — the old Vo puts a single (non-split)
 *  `@JsonProperty("grid01")` on the field, so unlike the other list wrappers in this batch the
 *  wire key is `grid01` in *both* directions, not just on the way in. */
data class AccountNoListResponse(
	@JsonProperty("grid01")
	val accountList: List<AccountNoListItem>? = null,
)

/**
 * Port of `ACI1002_Adapter_AccountNoList` — calls CBS opcode `CIB11300612` (via the old
 * `DGBEBankingService.getAccountList`, the same method `ACI1006_Adapter_AllAccountInquiryList`
 * uses).
 *
 * NOT a plain pass-through: reuses `AllAccountInquiryListSbc.fetchFilteredAccounts` (same
 * account-type/no/nickname/currency filtering as ACI1006 — see its doc comments) and then
 * projects each row down to just `accountNo`/`accountName`/`accountNickName`/`currencyCode`
 * (`BeanUtils.convert` in the old adapter). Unlike ACI1006, this adapter does NOT apply the
 * code-table description enrichment.
 */
@RestController
@RequestMapping("/ACI1002")
class AccountNoListCbc(
	private val sbc: AccountNoListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<AllAccountInquiryListRequest>): ResponseData<AccountNoListResponse> =
		sbc.inquire(request)
}
