package kh.bank.dgb.ibs.cbs

import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

data class TestRequestBody(val accountNo: String)
data class TestResponseBody(val balance: String)

/**
 * No live CBS endpoint exists to test against, so this stands one up locally (JDK's built-in
 * HttpServer, no extra test dependency) to prove the actual marshalling/parsing logic is
 * correct — the request really is a well-formed Maru envelope, and the response-parsing path
 * (msgBody.outMsgCd/outMsgDesc, commonBody.trxDate, msgData.dataList[0].dataBody) really does
 * produce the right ResponseData. This does NOT verify anything about the real CBS's actual
 * behavior — only that this connector's side of the contract is implemented as read from the
 * old code.
 */
class DefaultCoreBankingApiConnectorTest {

	private lateinit var server: HttpServer
	private lateinit var connector: DefaultCoreBankingApiConnector
	private var capturedRequestBody: String? = null

	@BeforeEach
	fun startFakeCbs() {
		server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
		server.createContext("/ONLWeb/HttpCsbSyncAdapter") { exchange ->
			capturedRequestBody = exchange.requestBody.readBytes().toString(StandardCharsets.UTF_8)

			val responseJson = """
				{
					"commonHdr": {"guid": "test-guid"},
					"commonBody": {"trxDate": "20260824"},
					"msgBody": {"outMsgCd": "100000", "outMsgDesc": "Success"},
					"msgData": {"dataList": [{"dataBody": {"balance": "1234.56"}}]}
				}
			""".trimIndent().toByteArray(StandardCharsets.UTF_8)

			exchange.responseHeaders.add("Content-Type", "application/json")
			exchange.sendResponseHeaders(200, responseJson.size.toLong())
			exchange.responseBody.use { it.write(responseJson) }
		}
		server.start()

		val baseUrl = "http://127.0.0.1:${server.address.port}/ONLWeb/HttpCsbSyncAdapter"
		val objectMapper = JsonMapper.builder().addModule(KotlinModule.Builder().build()).build()
		val restClient = RestClient.builder().build()
		val props = CoreBankingProperties(baseUrl = baseUrl)

		connector = DefaultCoreBankingApiConnector(restClient, objectMapper, props)
	}

	@AfterEach
	fun stopFakeCbs() {
		server.stop(0)
	}

	@Test
	fun `builds a well-formed Maru request and parses a successful response`() {
		val result = connector.post("ABM1001", "01", TestRequestBody(accountNo = "123-456"), TestResponseBody::class.java)

		// --- Request side: confirm the envelope is well-formed ---
		val sentJson = requireNotNull(capturedRequestBody)
		assertTrue(sentJson.contains("\"svcID\":\"ABM1001\""), "commonBody.svcID should be the operation code")
		assertTrue(sentJson.contains("\"ifID\":\"ABM1001CSB\""), "ifID should be operationCode + systemCode")
		assertTrue(sentJson.contains("\"accountNo\":\"123-456\""), "request payload should be nested under dataBody")
		assertTrue(sentJson.contains("\"dataHdrTypeCd\":\"IO\""), "dataHdr should always be IO")
		assertTrue(sentJson.contains("\"langTypeCd\":\"EN\""), "languageCode \"01\" should resolve to EN")

		// --- Response side: confirm parsing produced the right ResponseData ---
		assertEquals(true, result.header?.result)
		assertEquals("100000", result.header?.resultCode)
		assertEquals("Success (100000)", result.header?.resultMessage)
		assertEquals("20260824", result.header?.transactionID)
		assertEquals("1234.56", result.body?.balance)
	}

	@Test
	fun `resolves Khmer language code correctly`() {
		connector.post("ABM1001", "km", TestRequestBody(accountNo = "1"), TestResponseBody::class.java)

		val sentJson = requireNotNull(capturedRequestBody)
		assertTrue(sentJson.contains("\"langTypeCd\":\"KM\""))
	}
}
