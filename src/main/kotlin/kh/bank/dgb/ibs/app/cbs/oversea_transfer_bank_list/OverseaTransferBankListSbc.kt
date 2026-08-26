package kh.bank.dgb.ibs.app.cbs.oversea_transfer_bank_list

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

/**
 * Port of `TRS4003_Adapter_InquiryBankList.process(...)`. Beyond the plain CBS pass-through, the
 * old adapter also force-set `requestVO.getBody().setChannelTypeCode(CHANNEL_TYPE_CODE_CORP_BANKING)`
 * ("01") on the request body before calling CBS — replicated here (the old header-level
 * `channelTypeCode` set on every adapter is NOT replicated: the new connector already stamps its
 * own configured channel type code onto every CBS call, see `DefaultCoreBankingApiConnector`).
 */
@Service
class OverseaTransferBankListSbc(
	private val coreBankingApiConnector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<OverseaTransferBankListRequest>): ResponseData<OverseaTransferBankListResponse> {
		val body = (request.body ?: OverseaTransferBankListRequest()).copy(channelTypeCode = CHANNEL_TYPE_CODE_CORP_BANKING)
		return coreBankingApiConnector.post("CIB11300311", request.header?.languageCode, body, OverseaTransferBankListResponse::class.java)
	}

	companion object {
		private const val CHANNEL_TYPE_CODE_CORP_BANKING = "01"
	}
}
