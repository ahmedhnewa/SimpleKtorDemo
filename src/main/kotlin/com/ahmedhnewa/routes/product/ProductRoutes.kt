package com.ahmedhnewa.routes.product

import com.ahmedhnewa.data.product.ProductDataSource
import com.ahmedhnewa.data.product.ProductRequest
import com.ahmedhnewa.data.product.category.ProductCategoryDataSource
import com.ahmedhnewa.utils.allowOnlyAdmin
import com.ahmedhnewa.utils.helpers.ktor.RequestHelpers
import com.ahmedhnewa.utils.helpers.ktor.receiveAs
import com.ahmedhnewa.utils.helpers.ktor.requireId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId

class ProductRoutes(
    private val productCategoryDataSource: ProductCategoryDataSource,
    private val productDataSource: ProductDataSource,
    private val routeManager: Route
) {
    fun getAllProductsRoute(
    ) = routeManager.get("/") {
        val products = productDataSource.getProducts()
        val productsResponse = products.map { it.toResponse() }
        call.respond(HttpStatusCode.OK, productsResponse)
    }

    fun getProductRoute() = routeManager.get(RequestHelpers.ID) {
        val productId = call.requireId()
        val product = productDataSource.getProductById(productId)
        if (product == null) {
            call.respond(HttpStatusCode.Conflict, "There is no any product with that id to get")
            return@get
        }
        val productResponse = product.toResponse()
        call.respond(HttpStatusCode.OK, productResponse)
    }

    fun addProductRoute() = routeManager.authenticate {
        post("/") {
            call.allowOnlyAdmin()
            val productRequest = call.receiveAs<ProductRequest>("Enter valid product in the body to add product")

            val isAnyCategoryNull =
                productRequest.categories.any { productCategoryDataSource.getCategoryById(it) == null }
            if (isAnyCategoryNull) {
                call.respond(HttpStatusCode.Conflict, "All categories ids must be valid and exists")
                return@post
            }
            val isNameUsed = productDataSource.getProductByName(productRequest.name) != null
            if (isNameUsed) {
                call.respond(HttpStatusCode.Conflict, "This product name is used before by other product")
                return@post
            }
            val success = productDataSource.addProduct(
                productRequest.toDatabaseModel()
            )
            if (!success) {
                call.respond(HttpStatusCode.Conflict, "Error while adding the product to the database")
                return@post
            }
            call.respond(HttpStatusCode.Created, "Product has been added to the database successfully")
        }
    }

    fun updateProductRoute(
    ) = routeManager.authenticate {
        put(RequestHelpers.ID) {
            call.allowOnlyAdmin()
            val productRequest = call.receiveAs<ProductRequest>(
                "Enter valid product in the body to update the product"
            )

            val isAnyCategoryNull =
                productRequest.categories.any { productCategoryDataSource.getCategoryById(it) == null }
            if (isAnyCategoryNull) {
                call.respond(HttpStatusCode.Conflict, "All categories ids must be valid and exists")
                return@put
            }
            val productId = call.requireId()
            val productToBeUpdated = productDataSource.getProductById(productId)
            if (productToBeUpdated == null) {
                call.respond(HttpStatusCode.Conflict, "Can't find product by this id")
                return@put
            }
            val success = productDataSource.updateProduct(
                productRequest.toDatabaseModel(
                    ObjectId(productId)
                )
            )
            if (!success) {
                call.respond(HttpStatusCode.Conflict, "Error while adding the product to the database")
                return@put
            }
            call.respond(HttpStatusCode.OK, "Product has been updated successfully")
        }
    }

    fun deleteProductRoute() = routeManager.authenticate {
        delete(RequestHelpers.ID) {
            call.allowOnlyAdmin()
            val productId = call.requireId()
            val isExists = productDataSource.getProductById(productId) != null
            if (!isExists) {
                call.respond(HttpStatusCode.Conflict, "There is no any product with that id to delete")
                return@delete
            }
            val isDeleteSuccess = productDataSource.deleteProduct(productId)
            if (!isDeleteSuccess) {
                call.respond(HttpStatusCode.Conflict, "Error while delete the product from the database")
                return@delete
            }
            call.respond(HttpStatusCode.NoContent, "Product has been deleted from the database successfully")
        }
    }

}
