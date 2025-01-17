package io.github.threedify.backend.tasks.domain

import io.micronaut.serde.annotation.Serdeable
import java.util.*

@Serdeable
data class Task(
	val id: String = UUID.randomUUID().toString(),
	var status: TaskStatus = TaskStatus.PENDING,
	val createdAt: Long = System.currentTimeMillis(),
	val imageUrl: String = "", // To store the image ID
	var resultUrl: String? = null, // To store the result resource ID after processing
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Task

		if (id != other.id) return false
		if (status != other.status) return false
		if (createdAt != other.createdAt) return false
		if (resultUrl != other.resultUrl) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + status.hashCode()
		result = 31 * result + createdAt.hashCode()
		result = 31 * result + (resultUrl?.hashCode() ?: 0)
		return result
	}
}

