package kh.bank.dgb.ibs.app.local.document_upload

import kh.bank.dgb.ibs.common.envelope.ResponseData
import kh.bank.dgb.ibs.common.envelope.ResponseUserHeaderVo
import org.slf4j.LoggerFactory
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

/** Port of the per-file entry inside the old `fileInformation` JSON text part CBS/MCI expects. */
private data class MciFileEntry(
	val orgFilename: String,
	val targetFilepath: String,
	val targetFilename: String,
)

private data class MciFileInformation(
	val targetSystem: String,
	val files: List<MciFileEntry>,
)

/**
 * Port of `DocumentUploadController.handleFileUpload`. Builds the same multipart request shape the
 * old code built by hand with Apache HttpClient's `MultipartEntityBuilder` (binary `userFile` parts
 * + a `fileInformation` JSON text part) — done here via Spring's own `MultipartBodyBuilder`/
 * `RestClient` (matching how every other HTTP integration in this app was ported off Apache
 * HttpClient/RestTemplate), with the JSON part built from a real data class instead of manual
 * string concatenation. Same output for any normal filename; strictly more robust (correct
 * escaping) if a filename ever contained a character that would have broken the old hand-built
 * JSON string.
 *
 * UNVERIFIED — this external "MCI" service isn't reachable from this environment, same as CBS and
 * CBS's own RSA endpoint.
 */
@Service
class DocumentUploadSbc(
	private val restClient: RestClient,
	private val mciFileUploadProperties: MciFileUploadProperties,
) {
	private val logger = LoggerFactory.getLogger(DocumentUploadSbc::class.java)

	fun upload(files: List<MultipartFile>): ResponseData<DocumentUploadResponse> {
		val nonEmpty = files.filterNot { it.isEmpty }
		if (nonEmpty.isEmpty()) {
			return failureResponse()
		}

		val entries = nonEmpty.map { file -> file to planFor(file) }

		val multipartBody = MultipartBodyBuilder().apply {
			entries.forEach { (file, _) -> part("userFile", file.resource) }
			part("fileInformation", MciFileInformation(targetSystem = "CBS", files = entries.map { it.second.entry }))
		}.build()

		return try {
			restClient.post()
				.uri(mciFileUploadProperties.uploadUrl)
				.body(multipartBody)
				.retrieve()
				.toBodilessEntity()

			ResponseData(
				header = ResponseUserHeaderVo(result = true, resultCode = RESPONSE_SUCCESS_CODE, resultMessage = RESPONSE_SUCCESS_MESSAGE),
				body = DocumentUploadResponse(fileList = entries.map { it.second.info }),
			)
		} catch (e: RestClientException) {
			logger.error("MCI file upload failed", e)
			failureResponse()
		}
	}

	private data class Planned(val entry: MciFileEntry, val info: DocumentUploadFileInfo)

	private fun planFor(file: MultipartFile): Planned {
		val originalName = file.originalFilename ?: ""
		val dotIndex = originalName.lastIndexOf('.')
		val hasExt = dotIndex > 0
		val newFileName = UUID.randomUUID().toString()
		val targetFilename = if (hasExt) "$newFileName.${originalName.substring(dotIndex + 1)}" else newFileName
		val fileExtension = if (hasExt) originalName.substring(dotIndex + 1) else ""

		return Planned(
			entry = MciFileEntry(orgFilename = originalName, targetFilepath = TARGET_PATH, targetFilename = targetFilename),
			info = DocumentUploadFileInfo(
				fileName = targetFilename,
				filePath = TARGET_PATH,
				originalFileName = originalName,
				fileExtension = fileExtension,
			),
		)
	}

	private fun failureResponse(): ResponseData<DocumentUploadResponse> {
		return ResponseData(
			header = ResponseUserHeaderVo(result = false, resultCode = RESPONSE_FAIL_CODE, resultMessage = RESPONSE_FAIL_MESSAGE),
			body = DocumentUploadResponse(fileList = emptyList()),
		)
	}

	companion object {
		private const val TARGET_PATH = "/CCI/send/"

		// Port of `BizResultCodeType.RESPONSE_SUCCESS_CODE`/`RESPONSE_FAIL_CODE` — deliberately NOT
		// this app's own `ResponseResultCodeType` enum ("CBK_0000"/"CBK_0001"): the old
		// `DocumentUploadController` used its own separate, older code convention ("0000"/"0001"),
		// and the real client may specifically check for these exact values on this one endpoint.
		private const val RESPONSE_SUCCESS_CODE = "0000"
		private const val RESPONSE_SUCCESS_MESSAGE = "Success"
		private const val RESPONSE_FAIL_CODE = "0001"
		private const val RESPONSE_FAIL_MESSAGE = "Fail"
	}
}
