package kh.bank.dgb.ibs.app.local.account_inquiry

import org.apache.ibatis.annotations.Mapper
import java.math.BigDecimal

/**
 * Template feature slice — Repository/DAO (`Rbc` suffix).
 *
 * MyBatis mapper interface — one per feature, matching XML at
 * `src/main/resources/mapper/AccountInquiryRbc.xml`. `@MapperScan("kh.bank.dgb.ibs.app")` in
 * IbsApplication.kt picks these up regardless of which feature folder they live in.
 */
@Mapper
interface AccountInquiryRbc {

	fun findBalanceByAccountNo(accountNo: String): BigDecimal
}
