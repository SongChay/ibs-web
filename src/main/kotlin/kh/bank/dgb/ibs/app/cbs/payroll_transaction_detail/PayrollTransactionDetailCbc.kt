package kh.bank.dgb.ibs.app.cbs.payroll_transaction_detail

import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class PayrollTransactionDetailRequest(
	val approvalNo: Long? = null,
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
	val salaryTransferExecutionDate: String? = null,
	val acceptanceSeqNo: Long? = null,
)

data class PayrollTransactionDetailItem(
	val recipientAccountNo: String? = null,
	val recipientAccountName: String? = null,
	val currencyCode: String? = null,
	val transferAmount: BigDecimal? = null,
)

/** Old `PYR1203_RES_WrapperRetrievePayrollTransactionDetailVo` — no Jackson renaming, `grid01`
 *  field name kept as-is (unlike the other PYRxxxx wrappers in this batch). */
data class PayrollTransactionDetailResponse(
	val approvalNo: Long? = null,
	val accountNo: String? = null,
	val accountName: String? = null,
	val currencyCode: String? = null,
	val totalAmount: BigDecimal? = null,
	val totalFee: BigDecimal? = null,
	val totalCount: Long? = null,
	val transferDate: String? = null,
	val transferTime: String? = null,
	val narration: String? = null,
	val grid01Count: Long? = null,
	val grid01: List<PayrollTransactionDetailItem>? = null,
)

/** Port of `PYR1203_Adapter_RetrievePayrollTransactionDetail` — calls CBS opcode `CIB11300915`.
 *  Also replicates the old adapter's post-processing: rescales `totalAmount`/`totalFee` to 2
 *  decimal places, formats `transferDate` (`yyyyMMdd` -> `dd MMM yyyy`), and rescales each
 *  `grid01` item's `transferAmount` to 2 decimal places. */
@RestController
@RequestMapping("/PYR1203")
class PayrollTransactionDetailCbc(
	private val payrollTransactionDetailSbc: PayrollTransactionDetailSbc,
) {
	@PostMapping
	fun detail(@RequestBody request: RequestData<PayrollTransactionDetailRequest>): ResponseData<PayrollTransactionDetailResponse> {
		return payrollTransactionDetailSbc.detail(request)
	}
}
