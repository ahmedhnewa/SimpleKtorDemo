package com.ahmedhnewa.data.product

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoProductDataSource(
    db: CoroutineDatabase
): ProductDataSource {
    private val products = db.getCollection<Product>("products")
    override suspend fun getProducts(): List<Product> {
        return products.find()
            .toList()
    }

    override suspend fun getProductById(id: String): Product? {
        return try {
            products.findOneById(ObjectId(id))
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override suspend fun getProductByName(productName: String): Product? {
        return products.findOne(Product::name eq productName)
    }

    override suspend fun addProduct(product: Product): Boolean {
        return products.insertOne(product).wasAcknowledged()
    }

    override suspend fun updateProduct(product: Product): Boolean {
        return products.updateOneById(id = product.id, product).wasAcknowledged()
    }

    override suspend fun deleteProduct(id: String): Boolean {
        return products.deleteOneById(ObjectId(id)).wasAcknowledged()
    }
}