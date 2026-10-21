package thor.bridge_tournament

import com.jayway.jsonpath.JsonPath
import java.util.UUID
import java.util.function.Supplier
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import thor.bridge_tournament.core.port.dto.board.PairBoardResult
import thor.bridge_tournament.core.port.dto.board.RawBoardEntry
import thor.bridge_tournament.core.port.input.BoardEntryService

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
internal class ProtocolApiTests {
    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    private lateinit var jdbc: JdbcTemplate

    @Autowired
    private lateinit var boardEntryService: BoardEntryService

    @BeforeEach
    fun clearDatabase() {
        jdbc.execute("TRUNCATE TABLE board_entries, boards, users RESTART IDENTITY CASCADE")
    }

    @Test
    @Throws(Exception::class)
    fun createsReadsAndSearchesUsersByExactStrings() {
        // Arrange
        val id = user("alice")

        // Act
        val byId: ResultActions = mvc.perform(apiGet("/api/users/{id}", id))
        val bySurname: ResultActions = mvc.perform(apiGet("/api/users/by-surname").param("surname", "Example"))
        val lowercaseSurname: ResultActions = mvc.perform(apiGet("/api/users/by-surname").param("surname", "example"))
        val spacedSurname: ResultActions = mvc.perform(apiGet("/api/users/by-surname").param("surname", " Example "))
        val byUsername: ResultActions = mvc.perform(apiGet("/api/users/by-username").param("username", "alice"))
        val uppercaseUsername: ResultActions = mvc.perform(apiGet("/api/users/by-username").param("username", "ALICE"))
        val spacedUsername: ResultActions = mvc.perform(apiGet("/api/users/by-username").param("username", " alice "))
        val all: ResultActions = mvc.perform(apiGet("/api/users"))

        // Assert
        byId.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.sportCategory").value(5.0))
        bySurname.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(id))
        lowercaseSurname.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
            MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                "$",
                Matchers.hasSize<Any>(0)
            )
        )
        spacedSurname.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
            MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                "$",
                Matchers.hasSize<Any>(0)
            )
        )
        byUsername.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(id))
        uppercaseUsername.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
            MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                "$",
                Matchers.hasSize<Any>(0)
            )
        )
        spacedUsername.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
            MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                "$",
                Matchers.hasSize<Any>(0)
            )
        )
        all.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
            MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                "$",
                Matchers.hasSize<Any>(1)
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun createsBoardAndDerivesDealerAndVulnerability() {
        // Arrange
        val id = board(7)

        // Act
        val byId: ResultActions = mvc.perform(apiGet("/api/boards/{id}", id))
        val all: ResultActions = mvc.perform(apiGet("/api/boards"))

        // Assert
        byId.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.number").value(7))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dealer").value("s"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.vulnerable").value("ALL"))
        all.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
            MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                "$",
                Matchers.hasSize<Any>(1)
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun savesMpProtocolAndCorrectsAuthorsExistingEntryWithoutDuplicatingIt() {
        // Arrange
        val first = user("first")
        val second = user("second")
        val board = board(1)

        // Act
        val original: ResultActions = mvc.perform(entryRequest(board, first, "mp", entry("4S", "N", 1)))
        val entryId: String? = JsonPath.read<String>(
            original.andReturn().getResponse().getContentAsString(),
            "$.entryId"
        )
        val another: ResultActions = mvc.perform(entryRequest(board, second, "mp", entry("3S", "N", 1)))
        val corrected: ResultActions = mvc.perform(entryRequest(board, first, "mp", entry("pass", null, 0)))
        val protocol: ResultActions = mvc.perform(apiGet("/api/boards/{board}/entries", board))

        // Assert
        original.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(450))
            .andExpect(MockMvcResultMatchers.jsonPath("$.duplicatePoints").value(50.0))
        another.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(170))
            .andExpect(MockMvcResultMatchers.jsonPath("$.duplicatePoints").value(0.0))
            .andExpect(
                MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                    "$.protocol",
                    Matchers.hasSize<Any>(2)
                )
            )
        corrected.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.entryId").value(entryId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(0))
            .andExpect(
                MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                    "$.protocol",
                    Matchers.hasSize<Any>(2)
                )
            )
        protocol.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                    "$",
                    Matchers.hasSize<Any>(2)
                )
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath<Iterable<Int>>(
                    "$[*].points",
                    Matchers.hasItem<Int>(170)
                )
            )
        Assertions.assertEquals(
            2,
            jdbc.queryForObject<Int>("SELECT count(*) FROM board_entries", Int::class.java)
        )
    }

    @Test
    @Throws(Exception::class)
    fun calculatesImpsAcrossHttpAndPostgres() {
        // Arrange
        val first = user("first")
        val second = user("second")
        val board = board(1)
        mvc.perform(entryRequest(board, first, "imps", entry("4S", "N", 1)))
            .andExpect(MockMvcResultMatchers.status().isOk())

        // Act
        val result: ResultActions = mvc.perform(entryRequest(board, second, "imps", entry("3S", "N", 1)))

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(170))
            .andExpect(MockMvcResultMatchers.jsonPath("$.duplicatePoints").value(-4.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.countType").value("IMP"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["mp", "imps"])
    @Throws(Exception::class)
    fun acceptsLowercaseEntryFieldsThroughRest(mode: String?) {
        // Arrange
        val writer = user("writer")
        val board = board(1)
        val request: MockHttpServletRequestBuilder = entryRequest(
            board, writer, mode,
            "{\"contract\":\"4s\",\"declarer\":\"n\",\"lead\":\"ck\",\"result\":0}"
        )

        // Act
        val result: ResultActions = mvc.perform(request)

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.points").value(420))
            .andExpect(MockMvcResultMatchers.jsonPath("$.protocol[0].entry.contract").value("4S"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.protocol[0].entry.declarer").value("N"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.protocol[0].entry.lead").value("cK"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["N", "n"])
    @Throws(Exception::class)
    fun acceptsBothCasesThroughTheInputPort(declarer: String?) {
        // Arrange
        val writer = user("writer")
        val board = board(1)
        val entry: RawBoardEntry = RawBoardEntry("4s", declarer, "ck", 0)

        // Act
        val result: PairBoardResult = boardEntryService.addBoardEntryMp(UUID.fromString(writer), board, entry)

        // Assert
        Assertions.assertEquals(420, result.points)
        Assertions.assertEquals("N", result.protocol.keys.iterator().next().declarer)
        Assertions.assertEquals("cK", result.protocol.keys.iterator().next().lead)
        Assertions.assertEquals(
            1,
            jdbc.queryForObject<Int>("SELECT count(*) FROM board_entries", Int::class.java)
        )
    }

    @Test
    @Throws(Exception::class)
    fun readsCalculatedPointsFromStoredProtocolAndKeepsBoardsSeparate() {
        // Arrange
        val writer = user("first")
        val first = board(1)
        val second = board(2)
        mvc.perform(entryRequest(first, writer, "mp", entry("4S", "N", 0)))
            .andExpect(MockMvcResultMatchers.status().isOk())

        // Act
        val firstProtocol: ResultActions = mvc.perform(apiGet("/api/boards/{board}/entries", first))
        val secondProtocol: ResultActions = mvc.perform(apiGet("/api/boards/{board}/entries", second))

        // Assert
        firstProtocol.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].points").value(420))
        secondProtocol.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
            MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                "$",
                Matchers.hasSize<Any>(0)
            )
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["{\"contract\":\"0S\",\"declarer\":\"N\",\"result\":0}", "{\"contract\":\"4S\",\"declarer\":\"invalid\",\"result\":0}", "{\"contract\":\"4S\",\"declarer\":\"N\",\"lead\":\"C11\",\"result\":0}", "{\"contract\":\"4S\",\"declarer\":\"N\",\"result\":4}", "{\"contract\":\"4S\",\"result\":0}", "{\"contract\":\"pass\",\"result\":1}", "{\"contract\":\"4S\",\"declarer\":\"N\"}", "{}"
        ]
    )
    @Throws(Exception::class)
    fun rejectsInvalidEntryWithoutSavingAnything(body: String) {
        // Arrange
        val writer = user("writer")
        val board = board(1)
        val request: MockHttpServletRequestBuilder = entryRequest(board, writer, "mp", body)

        // Act
        val result: ResultActions = mvc.perform(request)

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        Assertions.assertEquals(
            0,
            jdbc.queryForObject<Int>("SELECT count(*) FROM board_entries", Int::class.java)
        )
    }

    @Test
    @Throws(Exception::class)
    fun missingUserAndBoardAreNotFoundWithoutPartialWrites() {
        // Arrange
        val writer = user("writer")
        val board = board(1)
        val missingUser: UUID = UUID.randomUUID()

        // Act
        val user: ResultActions = mvc.perform(apiGet("/api/users/{id}", missingUser))
        val protocol: ResultActions = mvc.perform(apiGet("/api/boards/9999/entries"))
        val missingWriter: ResultActions =
            mvc.perform(entryRequest(board, missingUser.toString(), "mp", entry("4S", "N", 0)))
        val missingBoard: ResultActions = mvc.perform(entryRequest(9999, writer, "mp", entry("4S", "N", 0)))

        // Assert
        user.andExpect(MockMvcResultMatchers.status().isNotFound())
        protocol.andExpect(MockMvcResultMatchers.status().isNotFound())
        missingWriter.andExpect(MockMvcResultMatchers.status().isNotFound())
        missingBoard.andExpect(MockMvcResultMatchers.status().isNotFound())
        Assertions.assertEquals(
            0,
            jdbc.queryForObject<Int>("SELECT count(*) FROM board_entries", Int::class.java)
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["-0.1", "0.5", "NaN", "Infinity"])
    @Throws(Exception::class)
    fun invalidImpTrimmingIsBadRequestWithoutSaving(value: String) {
        // Arrange
        val writer = user("writer")
        val board = board(1)
        val request: MockHttpServletRequestBuilder =
            entryRequest(board, writer, "imps", entry("4S", "N", 0)).param("throwAwayPercent", value)

        // Act
        val result: ResultActions = mvc.perform(request)

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isBadRequest())
        Assertions.assertEquals(
            0,
            jdbc.queryForObject<Int>("SELECT count(*) FROM board_entries", Int::class.java)
        )
    }

    @Test
    @Throws(Exception::class)
    fun leavesDatabaseConflictsToGenericErrorHandlerAndPreservesExistingUser() {
        // Arrange
        user("alice")
        val request: MockHttpServletRequestBuilder =
            apiPost("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"name\":\"Other\",\"surname\":\"User\"}")

        // Act
        val result: ResultActions = mvc.perform(request)

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isInternalServerError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(500))
            .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Internal server error"))
        Assertions.assertEquals(
            1,
            jdbc.queryForObject<Int>("SELECT count(*) FROM users", Int::class.java)
        )
    }

    @Test
    @Throws(Exception::class)
    fun validatesRequestBodyUuidPathAndMethodInsteadOfReturning500() {
        // Arrange
        val invalidUser: MockHttpServletRequestBuilder =
            apiPost("/api/users").contentType(MediaType.APPLICATION_JSON).content("{}")
        val invalidBoard: MockHttpServletRequestBuilder =
            apiPost("/api/boards").contentType(MediaType.APPLICATION_JSON)
                .content("{\"number\":0}")
        val invalidJson: MockHttpServletRequestBuilder =
            apiPost("/api/boards").contentType(MediaType.APPLICATION_JSON).content("not-json")

        // Act
        val user: ResultActions = mvc.perform(invalidUser)
        val board: ResultActions = mvc.perform(invalidBoard)
        val json: ResultActions = mvc.perform(invalidJson)
        val negativeId: ResultActions = mvc.perform(apiGet("/api/boards/-1"))
        val invalidUuid: ResultActions = mvc.perform(apiGet("/api/users/not-a-uuid"))
        val unsupportedMethod: ResultActions =
            mvc.perform(MockMvcRequestBuilders.put("/api/boards").contextPath("/api"))
        val unknownResource: ResultActions = mvc.perform(apiGet("/api/unknown-resource"))

        // Assert
        user.andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
        board.andExpect(MockMvcResultMatchers.status().isBadRequest())
        json.andExpect(MockMvcResultMatchers.status().isBadRequest())
        negativeId.andExpect(MockMvcResultMatchers.status().isBadRequest())
        invalidUuid.andExpect(MockMvcResultMatchers.status().isBadRequest())
        unsupportedMethod.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
        unknownResource.andExpect(MockMvcResultMatchers.status().isNotFound())
    }

    @Test
    @Throws(Exception::class)
    fun exposesRestEndpointsInOpenApi() {
        // Arrange
        val request: MockHttpServletRequestBuilder = apiGet("/api/v3/api-docs")

        // Act
        val result: ResultActions = mvc.perform(request)

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths['/users'].post").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths['/boards/{boardId}/entries/{userId}/mp'].post").exists())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.paths['/boards/{boardId}/entries/{userId}/imps'].post").exists()
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths['/users/by-surname'].get").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.paths['/users/{userId}'].put").doesNotExist())
    }

    @Test
    @Throws(Exception::class)
    fun rejectsRemovedPutEndpointsWithoutChangingData() {
        // Arrange
        val writer = user("writer")
        val board = board(1)

        // Act
        val user: ResultActions = mvc.perform(
            MockMvcRequestBuilders.put("/api/users/{id}", writer).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON).content("{}")
        )
        val entry: ResultActions = mvc.perform(
            MockMvcRequestBuilders.put("/api/boards/{board}/entries/{user}/mp", board, writer).contextPath("/api")
                .contentType(MediaType.APPLICATION_JSON).content(entry("4S", "N", 0))
        )

        // Assert
        user.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
        entry.andExpect(MockMvcResultMatchers.status().isMethodNotAllowed())
        Assertions.assertEquals(
            0,
            jdbc.queryForObject<Int>("SELECT count(*) FROM board_entries", Int::class.java)
        )
    }

    @Test
    @Throws(Exception::class)
    fun storesNamesWithoutTrimmingOrChangingCase() {
        // Arrange
        val id = createUser(
            """
                {"username":" spaced ","name":" Alice ","surname":" Example "}

                """.trimIndent()
        )

        // Act
        val user: ResultActions = mvc.perform(apiGet("/api/users/{id}", id))
        val exactSurname: ResultActions = mvc.perform(apiGet("/api/users/by-surname").param("surname", " Example "))
        val trimmedSurname: ResultActions = mvc.perform(apiGet("/api/users/by-surname").param("surname", "Example"))

        // Assert
        user.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(" spaced "))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(" Alice "))
            .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(" Example "))
        exactSurname.andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(id))
        trimmedSurname.andExpect(MockMvcResultMatchers.status().isOk()).andExpect(
            MockMvcResultMatchers.jsonPath<MutableCollection<*>>(
                "$",
                Matchers.hasSize<Any>(0)
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun enforcesUsernameLengthThroughDatabaseConstraint() {
        // Arrange
        user("u".repeat(255))
        val request: MockHttpServletRequestBuilder =
            apiPost("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + "u".repeat(256) + "\",\"name\":\"Player\",\"surname\":\"Example\"}")

        // Act
        val result: ResultActions = mvc.perform(request)

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isInternalServerError())
        Assertions.assertEquals(
            1,
            jdbc.queryForObject<Int>("SELECT count(*) FROM users", Int::class.java)
        )
    }

    @Throws(Exception::class)
    private fun user(username: String?): String {
        return createUser("{\"username\":\"" + username + "\",\"name\":\"Player\",\"surname\":\"Example\"}")
    }

    @Throws(Exception::class)
    private fun createUser(body: String): String {
        val result: MvcResult = mvc.perform(
            apiPost("/api/users").contentType(MediaType.APPLICATION_JSON).content(body)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn()
        return JsonPath.read<String>(result.getResponse().getContentAsString(), "$")
    }

    @Throws(Exception::class)
    private fun board(number: Int): Int {
        val result: MvcResult = mvc.perform(
            apiPost("/api/boards").contentType(MediaType.APPLICATION_JSON)
                .content("{\"number\":" + number + "}")
        )
            .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn()
        return JsonPath.read<Int>(result.getResponse().getContentAsString(), "$")
    }

    private fun entryRequest(
        boardId: Int,
        writerId: String?,
        mode: String?,
        body: String
    ): MockHttpServletRequestBuilder {
        return apiPost("/api/boards/{board}/entries/{writer}/{mode}", boardId, writerId, mode)
            .contentType(MediaType.APPLICATION_JSON).content(body)
    }

    private fun apiGet(path: String, vararg variables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.get(path, *variables).contextPath("/api")
    }

    private fun apiPost(path: String, vararg variables: Any?): MockHttpServletRequestBuilder {
        return MockMvcRequestBuilders.post(path, *variables).contextPath("/api")
    }

    private fun entry(contract: String, declarer: String?, result: Int): String {
        val direction = if (declarer == null) "null" else "\"" + declarer + "\""
        return ("{\"contract\":\"" + contract + "\",\"declarer\":" + direction
                + ",\"lead\":null,\"result\":" + result + "}")
    }

    companion object {
        @Container
        val postgres: PostgreSQLContainer =
            PostgreSQLContainer("postgres:16-alpine")

        @JvmStatic
        @DynamicPropertySource
        fun database(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", Supplier { postgres.getJdbcUrl() })
            registry.add("spring.datasource.username", Supplier { postgres.getUsername() })
            registry.add("spring.datasource.password", Supplier { postgres.getPassword() })
        }
    }
}
