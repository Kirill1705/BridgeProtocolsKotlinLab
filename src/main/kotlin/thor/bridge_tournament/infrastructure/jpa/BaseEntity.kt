package thor.bridge_tournament.infrastructure.jpa

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import java.time.Instant
import org.hibernate.annotations.SQLRestriction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
@SQLRestriction("removed = false")
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdTime: Instant? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedTime: Instant? = null

    @Column(name = "removed", nullable = false)
    var isRemoved: Boolean = false

    fun delete() {
        isRemoved = true
    }
}
