package kh.bank.dgb.ibs.common.envelope

/**
 * Port of the old `RequestUserHeaderVo` (bizmob.corpbanking.web.rest.vo.request) — travels in
 * clear on every request, even encrypted ones (only "body" gets AES-encrypted, see
 * EncryptedEnvelopeFilter). Field names/casing kept exactly as the wire format expects
 * ("userID", not "userId").
 */
data class RequestUserHeaderVo(
	val previousTransactionId: String? = null,
	val previousTransactionDate: String? = null,
	val languageCode: String? = null,
	val userID: String? = null,
	val customerNo: String? = null,
	val channelTypeCode: String? = null,
)
