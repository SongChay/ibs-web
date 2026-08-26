package kh.bank.dgb.ibs.app.cbs.multi_transfer_detail

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/**
 * Port of `TRS3201_Adapter_InquiryMultiTransferDetail#process`. Beyond the plain CBS pass-through,
 * the old adapter enriches every item with a status description derived from the old `DataUtils`
 * helper (not ported elsewhere, so the small lookup table it used is replicated locally below).
 */
@Service
class MultiTransferDetailSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<MultiTransferDetailRequest>): ResponseData<MultiTransferDetailResponse> {
		val response = coreBankingApiConnector.post("CIB11001501", request.header?.languageCode, request.body, MultiTransferDetailResponse::class.java)
		val enrichedList = response.body?.transferList?.map { item ->
			item.copy(transactionStatusDesc = transactionStatusDescription(item.transactionStatus))
		}
		return response.copy(body = response.body?.copy(transferList = enrichedList))
	}

	private fun transactionStatusDescription(code: String?): String {
		return when (code) {
			"001" -> "Processing"
			"002" -> "Failed"
			"003" -> "Completed"
			"000" -> "Unknown"
			else -> ""
		}
	}
}
