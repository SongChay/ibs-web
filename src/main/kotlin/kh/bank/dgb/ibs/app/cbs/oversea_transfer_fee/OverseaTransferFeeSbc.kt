package kh.bank.dgb.ibs.app.cbs.oversea_transfer_fee

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * Port of the body of `TRS4001_Adapter_InquiryOverseaTransferFee.process(...)`.
 *
 * Old logic (replicated exactly):
 *  1. Call CBS with `feeTypeCode = OUT_BOUND (32)`.
 *  2. Call CBS again with `feeTypeCode = CABLE (33)`.
 *  3. (An in-bound call with `feeTypeCode = IN_BOUND (31)` was already commented out in the old
 *     code — `inBoundFee` was hard-coded to `0.0` there too. Preserved as-is, not revived.)
 *  4. Response header comes from the CABLE call.
 *  5. Response body is only built if BOTH the out-bound and cable calls returned a body:
 *     `outBoundFee`/`cableFee` copied from each call's `transferFee`, `feeCurrencyCode`/`resultYn`
 *     taken from the CABLE call, and `transferFee` = outBoundFee + cableFee (BigDecimal sum).
 *     If either call came back bodyless, the merged body is null (header is still returned).
 */
@Service
class OverseaTransferFeeSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<OverseaTransferFeeRequest>): ResponseData<OverseaTransferFeeResponse> {
		val body = request.body ?: OverseaTransferFeeRequest()
		val languageCode = request.header?.languageCode

		val outBoundResult = coreBankingApiConnector.post(
			OPCODE,
			languageCode,
			body.copy(feeTypeCode = FEE_TYPE_OUT_BOUND),
			OverseaTransferFeeResponse::class.java,
		)
		val cableResult = coreBankingApiConnector.post(
			OPCODE,
			languageCode,
			body.copy(feeTypeCode = FEE_TYPE_CABLE),
			OverseaTransferFeeResponse::class.java,
		)

		val outBoundBody = outBoundResult.body
		val cableBody = cableResult.body

		val mergedBody = if (outBoundBody != null && cableBody != null) {
			val outBoundFee = outBoundBody.transferFee ?: BigDecimal.ZERO
			val cableFee = cableBody.transferFee ?: BigDecimal.ZERO
			OverseaTransferFeeResponse(
				inBoundFee = 0.0,
				outBoundFee = outBoundFee.toDouble(),
				cableFee = cableFee.toDouble(),
				feeCurrencyCode = cableBody.feeCurrencyCode,
				resultYn = cableBody.resultYn,
				transferFee = outBoundFee.add(cableFee),
			)
		} else {
			null
		}

		return ResponseData(header = cableResult.header, body = mergedBody)
	}

	companion object {
		private const val OPCODE = "CIB11001611"
		private const val FEE_TYPE_OUT_BOUND = 32
		private const val FEE_TYPE_CABLE = 33
	}
}
