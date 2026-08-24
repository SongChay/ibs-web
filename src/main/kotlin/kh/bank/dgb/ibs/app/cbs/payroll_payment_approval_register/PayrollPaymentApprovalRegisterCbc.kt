package kh.bank.dgb.ibs.app.cbs.payroll_payment_approval_register

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PayrollPaymentApprovalItem(
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

data class PayrollPaymentApprovalRegisterRequest(
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
	val grid02: List<PayrollPaymentApprovalItem>? = null,
	val approvalList: List<ApprovalLineItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

data class PayrollPaymentApprovalRegisterResponse(
	val resultYn: String? = null,
	val approvalNo: Long? = null,
)

/** Port of `PYR1107_Adapter_RegisterPayrollPaymentApproval` — calls CBS opcode `CIB11300222`
 *  (`DGBEBankingService.processCIB11300222`, response type reused from the old
 *  `PYR1103_RES_RegisterPayrollPaymentVo`). Also replicates the old adapter's post-processing:
 *  overwrites `resultYn` with "Y"/"N" based on the response header's `result` flag. Unlike
 *  `PYR1103_Adapter_RegisterPayrollPayment`, this adapter has no service-status/OTP gating — it's
 *  a direct call. */
@RestController
@RequestMapping("/PYR1107")
class PayrollPaymentApprovalRegisterCbc(
	private val sbc: PayrollPaymentApprovalRegisterSbc,
) {
	@PostMapping
	fun register(
		@RequestBody request: RequestData<PayrollPaymentApprovalRegisterRequest>,
	): ResponseData<PayrollPaymentApprovalRegisterResponse> = sbc.register(request)
}
