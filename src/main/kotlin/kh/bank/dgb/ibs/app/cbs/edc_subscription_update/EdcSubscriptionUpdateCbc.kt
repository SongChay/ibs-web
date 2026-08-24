package kh.bank.dgb.ibs.app.cbs.edc_subscription_update

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EdcSubscriptionUpdateRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	val counterpartAccountNo: String? = null,
	val templateName: String? = null,
	val accountNo: String? = null,
)

data class EdcSubscriptionUpdateResponse(
	val success: Boolean? = null,
)

/** Port of `TRS2531_Adapter_UpdateSubscriptionInfoEDC` — calls CBS opcode `CIB11102531` (via the
 *  old `DGBEBankingService.processCIB11102531`). */
@RestController
@RequestMapping("/TRS2531")
class EdcSubscriptionUpdateCbc(
	private val sbc: EdcSubscriptionUpdateSbc,
) {
	@PostMapping
	fun update(@RequestBody request: RequestData<EdcSubscriptionUpdateRequest>): ResponseData<EdcSubscriptionUpdateResponse> =
		sbc.update(request)
}
