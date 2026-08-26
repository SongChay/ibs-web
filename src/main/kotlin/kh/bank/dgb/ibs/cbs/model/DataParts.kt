package kh.bank.dgb.ibs.cbs.model

/** Port of `DataHeader` — always `dataHdrTypeCd = "IO"` in practice (see `MaruMessageFactory`). */
data class DataHeader(
	var dataHdrTypeCd: String? = null,
)

/** Port of `DataAppendixHeader` — null unless `dataHdrTypeCd` is `"OD"`, which this app never
 *  sends (always `"IO"`). Kept for structural completeness. */
data class DataAppendixHeader(
	var outputSvcID: String? = null,
	var outputScreenID: String? = null,
	var releaseTypeCode: String? = null,
	var induceMessage: String? = null,
	var outputType: String? = null,
)

/**
 * Port of `DataList<MData>` — genericized over the actual payload type `T` instead of the old
 * app's `MData` (a hand-rolled `LinkedHashMap<String, Object>` with ~40 typed getter/setter
 * methods, `MMultiData` its list-of-rows sibling). Neither changes the wire JSON shape at all —
 * `{"dataAppdHdr": ..., "dataBody": {...}}` either way — Kotlin generics + Jackson just do the
 * same job type-safely, without ~800 lines of pre-generics-era boilerplate. See
 * `CoreBankingApiConnector` for where `T` gets bound per call.
 */
data class DataList<T>(
	var dataAppdHdr: DataAppendixHeader? = null,
	var dataBody: T,
)

/** Port of `MessageData<MData>` — see `DataList`. */
data class MessageData<T>(
	var dataHdr: DataHeader = DataHeader(),
	var dataList: List<DataList<T>> = emptyList(),
)
