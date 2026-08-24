package kh.bank.dgb.ibs.cbs.model

/**
 * Ports of `MessageHeader`/`MessageBody`/`ErrorOccur` (표준전문메시지부 — the "message" section of a
 * Maru envelope). The old app never populates these on outgoing requests (`msgHdr`/`msgBody`
 * always null in `MaruMessage`), but `MessageBody`'s fields — `outMsgCd`, `outMsgDesc` — are read
 * directly off every *response*, so they're real, not dead weight.
 */
data class MessageHeader(
	var msgHdrTypeCd: String? = null,
)

data class MessageBody(
	var outAttrCd: String? = null,
	var outMsgCd: String? = null,
	var outMsgCtnt: String? = null,
	var outMsgDesc: String? = null,
	var errCorrActCtnt: String? = null,
	var errOwnNm: String? = null,
	var errOwnContactNo: String? = null,
	var outMsgCnt: Long? = null,
	var errorOccurList: List<ErrorOccur> = emptyList(),
)

data class ErrorOccur(
	var errLoc: String? = null,
	var errScnItemNm: String? = null,
)
