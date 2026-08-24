package kh.bank.dgb.ibs.app.cbs.current_approval_line

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CurrentApprovalLineRequest(
	val userID: String? = null,
	val corpBankingApprovalBizTypeCode: String? = null,
)

data class CurrentApprovalLineItem(
	val transferApproverID: String? = null,
	val transferApproverName: String? = null,
	val corpBankingApproverTypeCode: String? = null,
	val corpBankingApprovalStatusCode: String? = null,
	val terminationYn: String? = null,
	/** 00 : Application, 01 : Normal (Available), 08 : Ceased (Available, Warning), 09 : Terminated */
	val serviceStatusCode: String? = null,
)

/** Port of `TRS1101_Adapter_InquiryCurrentApprovalLine` — calls CBS opcode `CIB11001001`
 *  (via the old `DGBEBankingService.processAPR0011`). Straight pass-through — the old adapter also
 *  builds a `totalRes` local variable and copies `baseLineYn` into it, but never assigns it back to
 *  `resData` or returns it, so that block was dead code and isn't replicated. */
@RestController
@RequestMapping("/TRS1101")
class CurrentApprovalLineCbc(
	private val sbc: CurrentApprovalLineSbc,
) {
	@PostMapping
	fun inquire(@RequestBody request: RequestData<CurrentApprovalLineRequest>): ResponseData<CurrentApprovalLineResponse> =
		sbc.inquire(request)
}

data class CurrentApprovalLineResponse(
	val baseLineYn: String? = null,
	@param:JsonAlias("grid01")
	val transferApprovalList: List<CurrentApprovalLineItem>? = null,
)
