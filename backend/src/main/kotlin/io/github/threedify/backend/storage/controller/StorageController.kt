package io.github.threedify.backend.storage.controller

import io.github.threedify.backend.storage.application.StorageService
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import org.slf4j.LoggerFactory.getLogger

@Controller("/storage")
class StorageController(
	private val storageService: StorageService,
) {
	private val logger = getLogger(this.javaClass)

	// Consume raw bytes
	@Post("/", consumes = [MediaType.ALL])
	fun uploadFile(@Body content: ByteArray): String {
		logger.info("Uploading file")
		return storageService.uploadFile(content)
	}

	@Post("/{fileId}")
	fun getFile(@PathVariable fileId: String): ByteArray {
		logger.info("Getting file: $fileId")
		return storageService.getFile(fileId)
	}

	@Delete("/{fileId}")
	fun deleteFile(@PathVariable fileId: String) {
		logger.info("Deleting file: $fileId")
		storageService.deleteFile(fileId)
	}

}