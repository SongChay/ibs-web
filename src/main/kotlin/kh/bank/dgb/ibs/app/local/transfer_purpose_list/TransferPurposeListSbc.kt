package kh.bank.dgb.ibs.app.local.transfer_purpose_list

import kh.bank.dgb.ibs.app.local.prepare_document.PrepareDocumentRbc
import kh.bank.dgb.ibs.app.local.purpose_of_transfer.PurposeOfTransferRbc
import kh.bank.dgb.ibs.app.local.remark_document.RemarkDocumentRbc
import org.springframework.stereotype.Service

@Service
class TransferPurposeListSbc(
	private val purposeOfTransferRbc: PurposeOfTransferRbc,
	private val prepareDocumentRbc: PrepareDocumentRbc,
	private val remarkDocumentRbc: RemarkDocumentRbc,
) {

	fun getList(): List<TransferPurposeItem> {
		return purposeOfTransferRbc.getPurposeOfTransferList().map { purpose ->
			TransferPurposeItem(
				code = purpose.code,
				item = purpose.item,
				item1 = purpose.item1,
				prepareDocument = prepareDocumentRbc.getPrepareDocumentList(purpose.code).map { DocumentItem(it.code, it.item) },
				remarkDocument = remarkDocumentRbc.getRemarkDocumentList(purpose.code).map { DocumentItem(it.code, it.item) },
			)
		}
	}
}
