package thor.bridge_tournament.core

import java.util.UUID
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import thor.bridge_tournament.core.domain.Contract
import thor.bridge_tournament.core.domain.board.Board
import thor.bridge_tournament.core.domain.board.BoardEntry
import thor.bridge_tournament.core.domain.board.Direction
import thor.bridge_tournament.core.domain.board.Vulnerable
import thor.bridge_tournament.core.exception.DomainValidationException
import thor.bridge_tournament.core.mapping.BoardMapper
import thor.bridge_tournament.core.port.dto.board.BoardDto

internal class BoardDomainTests {
    @ParameterizedTest
    @CsvSource(
        "1,N,NO_ONE",
        "2,E,NS",
        "3,S,EW",
        "4,W,ALL",
        "5,N,NS",
        "6,E,EW",
        "7,S,ALL",
        "8,W,NO_ONE",
        "9,N,EW",
        "10,E,ALL",
        "11,S,NO_ONE",
        "12,W,NS",
        "13,N,ALL",
        "14,E,NO_ONE",
        "15,S,NS",
        "16,W,EW"
    )
    fun standardDealerAndVulnerabilityCycleRepeatsEverySixteenBoards(
        number: Int,
        dealer: Direction?,
        vulnerable: Vulnerable
    ) {
        for (offset in intArrayOf(0, 16, 32)) {
            // Arrange
            val boardNumber = number + offset

            // Act
            val board: Board = Board(boardNumber)

            // Assert
            Assertions.assertEquals(dealer, board.dealer)
            Assertions.assertEquals(vulnerable, board.vulnerable)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "1NT,0,NO_ONE,90",
        "2S,1,NO_ONE,140",
        "3NT,0,NO_ONE,400",
        "4S,0,NO_ONE,420",
        "4S,0,ALL,620",
        "5D,0,ALL,600",
        "6NT,0,ALL,1440",
        "7NT,0,NO_ONE,1520",
        "2HX,0,NO_ONE,470",
        "2HXX,1,NO_ONE,840",
        "4SX,1,ALL,990",
        "4S,-2,NO_ONE,-100",
        "4S,-2,ALL,-200",
        "4SX,-1,NO_ONE,-100",
        "4SX,-2,NO_ONE,-300",
        "4SX,-3,NO_ONE,-500",
        "4SX,-4,NO_ONE,-800",
        "4SX,-1,ALL,-200",
        "4SX,-2,ALL,-500",
        "4SX,-3,ALL,-800",
        "4SXX,-4,NO_ONE,-1600",
        "4SXX,-3,ALL,-1600"
    )
    fun calculatesContractOvertricksSlamsAndPenaltiesFromNorthSouthPerspective(
        contract: String, result: Int,
        vulnerable: Vulnerable, points: Int
    ) {
        // Arrange
        val board: Board = Board(1, 1, Direction.N, vulnerable)

        // Act
        val north: BoardEntry = entry(board, contract, Direction.N, result)
        val south: BoardEntry = entry(board, contract, Direction.S, result)
        val east: BoardEntry = entry(board, contract, Direction.E, result)
        val west: BoardEntry = entry(board, contract, Direction.W, result)

        // Assert
        Assertions.assertEquals(points, north.points)
        Assertions.assertEquals(points, south.points)
        Assertions.assertEquals(-points, east.points)
        Assertions.assertEquals(-points, west.points)
    }

    @Test
    fun vulnerabilityDependsOnDeclarersSideAndPassNeedsNoDeclarer() {
        // Arrange
        val board: Board = Board(1, 1, Direction.N, Vulnerable.NS)

        // Act
        val north: BoardEntry = entry(board, "4S", Direction.N, 0)
        val east: BoardEntry = entry(board, "4S", Direction.E, 0)
        val pass: BoardEntry = entry(board, "pass", null, 0)

        // Assert
        Assertions.assertEquals(620, north.points)
        Assertions.assertEquals(-420, east.points)
        Assertions.assertEquals(0, pass.points)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0])
    fun rejectsNonPositiveBoardNumber(number: Int) {
        // Arrange
        val createBoard: Executable =
            Executable { Board(number) }

        // Act
        val failure: Throwable? =
            org.assertj.core.api.Assertions.catchThrowable(ThrowingCallable { createBoard.execute() })

        // Assert
        Assertions.assertInstanceOf<DomainValidationException>(
            DomainValidationException::class.java,
            failure
        )
    }

    @Test
    fun impossibleTrickCountsAreRejectedWithoutChangingExistingResult() {
        // Arrange
        val entry: BoardEntry = entry(Board(1), "4S", Direction.N, 0)
        val contract: Contract = Contract("4S")

        // Act
        val tooMany: Throwable? = org.assertj.core.api.Assertions.catchThrowable(ThrowingCallable {
            entry.setResult(
                contract,
                Direction.N,
                null,
                4
            )
        })
        val tooFew: Throwable? = org.assertj.core.api.Assertions.catchThrowable(ThrowingCallable {
            entry.setResult(
                contract,
                Direction.N,
                null,
                -11
            )
        })

        // Assert
        Assertions.assertInstanceOf<DomainValidationException>(
            DomainValidationException::class.java,
            tooMany
        )
        Assertions.assertInstanceOf<DomainValidationException>(
            DomainValidationException::class.java,
            tooFew
        )
        Assertions.assertEquals(420, entry.points)
        Assertions.assertEquals(0, entry.result)
    }

    @Test
    fun mapsLowercaseDealerAndVulnerability() {
        // Arrange
        val dto: BoardDto = BoardDto(1, 1, "n", "ns")

        // Act
        val board: Board = BoardMapper.fromDto(dto)

        // Assert
        Assertions.assertEquals(
            Direction.N,
            board.dealer
        )
        Assertions.assertEquals(Vulnerable.NS, board.vulnerable)
    }

    private fun entry(
        board: Board,
        contract: String,
        declarer: Direction?,
        result: Int
    ): BoardEntry {
        return BoardEntry(
            UUID.randomUUID(),
            board,
            Contract(contract),
            declarer,
            null,
            result,
            null
        )
    }
}
