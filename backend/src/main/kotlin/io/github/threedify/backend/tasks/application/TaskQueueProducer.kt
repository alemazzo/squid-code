package io.github.threedify.backend.tasks.application

import io.github.threedify.backend.tasks.domain.Task

interface TaskQueueProducer {
	fun enqueueTask(task: Task)
}

