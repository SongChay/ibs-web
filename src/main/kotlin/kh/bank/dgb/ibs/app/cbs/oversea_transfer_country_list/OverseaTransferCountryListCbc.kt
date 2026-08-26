package kh.bank.dgb.ibs.app.cbs.oversea_transfer_country_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class OverseaTransferCountryListRequest(
	val isEnabled: String? = null,
)

data class OverseaTransferCountryItem(
	val id: String? = null,
	val name: String? = null,
	val countryCode: String? = null,
	val isEnabled: String? = null,
)

/** Port of `TRS4002_RES_WrapperGetCountryListVo` — `@JsonSetter("grid01")`/`@JsonGetter("countryList")`
 *  pair: CBS returns the list as `grid01`, but the old adapter re-exposed it to the client as
 *  `countryList`. `@param` controls what key is read from CBS's response, `@get` controls what
 *  key is sent to the client. */
data class OverseaTransferCountryListResponse(
	@param:JsonProperty("grid01") @get:JsonProperty("countryList")
	val countryList: List<OverseaTransferCountryItem>? = null,
)

/** Port of `TRS4002_Adapter_InquiryCountryList` — calls CBS opcode `CIB11000303` (the old
 *  `DGBEBankingService.processCOM0004`). */
@RestController
@RequestMapping("/TRS4002")
class OverseaTransferCountryListCbc(
	private val overseaTransferCountryListSbc: OverseaTransferCountryListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<OverseaTransferCountryListRequest>): ResponseData<OverseaTransferCountryListResponse> {
		return overseaTransferCountryListSbc.inquire(request)
	}
}
