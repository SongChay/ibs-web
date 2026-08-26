package kh.bank.dgb.ibs.cbs.model

/**
 * Port of `MaruMessage` — the full standard-message envelope (표준전문) every core-banking call is
 * wrapped in. `msgHdr`/`apprHdr`/`apprBody` stay null on outgoing requests, matching the old app;
 * `msgBody` is populated when parsing a *response* (see `CoreBankingApiConnector`). Its
 * header/common/message/approval/data sections each live in their own file — `CommonHeader.kt`,
 * `CommonBody.kt`, `MessageParts.kt`, `ApprovalParts.kt`, `DataParts.kt` — this file holds only the
 * envelope itself.
 */
data class MaruMessage<T>(
	var commonHdr: CommonHeader = CommonHeader(),
	var commonBody: CommonBody = CommonBody(),
	var msgHdr: MessageHeader? = null,
	var msgBody: MessageBody? = null,
	var apprHdr: ApprovalHeader? = null,
	var apprBody: ApprovalBody? = null,
	var msgData: MessageData<T> = MessageData(),
)
