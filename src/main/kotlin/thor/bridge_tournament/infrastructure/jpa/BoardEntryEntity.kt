package thor.bridge_tournament.infrastructure.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "board_entries")
class BoardEntryEntity : BaseEntity() {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "board_id")
    lateinit var board: BoardEntity

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "writer_id")
    lateinit var writer: UserEntity

    @Column(name = "contract")
    var contract: String = ""

    @Column(name = "declarer")
    var declarer: String? = null

    @Column(name = "lead")
    var lead: String? = null

    @Column(name = "result")
    var result: Int = 0
}
