package kh.bank.dgb.ibs.app.cbs.failed_virtual_account_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class FailedVirtualAccountListRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val pageSize: Int? = null,
	val currentPage: Int? = null,
)

data class FailedVirtualAccountListItem(
	val virtualAccountNo: String? = null,
	val parentAccountNo: String? = null,
	val paymentName: String? = null,
	val customerUniqueNo: String? = null,
	val customerDescription: String? = null,
	val virtualAccountReceiveTypeCode: String? = null,
	val depositStartDate: String? = null,
	val depositStartHMS: String? = null,
	val depositEndDate1: String? = null,
	val depositEndHMS1: String? = null,
	val registerAmount1: BigDecimal? = null,
	val depositEndDate2: String? = null,
	val depositEndHMS2: String? = null,
	val registerAmount2: BigDecimal? = null,
	val accountExpiryDate: String? = null,
	val transactionErrorDetailCode: String? = null,
	val paymentInfoUploadFailReasonDesc: String? = null,
)

data class FailedVirtualAccountListResponse(
	val totalCount: Int? = null,
	val virtualAccountList: List<FailedVirtualAccountListItem>? = null,
)

/** Port of `VAC1003_Adapter_ViewFailedVirtualAccountList` — calls CBS opcode `CIB11002012`. */
@RestController
@RequestMapping("/VAC1003")
class FailedVirtualAccountListCbc(
	private val sbc: FailedVirtualAccountListSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<FailedVirtualAccountListRequest>): ResponseData<FailedVirtualAccountListResponse> =
		sbc.inquire(request)
}
