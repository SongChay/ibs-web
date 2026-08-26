package kh.bank.dgb.ibs.app.local.document_upload

import kh.bank.dgb.ibs.common.envelope.ResponseData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

data class DocumentUploadFileInfo(
	val fileName: String? = null,
	val filePath: String? = null,
	val originalFileName: String? = null,
	val fileExtension: String? = null,
)

data class DocumentUploadResponse(
	val fileList: List<DocumentUploadFileInfo>? = null,
)

/**
 * Port of `DocumentUploadController.handleFileUpload` (`/upload/docfile`) — a raw multipart-upload
 * endpoint, not a `{header,body}`-enveloped adapter (matching `ResourceFileInfoCbc`'s upload
 * endpoint). Proxies uploaded files straight through to an external, non-CBS "MCI" file-upload
 * service (see `MciFileUploadProperties`) — the only feature in this app that talks to something
 * other than CBS or its own Postgres schema.
 */
@RestController
class DocumentUploadCbc(
	private val documentUploadSbc: DocumentUploadSbc,
) {
	@PostMapping("/upload/docfile")
	fun upload(@RequestParam("files") files: Array<MultipartFile>): ResponseData<DocumentUploadResponse> {
		return documentUploadSbc.upload(files.toList())
	}

}
