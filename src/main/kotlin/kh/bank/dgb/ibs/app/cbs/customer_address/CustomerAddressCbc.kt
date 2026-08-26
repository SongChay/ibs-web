package kh.bank.dgb.ibs.app.cbs.customer_address

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CustomerAddressRequest(
	val customerNo: String? = null,
)

data class CustomerAddressResponse(
	val customerAddress: String? = null,
	val idCardNo: String? = null,
)

/** Port of `TRS4104_Adapter_InquiryCustomerAddress` — calls CBS opcode `CIB11302512` (the old
 *  `DGBEBankingService.processCIB11302512`). */
@RestController
@RequestMapping("/TRS4104")
class CustomerAddressCbc(
	private val customerAddressSbc: CustomerAddressSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<CustomerAddressRequest>): ResponseData<CustomerAddressResponse> {
		return customerAddressSbc.inquire(request)
	}
}
