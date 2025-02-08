package io.github.alemazzo.squidcode.backend.notification

import com.example.service.EvaluationEvent
import com.example.service.EventBus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.server.types.ServerSentEvent
import io.reactivex.Flowable

@Controller("/api/events")
class EventController(private val eventBus: EventBus) {

	@Get(uri = "/stream", produces = [MediaType.TEXT_EVENT_STREAM])
	fun streamEvents(): Flowable<ServerSentEvent<EvaluationEvent>> {
		return eventBus.listenEvents()
			.map { event ->
				ServerSentEvent.builder(event).build()
			}
	}
}
