package io.github.threedify.backend.storage.application

interface StorageProvider {
	fun uploadFile(content: ByteArray): String
	fun getFile(fileId: String): ByteArray
	fun deleteFile(fileId: String)
}