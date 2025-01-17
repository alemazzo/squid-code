package io.github.threedify.backend.storage.application

import jakarta.inject.Singleton

@Singleton
class StorageService(private val storageProvider: StorageProvider) {

	fun uploadFile(content: ByteArray): String {
		return storageProvider.uploadFile(content)
	}

	fun getFile(fileId: String): ByteArray {
		return storageProvider.getFile(fileId)
	}

	fun deleteFile(fileId: String) {
		storageProvider.deleteFile(fileId)
	}
	
}