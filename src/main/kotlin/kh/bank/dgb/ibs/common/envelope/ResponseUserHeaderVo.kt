package kh.bank.dgb.ibs.common.envelope

/**
 * Port of the old `ResponseUserHeaderVo` — the result envelope every existing client already
 * parses (`header.resultCode` etc). Kept as the response header for all 143 ported adapters per
 * the "existing client can't change" decision; genuinely new endpoints added later are free to
 * skip this and return plain DTOs + HTTP status instead.
 */
data class ResponseUserHeaderVo(
	val result: Boolean? = null,
	val resultCode: String? = null,
	val resultMessage: String? = null,
	val transactionID: String? = null,
	val transactionDate: String? = null,
)
