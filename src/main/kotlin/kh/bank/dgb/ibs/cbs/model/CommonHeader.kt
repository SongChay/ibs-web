package kh.bank.dgb.ibs.cbs.model

/**
 * Port of `CommonHeader` (bizmob.corpbanking.ebanking.model) — the "표준전문헤더부·헤더부" (standard
 * message header) of the Maru core-banking protocol. Field names kept exactly as the old app had
 * them; this is a wire contract with an external system we don't control, not free to rename.
 */
data class CommonHeader(
	var encProcTypeCd: String = "",
	var guid: String = "",
	var msgReqResTypeCd: String = "",
	var msgVerNo: String = "",
	var firstIPAddr: String = "",
	var firstReqSysCd: String = "",
	var chnSysCd: String = "",
	var mciNodeNo: String = "",
	var sessionID: String = "",
	var eaiNodeNo: String = "",
	var eaiSessionID: String = "",
	var fepNodeNo: String = "",
	var fepSessionID: String = "",
	var extReqSysCd: String = "",
	var totTimeoutSec: String = "",
	var trxTimeoutSec: String = "",
)
