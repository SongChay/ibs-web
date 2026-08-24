package kh.bank.dgb.ibs.app.cbs.virtual_account_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class VirtualAccountListRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val currencyCode: String? = null,
	val selectedBy: String? = null,
	val inquiryBy: String? = null,
	val fromDate: String? = null,
	val toDate: String? = null,
	val searchBy: String? = null,
	val searchKeyword: String? = null,
	val currentPage: Int? = null,
	val pageSize: Int? = null,
)

data class VirtualAccountListItem(
	val virtualAccountNo: String? = null,
	val parentAccountNo: String? = null,
	val paymentName: String? = null,
	val customerUniqueNo: String? = null,
	val customerDescription: String? = null,
	val virtualAccountReceiveTypeCode: String? = null,
	val virtualAccountReceiveType: String? = null,
	val currencyCode: String? = null,
	val monthlyRepetitionYN: String? = null,
	val depositStartDate: String? = null,
	val depositStartHMS: String? = null,
	val depositEndDate1: String? = null,
	val depositEndHMS1: String? = null,
	val registerAmount1: BigDecimal? = null,
	val useYN: String? = null,
	val depositEndDate2: String? = null,
	val depositEndHMS2: String? = null,
	val registerAmount2: BigDecimal? = null,
	val virtualAccountReceiveStatusCode: String? = null,
	val virtualAccountReceiveStatus: String? = null,
	val depositDate: String? = null,
	val balanceAmount: BigDecimal? = null,
	val accountExpiryDate: String? = null,
)

data class VirtualAccountListResponse(
	val totalCount: Long? = null,
	val virtualAccountList: List<VirtualAccountListItem>? = null,
)

/** Port of `VAC1001_Adapter_InquiryVirtualAccountList` — calls CBS opcode `CIB11302011`. */
@RestController
@RequestMapping("/VAC1001")
class VirtualAccountListCbc(
	private val sbc: VirtualAccountListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<VirtualAccountListRequest>): ResponseData<VirtualAccountListResponse> =
		sbc.inquire(request)
}
