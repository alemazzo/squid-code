package io.github.threedify.backend.tasks.infrastructure.mongo

import io.github.threedify.backend.tasks.domain.Task
import io.github.threedify.backend.tasks.domain.TaskStatus
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity

@MappedEntity
data class TaskMongoEntity(
	@Id val id: String = "",
	var status: TaskStatus = TaskStatus.PENDING,
	val createdAt: Long = System.currentTimeMillis(),
	val imageUrl: String = "",
	var resultUrl: String? = null,
) {
	fun toDomain(): Task {
		return Task(
			id = id,
			status = status,
			createdAt = createdAt,
			imageUrl = imageUrl,
			resultUrl = resultUrl
		)
	}

	companion object {
		fun fromDomain(task: Task): TaskMongoEntity {
			return TaskMongoEntity(
				id = task.id,
				status = task.status,
				createdAt = task.createdAt,
				imageUrl = task.imageUrl,
				resultUrl = task.resultUrl
			)
		}
	}
}