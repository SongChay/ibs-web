package kh.bank.dgb.ibs.app.cbs.payroll_payment_register

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PayrollPaymentRegisterItem(
	val receiptAccountNo: String? = null,
	val currencyCode: String? = null,
	val receiptAmount: BigDecimal? = null,
	val accountName: String? = null,
	val depositSubjectCode: String? = null,
	val depositAccountStatusCode: String? = null,
	val remark: String? = null,
	val salaryTransferFeeStatusCode: String? = null,
)

data class ApprovalLineItem(
	val approverTypeCode: String? = null,
	val approverID: String? = null,
)

data class VerifyQrCodeRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val verifyCode: Int? = null,
	val firstYn: String? = null,
)

data class PayrollPaymentRegisterRequest(
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val transferTypeCode: String? = null,
	val currencyCode: String? = null,
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
	val salaryTransferTotalFeeAmount: BigDecimal? = null,
	val salaryTransferExecutionDate: String? = null,
	val salaryTransferExecutionTime: String? = null,
	val salaryTransferAmount: BigDecimal? = null,
	val salaryTransferCount: BigDecimal? = null,
	val scheduleTransferTypeCode: String? = null,
	val remark: String? = null,
	val grid02Count: String? = null,
	val grid02: List<PayrollPaymentRegisterItem>? = null,
	val approvalList: List<ApprovalLineItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

data class PayrollPaymentRegisterResponse(
	val resultYn: String? = null,
	val approvalNo: Long? = null,
)

/** Port of `PYR1103_Adapter_RegisterPayrollPayment` — calls CBS opcode `CIB11300221`
 *  (`DGBEBankingService.processCIB11300221`), gated behind a service-status blocking-time check
 *  and an OTP verification round-trip. NOT a simple pass-through — see `PayrollPaymentRegisterSbc`
 *  doc comment for the full flow and a flagged bug fix vs. the old code. */
@RestController
@RequestMapping("/PYR1103")
class PayrollPaymentRegisterCbc(
	private val payrollPaymentRegisterSbc: PayrollPaymentRegisterSbc,
) {
	@PostMapping
	fun register(@RequestBody request: RequestData<PayrollPaymentRegisterRequest>): ResponseData<PayrollPaymentRegisterResponse> {
		return payrollPaymentRegisterSbc.register(request)
	}
}
