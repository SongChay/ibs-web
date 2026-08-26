package kh.bank.dgb.ibs.app.cbs.payroll_schedule_transfer_update

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PayrollScheduleTransferItem(
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

data class PayrollScheduleTransferUpdateRequest(
	val approvalNo: BigDecimal? = null,
	val userID: String? = null,
	val channelTypeCode: String? = null,
	val transferTypeCode: String? = null,
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
	val salaryTransferTotalFeeAmount: BigDecimal? = null,
	val salaryTransferExecutionDate: String? = null,
	val salaryTransferExecutionTime: String? = null,
	val salaryTransferAmount: BigDecimal? = null,
	val salaryTransferCount: BigDecimal? = null,
	val scheduleTransferTypeCode: String? = null,
	val remark: String? = null,
	val acceptanceSeqNo: Long? = null,
	val grid02Count: String? = null,
	val grid02: List<PayrollScheduleTransferItem>? = null,
	val approvalList: List<ApprovalLineItem>? = null,
	val verifyQRCodeVo: VerifyQrCodeRequest? = null,
)

data class PayrollScheduleTransferUpdateResponse(
	val resultYn: String? = null,
	val approvalNo: Long? = null,
)

/**
 * Port of `PYR1108_Adapter_UpdatePayrollScheduleTransfer` — calls CBS opcode `CIB11300223`
 * (`DGBEBankingService.processCIB11300223`) and overwrites `resultYn` with "Y"/"N" based on the
 * response header's `result` flag.
 *
 * FLAGGED FOR REVIEW: the old adapter's *active* `process` method is this simple pass-through +
 * `resultYn` logic. But the file also contains a large commented-out (dead) alternate
 * implementation, structurally identical to the active code in `PYR1103_Adapter_RegisterPayrollPayment`
 * — i.e. a service-status blocking-time check (`ServiceStatusService.getBlockingTime()`) plus an
 * OTP verification round-trip (`processUSR0103` / `processSEC0004`) gating the actual
 * `processCIB11300223` call. That block is entirely `//`-commented in the old source (including
 * the `@Autowired private ServiceStatusService serviceStatus` field), so it does NOT run in the
 * old app today — this port therefore follows the *active* code, not the dead block.
 *
 * This is called out explicitly because the task brief for this batch listed
 * `PYR1108_Adapter_UpdatePayrollScheduleTransfer` alongside `PYR1103` as an adapter that "also
 * uses `ServiceStatusService`" — which is only true of the commented-out, non-executing code path.
 * If the intent is for this endpoint to eventually get the same OTP/service-status gating as
 * `PayrollPaymentRegisterSbc` (`app/cbs/payroll_payment_register`), that would be a deliberate
 * behavior change from the current old app, not a faithful port — please confirm before adding it.
 */
@RestController
@RequestMapping("/PYR1108")
class PayrollScheduleTransferUpdateCbc(
	private val payrollScheduleTransferUpdateSbc: PayrollScheduleTransferUpdateSbc,
) {
	@PostMapping
	fun update(
		@RequestBody request: RequestData<PayrollScheduleTransferUpdateRequest>,
	): ResponseData<PayrollScheduleTransferUpdateResponse> {
		return payrollScheduleTransferUpdateSbc.update(request)
	}
}
