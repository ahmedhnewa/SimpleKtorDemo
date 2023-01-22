package com.ahmedhnewa.data.product.category

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoProductCategoryDataSource(
    db: CoroutineDatabase
) : ProductCategoryDataSource {
    private val categories = db.getCollection<ProductCategory>("categories")

    override suspend fun getAllCategories(): List<ProductCategory> {
        return categories.find()
            .toList()
    }

    override suspend fun getCategoryById(id: String): ProductCategory? {
        return try {
            categories.findOneById(ObjectId(id))
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override suspend fun addCategory(productCategory: ProductCategory): Boolean {
        return categories.insertOne(productCategory).wasAcknowledged()
    }

    override suspend fun updateCategory(updatedCategory: ProductCategory): Boolean {
        return categories.updateOneById(id = updatedCategory.id, updatedCategory).wasAcknowledged()
    }

    override suspend fun deleteCategory(id: String): Boolean {
        return categories.deleteOneById(ObjectId(id)).wasAcknowledged()
    }


}