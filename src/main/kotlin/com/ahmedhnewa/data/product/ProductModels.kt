package com.ahmedhnewa.data.product

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

/**
 * How it looks in database
 */
data class Product(
    @BsonId
    val id: ObjectId = ObjectId(),
    val name: String,
    val description: String,
    val shortDescription: String,
    val originalPrice: Double,
    val salePrice: Double,
    val imageUrl: String,
    val categories: Set<String>
) {
    fun toResponse(): ProductResponse = ProductResponse(
        id = id.toString(),
        name = name,
        description = description,
        shortDescription = shortDescription,
        originalPrice = originalPrice,
        salePrice = salePrice,
        imageUrl = imageUrl,
        categories = categories
    )
}

@Serializable
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String,
    val originalPrice: Double,
    val salePrice: Double,
    val imageUrl: String,
    val categories: Set<String>
)

@Serializable
data class ProductRequest(
    val name: String,
    val description: String,
    val shortDescription: String,
    val originalPrice: Double,
    val salePrice: Double,
    val imageUrl: String,
    val categories: Set<String>
) {
    fun validate(): Boolean {
        return true
    }

    fun toDatabaseModel(id: ObjectId = ObjectId()) =
        Product(
            id = id,
            name = name,
            description = description,
            shortDescription = shortDescription,
            originalPrice = originalPrice,
            salePrice = salePrice,
            imageUrl = imageUrl,
            categories = categories
        )
}