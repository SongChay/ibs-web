package kh.bank.dgb.ibs.app.cbs.virtual_account_institution_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class VirtualAccountInstitutionListRequest(
	val customerNo: String? = null,
)

data class VirtualAccountInstitutionItem(
	val institutionCode: String? = null,
	val institutionName: String? = null,
)

data class VirtualAccountInstitutionListResponse(
	val institutionList: List<VirtualAccountInstitutionItem>? = null,
)

/** Port of `VAC1005_Adapter_GetInstitutionList` — calls CBS opcode `CIB11002013`. */
@RestController
@RequestMapping("/VAC1005")
class VirtualAccountInstitutionListCbc(
	private val sbc: VirtualAccountInstitutionListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<VirtualAccountInstitutionListRequest>): ResponseData<VirtualAccountInstitutionListResponse> =
		sbc.inquire(request)
}
