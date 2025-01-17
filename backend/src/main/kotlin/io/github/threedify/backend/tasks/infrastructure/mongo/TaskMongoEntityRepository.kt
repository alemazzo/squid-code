package io.github.threedify.backend.tasks.infrastructure.mongo

import io.micronaut.data.mongodb.annotation.MongoRepository
import io.micronaut.data.repository.CrudRepository

@MongoRepository
interface TaskMongoEntityRepository : CrudRepository<TaskMongoEntity, String>