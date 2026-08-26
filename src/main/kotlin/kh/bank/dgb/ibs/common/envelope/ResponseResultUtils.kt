package kh.bank.dgb.ibs.common.envelope

/**
 * Port of `ResponseResultUtils` — builders for the response header. Dropped the old
 * `@Autowired static MessageSource` field (static-field injection; the one call site that would
 * have used it for localized messages was commented-out dead code) — result-message i18n is
 * tracked separately as its own open item, not solved here.
 */
object ResponseResultUtils {

	fun makeResponse(result: Boolean, resultCode: String, resultMessage: String): ResponseUserHeaderVo {
		return ResponseUserHeaderVo(result = result, resultCode = resultCode, resultMessage = resultMessage)
	}

	fun makeResponse(
		result: Boolean,
		resultCode: String,
		resultMessage: String,
		transactionId: String,
		transactionDate: String,
	): ResponseUserHeaderVo {
		return ResponseUserHeaderVo(
			result = result,
			resultCode = resultCode,
			resultMessage = resultMessage,
			transactionID = transactionId,
			transactionDate = transactionDate,
		)
	}

	fun makeResponse(result: Boolean, code: ResponseResultCodeType): ResponseUserHeaderVo {
		return makeResponse(result, code.value, code.description)
	}

	fun makeResponse(
		result: Boolean,
		code: ResponseResultCodeType,
		transactionId: String,
		transactionDate: String,
	): ResponseUserHeaderVo {
		return makeResponse(result, code.value, code.description, transactionId, transactionDate)
	}
}
