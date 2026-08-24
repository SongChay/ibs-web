package kh.bank.dgb.ibs.common.exception

import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseResultCodeType
import kh.bank.dgb.ibs.common.envelope.ResponseResultUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Replaces the old app's two parallel, inconsistent error paths (`ErrorControllerAdvice` for
 * normal MVC dispatch vs. `MainController`'s own inline try/catch for adapter dispatch) with one
 * handler, now that every adapter is a real controller.
 *
 * Always returns HTTP 200 with `{"header": {result:false, resultCode, resultMessage}, "body": null}`
 * — confirmed decision: the old app never used HTTP status semantically (every handler in it
 * writes a 200 with the result conveyed in the body), and the existing client is assumed to key
 * off `header.resultCode` rather than the transport status. `IbsException.httpStatus` is kept on
 * the exception type for now (it's meaningful information) but intentionally unused here.
 *
 * Body stays in clear regardless — EncryptedEnvelopeFilter only encrypts when
 * `header.result == true`, so error bodies stay readable even on an encrypted-channel request,
 * matching the old MainController, which never encrypted a catch-block response either.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(IbsException::class)
	fun handleIbsException(ex: IbsException): ResponseEntity<ResponseData<Unit>> =
		respond(ex.resultCode)

	@ExceptionHandler(Exception::class)
	fun handleUnexpected(ex: Exception): ResponseEntity<ResponseData<Unit>> =
		respond(ResponseResultCodeType.UNKNOWN_ERROR)

	private fun respond(code: ResponseResultCodeType): ResponseEntity<ResponseData<Unit>> =
		ResponseEntity.status(HttpStatus.OK).body(ResponseData(header = ResponseResultUtils.makeResponse(false, code)))
}
