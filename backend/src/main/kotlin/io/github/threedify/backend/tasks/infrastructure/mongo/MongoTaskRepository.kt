package io.github.threedify.backend.tasks.infrastructure.mongo

import io.github.threedify.backend.tasks.domain.Task
import io.github.threedify.backend.tasks.domain.TaskRepository
import jakarta.inject.Singleton

@Singleton
class MongoTaskRepository(private val repository: TaskMongoEntityRepository) : TaskRepository {
	override fun save(task: Task): Task {
		val entity = TaskMongoEntity.fromDomain(task)
		val savedEntity = repository.save(entity)
		return savedEntity.toDomain()
	}

	override fun findById(id: String): Task? {
		val entity = repository.findById(id).orElse(null)
		return entity?.toDomain()
	}

	override fun update(task: Task): Task {
		val entity = TaskMongoEntity.fromDomain(task)
		val updatedEntity = repository.update(entity)
		return updatedEntity.toDomain()
	}
}