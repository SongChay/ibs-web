package kh.bank.dgb.ibs.app.local.prepare_document

import org.apache.ibatis.annotations.Mapper

/** Port of `PrepareDocumentDTO` — a code/item lookup table (documents required to prepare for
 *  some transaction type, keyed by `parentCode`). */
data class PrepareDocument(
	val code: String,
	val parentCode: String? = null,
	val item: String? = null,
)

/** Port of `PrepareDocumentDAO`. */
@Mapper
interface PrepareDocumentRbc {
	fun getPrepareDocumentList(parentCode: String): List<PrepareDocument>
}
