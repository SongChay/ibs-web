package kh.bank.dgb.ibs.app.cbs.inquiry_deposit_status

import com.fasterxml.jackson.annotation.JsonAlias
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class InquiryDepositStatusRequest(
	val userID: String? = null,
	val customerNo: String? = null,
	/**
	 * customer account type code
	 * 00 : all ( saving, deposit, loan )
	 * 01 : Saving
	 * 02 : deposit
	 * 03 : loan
	 */
	val accountTypeCode: String? = null,
	val currencyCode: String? = null,
)

data class InquiryDepositStatusItem(
	val accountNo: String? = null,
	val productName: String? = null,
	val loanAmount: java.math.BigDecimal? = null,
	val applyInterestRate: java.math.BigDecimal? = null,
	val installmentsMonth: Long? = null,
	val paymentDay: String? = null,
	val repaymentMethodCode: String? = null,
	val repaymentMethodDescription: String? = null,
	val accountName: String? = null,
	val openDate: String? = null,
	val closeDate: String? = null,
	val branchCode: String? = null,
	val branchName: String? = null,
	val balance: java.math.BigDecimal? = null,
	val currencyCode: String? = null,
	val accountNickName: String? = null,
	val depositSubjectCode: String? = null,
	val depositSubjectDescription: String? = null,
	val depositAccountStatusCode: String? = null,
	val depositAccountStatusDescription: String? = null,
)

data class InquiryDepositStatusResponse(
	val totalAmount: java.math.BigDecimal? = null,
	@JsonAlias("grid01") val accountList: List<InquiryDepositStatusItem>? = null,
)

/** Port of `MAN1002_Adapter_InquiryDepositStatus` — calls CBS opcode `CIB11300412`
 *  (via the old `DGBEBankingService.processCIB11300412`).
 *
 *  Real logic beyond a plain pass-through: on a successful response, the old adapter fills in
 *  three human-readable description fields per account row (`depositSubjectDescription`,
 *  `depositAccountStatusDescription`, `repaymentMethodDescription`) by looking up the
 *  corresponding code against fixed enum tables (old `DataUtils`). Replicated in the Sbc. */
@RestController
@RequestMapping("/MAN1002")
class InquiryDepositStatusCbc(
	private val inquiryDepositStatusSbc: InquiryDepositStatusSbc,
) {
	@PostMapping
	fun inquire(
		@RequestBody request: RequestData<InquiryDepositStatusRequest>,
	): ResponseData<InquiryDepositStatusResponse> {
		return inquiryDepositStatusSbc.inquire(request)
	}
}
