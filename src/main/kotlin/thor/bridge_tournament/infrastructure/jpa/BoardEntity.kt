package thor.bridge_tournament.infrastructure.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "boards")
class BoardEntity : BaseEntity() {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "number")
    var number: Int = 0

    @Column(name = "dealer")
    var dealer: String = ""

    @Column(name = "vulnerable")
    var vulnerable: String = ""
}
