package kh.bank.dgb.ibs.cbs.model

/**
 * Ports of `ApprovalHeader`/`ApprovalBody`/`ApprovalReason`/`ManagerApproval` (표준전문책임자승인부 —
 * the manager-approval section of a Maru envelope). Always null/unused in the old app's outgoing
 * requests (`apprHdr`/`apprBody` on `MaruMessage`) — kept here for structural completeness per
 * the full-port decision, not because anything currently populates them.
 */
data class ApprovalHeader(
	var dataHdrTypeCd: String = "",
)

data class ApprovalBody(
	var acctNo: String? = null,
	var trmnCustNm: String? = null,
	var trxAmt: String? = null,
	var mgrApprRsnCdCnt: Long? = null,
	var mgrApprRsnCd: String? = null,
	var mgrApprDesc: String? = null,
	var mgrApprTypeCd: String? = null,
	var mgrApprLstCnt: Long? = null,
	var apprRsnLst: List<ApprovalReason> = emptyList(),
	var mgrApprLst: List<ManagerApproval> = emptyList(),
)

data class ApprovalReason(
	var mgrApprRsnCd: String? = null,
	var mgrApprDesc: String? = null,
	var mgrApprTypeCd: String? = null,
)

data class ManagerApproval(
	var mgrApprEmpNo: String? = null,
	var mgrApprEmpNm: String? = null,
	var mgrApprBrchCd: String? = null,
	var mgrApprBrchNm: String? = null,
	var mgrApprJobCd: String? = null,
	var mgrApprLoginIP: String? = null,
	var mgrApprLoginStatusCd: String? = null,
	var mgrApprAbsentYN: String? = null,
	var mgrApprActApprTellerID: String? = null,
	var brchMgrYN: String? = null,
	var auditYN: String? = null,
)
