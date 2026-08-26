package kh.bank.dgb.ibs.app.cbs.corporate_payroll_retrieve

import com.fasterxml.jackson.annotation.JsonProperty
import kh.bank.dgb.ibs.common.envelope.RequestData
import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class CorporatePayrollRetrieveRequest(
	val customerNo: String? = null,
	val salaryTransferAccountNo: String? = null,
)

data class CorporatePayrollRetrieveItem(
	val phoneNo: String? = null,
	val emailAddress: String? = null,
	val applicationNo: Long? = null,
)

/** Old Vo has an asymmetric Jackson mapping: CBS's own response carries the list under `grid01`
 *  (that's what the connector must deserialize), but the field is re-exposed to the client under
 *  `retrieveCorporatePayroll` (`@JsonGetter("retrieveCorporatePayroll")` / `@JsonSetter("grid01")`
 *  in the old `PYR1001_RES_WrapperRetrieveCorporatePayrollVo`). Replicated here the same way. */
data class CorporatePayrollRetrieveResponse(
	val salaryRegisterStatusCode: String? = null,
	val registerDate: String? = null,
	val unregisterDate: String? = null,
	val salaryTransferAccountNo: String? = null,
	val salaryTransferFeeStatusCode: String? = null,
	val salaryTransferFeeAmount: BigDecimal? = null,
	val currencyCode: String? = null,
	val registerReasonDesc: String? = null,
	val terminationReason: String? = null,
	val unregisterBranchCode: String? = null,
	val grid01Count: String? = null,
	@param:JsonProperty("grid01")
	@get:JsonProperty("retrieveCorporatePayroll")
	val retrieveCorporatePayroll: List<CorporatePayrollRetrieveItem>? = null,
)

/** Port of `PYR1001_Adapter_RetrieveCorporatePayroll` — calls CBS opcode `CIB11300111`. Also
 *  replicates the old adapter's post-processing: formats `registerDate`/`unregisterDate` from
 *  `yyyyMMdd` to `dd MMM yyyy`, and rescales `salaryTransferFeeAmount` to 2 decimal places. */
@RestController
@RequestMapping("/PYR1001")
class CorporatePayrollRetrieveCbc(
	private val corporatePayrollRetrieveSbc: CorporatePayrollRetrieveSbc,
) {
	@PostMapping
	fun retrieve(@RequestBody request: RequestData<CorporatePayrollRetrieveRequest>): ResponseData<CorporatePayrollRetrieveResponse> {
		return corporatePayrollRetrieveSbc.retrieve(request)
	}
}
