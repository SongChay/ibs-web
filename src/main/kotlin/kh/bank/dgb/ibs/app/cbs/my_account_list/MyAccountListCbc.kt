package kh.bank.dgb.ibs.app.cbs.my_account_list

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class MyAccountListRequest(
	val userID: String? = null,
	val customerNo: String? = null,
)

data class MyAccountListItem(
	val depositSubjectCode: String? = null,
	val depositSubjectDescription: String? = null,
	val accountNo: String? = null,
	val depositAccountStatusCode: String? = null,
	val depositAccountStatusDescription: String? = null,
	val customerName: String? = null,
	val accountNickName: String? = null,
	val currencyCode: String? = null,
	val balance: BigDecimal? = null,
)

data class MyAccountListResponse(
	@JsonAlias("grid01") val accountList: List<MyAccountListItem>? = null,
)

/** Port of `TRS1401_Adapter_InquiryMyAccountList` — calls CBS opcode `CIB11001201` (via the old
 *  `DGBEBankingService.processACC0022`). */
@RestController
@RequestMapping("/TRS1401")
class MyAccountListCbc(
	private val myAccountListSbc: MyAccountListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<MyAccountListRequest>): ResponseData<MyAccountListResponse> {
		return myAccountListSbc.inquire(request)
	}
}
