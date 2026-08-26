package kh.bank.dgb.ibs.app.cbs.payroll_payment_detail

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class PayrollPaymentDetailSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun detail(request: RequestData<PayrollPaymentDetailRequest>): ResponseData<PayrollPaymentDetailResponse> {
		val result = coreBankingApiConnector.post("CIB11300912", request.header?.languageCode, request.body, PayrollPaymentDetailResponse::class.java)
		val body = result.body ?: return result
		val group = body.wrapperRetrievePayrollPaymentDetail ?: return result

		val updatedGroup = group.copy(
			retrievePayrollPaymentDetail = group.retrievePayrollPaymentDetail?.map { item ->
				item.copy(
					receiptAmount = item.receiptAmount?.setScale(2),
					feeAmount = item.feeAmount?.setScale(2),
					transactionStatusDesc = transactionStatusDescription(item.transactionStatus),
				)
			},
		)
		return ResponseData(header = result.header, body = body.copy(wrapperRetrievePayrollPaymentDetail = updatedGroup))
	}

	/** Port of `DataUtils.getTransactionStatusDescription`, backed by the old
	 *  `TransactionStatusTypeCode` enum. */
	private fun transactionStatusDescription(transactionStatus: String?): String {
		return when (transactionStatus) {
			"001" -> "Processing"
			"002" -> "Failed"
			"003" -> "Completed"
			"000" -> "Unknown"
			else -> ""
		}
	}
}
