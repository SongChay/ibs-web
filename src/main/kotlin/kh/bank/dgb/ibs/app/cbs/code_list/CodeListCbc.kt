package kh.bank.dgb.ibs.app.cbs.code_list

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CodeListRequest(
	val searchType: String? = null,
	val searchKeyword: String? = null,
)

data class CodeListItem(
	val groupCode: String? = null,
	val code: String? = null,
	val value: String? = null,
	val remark: String? = null,
	val description: String? = null,
	val rFTBankCode: String? = null,
	val fastRemark: String? = null,
	val interbankYN: String? = null,
)

/** Port of `COM0001_RES_WrapperCodeListVo` — its old Java Vo used an asymmetric
 *  `@JsonGetter("codeList")`/`@JsonSetter("grid01")` pair: CBS returns the list as `grid01`, but
 *  the field is re-exposed to the client as `codeList`. Kept exactly: `@param` controls what key
 *  is read from CBS's response, `@get` controls what key is sent to the client. */
data class CodeListResponse(
	@param:JsonProperty("grid01") @get:JsonProperty("codeList")
	val codeList: List<CodeListItem>? = null,
)

/** Port of `COM0001_Adapter_CodeList` — calls CBS opcode `CIB11000012` (via the old
 *  `DGBEBankingService.processCOM0001`). */
@RestController
@RequestMapping("/COM0001")
class CodeListCbc(
	private val codeListSbc: CodeListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<CodeListRequest>): ResponseData<CodeListResponse> {
		return codeListSbc.inquire(request)
	}
}
