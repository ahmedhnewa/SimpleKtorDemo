package com.ahmedhnewa.data.product.category

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

/**
 * How it looks in database
 */
data class ProductCategory(
    @BsonId
    val id: ObjectId = ObjectId(),
    val name: String,
    val description: String,
    val shortDescription: String,
    val imageUrl: String,
    val parent: String?,
) {
    fun toResponseModel(): ProductCategoryResponse = ProductCategoryResponse(
        id = id.toString(),
        name = name,
        description = description,
        shortDescription = shortDescription,
        imageUrl = imageUrl,
        parent = parent
    )
}

/*
* Use toListOf() { // create the instance of the converted list for every item }
* */
@Deprecated("Use toListOf() { // create the instance of the converted list for every item }")
fun List<ProductCategory>.toResponse(): List<ProductCategoryResponse> {
    val productCategoriesResponse = mutableListOf<ProductCategoryResponse>()
    this.forEach {
        productCategoriesResponse.add(it.toResponseModel())
    }
    return productCategoriesResponse
}

/**
 * For insert data in the body
 */
@Serializable
data class ProductCategoryRequest(
    val name: String,
    val description: String,
    val shortDescription: String,
    val imageUrl: String,
    val parent: String?
) {
    fun toDatabaseModel(id: ObjectId = ObjectId()): ProductCategory = ProductCategory(
        name = name,
        description = description,
        shortDescription = shortDescription,
        imageUrl = imageUrl,
        parent = parent,
        id = id
    )
}

/**
 * How it looks in response
 */
@Serializable
data class ProductCategoryResponse(
    val id: String,
    val name: String,
    val description: String,
    val shortDescription: String,
    val imageUrl: String,
    val parent: String?
)