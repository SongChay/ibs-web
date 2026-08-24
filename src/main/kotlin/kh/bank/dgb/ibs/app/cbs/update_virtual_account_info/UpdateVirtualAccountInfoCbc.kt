package kh.bank.dgb.ibs.app.cbs.update_virtual_account_info

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class UpdateVirtualAccountInfoRequest(
	val userID: String? = null,
	val customerNo: String? = null,
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

/** Port of `VAC1004_RES_UpdateVirtualAccountInfoVo` — genuinely empty in the old app. */
class UpdateVirtualAccountInfoResponse

/** Port of `VAC1004_Adapter_UpdateVirtualAccountInfo` — calls CBS opcode `CIB11302032`. */
@RestController
@RequestMapping("/VAC1004")
class UpdateVirtualAccountInfoCbc(
	private val sbc: UpdateVirtualAccountInfoSbc,
) {
	@PostMapping
	fun update(@RequestBody request: RequestData<UpdateVirtualAccountInfoRequest>): ResponseData<UpdateVirtualAccountInfoResponse> =
		sbc.update(request)
}
