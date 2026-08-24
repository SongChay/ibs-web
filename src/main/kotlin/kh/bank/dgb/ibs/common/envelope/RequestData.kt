package kh.bank.dgb.ibs.common.envelope

/** Port of `RequestData<T>` — the outer `{"header": ..., "body": ...}` request envelope. */
data class RequestData<T>(
	val header: RequestUserHeaderVo? = null,
	val body: T? = null,
)
