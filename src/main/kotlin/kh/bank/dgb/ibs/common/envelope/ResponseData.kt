package kh.bank.dgb.ibs.common.envelope

/** Port of `ResponseData<T>` — the outer `{"header": ..., "body": ...}` response envelope. */
data class ResponseData<T>(
	val header: ResponseUserHeaderVo? = null,
	val body: T? = null,
)
