package kh.bank.dgb.ibs.app.cbs.retrieve_virtual_account_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class RetrieveVirtualAccountListRequest(
	val customerNo: String? = null,
)

data class RetrieveVirtualAccountItem(
	val virtualAccountNo: String? = null,
)

data class RetrieveVirtualAccountListResponse(
	val virtualAccountList: List<RetrieveVirtualAccountItem>? = null,
)

/** Port of `VAC1007_Adapter_RetrieveVirtualAccountList` — calls CBS opcode `CIB11302211`
 *  (via the old `DGBEBankingService.retrieveVirtualAccountList`). */
@RestController
@RequestMapping("/VAC1007")
class RetrieveVirtualAccountListCbc(
	private val sbc: RetrieveVirtualAccountListSbc,
) {
	@PostMapping
	fun retrieve(@RequestBody request: RequestData<RetrieveVirtualAccountListRequest>): ResponseData<RetrieveVirtualAccountListResponse> =
		sbc.retrieve(request)
}
