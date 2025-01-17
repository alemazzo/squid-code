package io.github.threedify.backend.storage.infrastructure

import com.mongodb.client.MongoClient
import com.mongodb.client.gridfs.GridFSBucket
import com.mongodb.client.gridfs.GridFSBuckets
import com.mongodb.client.gridfs.model.GridFSUploadOptions
import io.github.threedify.backend.storage.application.StorageProvider
import jakarta.inject.Singleton
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory.getLogger
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

@Singleton
class MongoStorageProvider(mongoClient: MongoClient) : StorageProvider {

	private val logger = getLogger(this.javaClass)
	private val databaseName = "3dify" // Your MongoDB database name
	private val bucket: GridFSBucket = GridFSBuckets.create(mongoClient.getDatabase(databaseName))

	override fun uploadFile(content: ByteArray): String {
		logger.info("Uploading file")
		val options = GridFSUploadOptions().chunkSizeBytes(1024 * 1024) // 1MB chunks
		val inputStream = ByteArrayInputStream(content)
		val fileName = UUID.randomUUID().toString()
		val fileId = bucket.uploadFromStream(fileName, inputStream, options)
		return fileId.toHexString()
	}

	override fun getFile(fileId: String): ByteArray {
		logger.info("Getting file: $fileId")
		val fileObjectId = ObjectId(fileId)
		val outputStream = ByteArrayOutputStream()
		bucket.downloadToStream(fileObjectId, outputStream)
		return outputStream.toByteArray()
	}

	override fun deleteFile(fileId: String) {
		logger.info("Deleting file: $fileId")
		val fileObjectId = ObjectId(fileId)
		bucket.delete(fileObjectId)
	}
}