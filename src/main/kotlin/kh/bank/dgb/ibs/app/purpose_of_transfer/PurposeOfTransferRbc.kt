package kh.bank.dgb.ibs.app.purpose_of_transfer

import org.apache.ibatis.annotations.Mapper

/** Port of `PurposeTransferDTO` — the fixed code/item list shown when a user picks a "purpose of
 *  transfer" on a transfer form. */
data class PurposeOfTransfer(
	val code: String,
	val parentCode: String? = null,
	val item: String? = null,
	val item1: String? = null,
)

/** Port of `PurposeTransferDAO`. */
@Mapper
interface PurposeOfTransferRbc {
	fun getPurposeOfTransferList(): List<PurposeOfTransfer>
}
