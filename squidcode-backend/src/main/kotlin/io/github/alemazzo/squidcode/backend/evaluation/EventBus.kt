package io.github.alemazzo.squidcode.backend.evaluation

import io.github.alemazzo.squidcode.backend.evaluation.domain.EvaluationEvent
import io.micronaut.context.event.ApplicationEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject
import jakarta.inject.Singleton

@Singleton
class EventBus : ApplicationEventListener<EvaluationEvent> {

	private val subject = PublishSubject.create<EvaluationEvent>()

	override fun onApplicationEvent(event: EvaluationEvent) {
		subject.onNext(event)
	}

	fun listenEvents(): Flowable<EvaluationEvent> = subject.toFlowable(BackpressureStrategy.BUFFER)
}
