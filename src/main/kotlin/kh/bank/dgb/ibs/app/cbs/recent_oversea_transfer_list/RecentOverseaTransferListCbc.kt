package kh.bank.dgb.ibs.app.cbs.recent_oversea_transfer_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RecentOverseaTransferListRequest(
	val userID: String? = null,
)

data class RecentOverseaTransferItem(
	val beneficiaryCountry: String? = null,
	val beneficiaryAccountNo: String? = null,
	val counterpartBankAccountNumber: String? = null,
	val beneficiaryBankName: String? = null,
	val beneficiaryName: String? = null,
	val beneficiarySwiftCode: String? = null,
	val beneficiaryAddress: String? = null,
)

/** Port of `TRS4103_RES_InquiryWrapperRecentOverseasTransferVo` — `@JsonSetter("grid01")`/
 *  `@JsonGetter("rencentTransferList")` pair (name misspelled in the old code, kept verbatim as
 *  the wire contract). `@param` controls what key is read from CBS's response, `@get` controls
 *  what key is sent to the client. */
data class RecentOverseaTransferListResponse(
	@param:JsonProperty("grid01") @get:JsonProperty("rencentTransferList")
	val rencentTransferList: List<RecentOverseaTransferItem>? = null,
)

/** Port of `TRS4103_Adapter_InquiryRecentOverseasTransfer` — calls CBS opcode `CIB11301612` (the
 *  old `DGBEBankingService.processCIB11301612`). */
@RestController
@RequestMapping("/TRS4103")
class RecentOverseaTransferListCbc(
	private val sbc: RecentOverseaTransferListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<RecentOverseaTransferListRequest>): ResponseData<RecentOverseaTransferListResponse> =
		sbc.inquire(request)
}
