package kh.bank.dgb.ibs.app.cbs.edc_consumer_inquiry

import kh.bank.dgb.ibs.cbs.CoreBankingApiConnector
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.stereotype.Service

@Service
class EdcConsumerInquirySbc(
	private val connector: CoreBankingApiConnector,
) {
	fun inquire(request: RequestData<EdcConsumerInquiryRequest>): ResponseData<EdcConsumerInquiryResponse> =
		connector.post("CIB11102511", request.header?.languageCode, request.body, EdcConsumerInquiryResponse::class.java)
}
