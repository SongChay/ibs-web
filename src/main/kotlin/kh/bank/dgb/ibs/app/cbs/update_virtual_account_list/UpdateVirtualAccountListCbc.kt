package kh.bank.dgb.ibs.app.cbs.update_virtual_account_list

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class UpdateVirtualAccountListItem(
	val virtualAccountNo: String? = null,
	val parentAccountNo: String? = null,
	val paymentName: String? = null,
	val customerUniqueNo: String? = null,
	val customerDescription: String? = null,
	val depositStartDate: String? = null,
	val depositStartHMS: String? = null,
	val virtualAccountReceiveTypeCode: String? = null,
	val monthlyRepetitionYN: String? = null,
	val depositEndDate1: String? = null,
	val depositEndHMS1: String? = null,
	val registerAmount1: BigDecimal? = null,
	val depositEndDate2: String? = null,
	val depositEndHMS2: String? = null,
	val registerAmount2: BigDecimal? = null,
	val accountExpiryDate: String? = null,
)

data class UpdateVirtualAccountListRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val beginningFlag: String? = null,
	val endFlag: String? = null,
	val virtualAccountList: List<UpdateVirtualAccountListItem>? = null,
)

data class UpdateVirtualAccountListResponse(
	val successCount: Int? = null,
	val failCount: Int? = null,
)

/** Port of `VAC1002_Adapter_UpdateVirtualAccountList` — calls CBS opcode `CIB11302031`. */
@RestController
@RequestMapping("/VAC1002")
class UpdateVirtualAccountListCbc(
	private val updateVirtualAccountListSbc: UpdateVirtualAccountListSbc,
) {
	@PostMapping
	fun update(@RequestBody request: RequestData<UpdateVirtualAccountListRequest>): ResponseData<UpdateVirtualAccountListResponse> {
		return updateVirtualAccountListSbc.update(request)
	}
}
