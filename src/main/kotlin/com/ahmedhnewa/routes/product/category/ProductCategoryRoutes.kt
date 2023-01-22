package com.ahmedhnewa.routes.product.category

import com.ahmedhnewa.data.product.category.ProductCategoryDataSource
import com.ahmedhnewa.data.product.category.ProductCategoryRequest
import com.ahmedhnewa.utils.allowOnlyAdmin
import com.ahmedhnewa.utils.helpers.ktor.RequestHelpers
import com.ahmedhnewa.utils.helpers.ktor.requireId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class ProductCategoryRoutes (
    private val productCategoryDataSource: ProductCategoryDataSource,
    private val routeManager: Route
) {
    fun getAllProductCategoriesRoute() = routeManager.get("/") {
        val productCategories = productCategoryDataSource.getAllCategories()
        val productCategoriesResponse = productCategories.map { it.toResponseModel() }
        if (productCategoriesResponse.isEmpty()) {
            call.respond(HttpStatusCode.OK, message = productCategoriesResponse)
            return@get
        }

        call.respond(HttpStatusCode.OK, message = productCategoriesResponse)
    }
    fun getProductCategoryRoute() = routeManager.get(RequestHelpers.ID) {
        val productCategoryId = call.requireId()
        val productCategory = productCategoryDataSource.getCategoryById(productCategoryId)
        if (productCategory == null) {
            call.respond(HttpStatusCode.NotFound, "Can't be found")
            return@get
        }
        call.respond(HttpStatusCode.OK, productCategory.toResponseModel())
    }

    fun addProductCategoryRoute() = routeManager.authenticate {
        post("/") {
            call.allowOnlyAdmin()

            val productCategoryRequest = call.receiveNullable<ProductCategoryRequest>()
            if (productCategoryRequest == null) {
                call.respond(HttpStatusCode.BadRequest, "Please enter valid product category in the body to add it")
                return@post
            }
            if (productCategoryRequest.parent != null) {
                val parentProductCategory = productCategoryDataSource.getCategoryById(productCategoryRequest.parent)
                if (parentProductCategory == null) {
                    call.respond(HttpStatusCode.Conflict, "The parent product category id does not exists in the database.")
                    return@post
                }
            }
            val isSuccess = productCategoryDataSource.addCategory(productCategoryRequest.toDatabaseModel())
            if (!isSuccess) {
                call.respond(HttpStatusCode.BadRequest, "Error while insert the data to database")
                return@post
            }
            call.respond(HttpStatusCode.Created, "Data has been added successfully")
        }
    }
    fun updateProductCategoryRoute() = routeManager.authenticate {
        put(RequestHelpers.ID) {
            call.allowOnlyAdmin()

            val productCategoryRequest = call.receiveNullable<ProductCategoryRequest>()
            if (productCategoryRequest == null) {
                call.respond(HttpStatusCode.BadRequest, "Please enter valid product category in the body to update it")
                return@put
            }
            val productCategoryId = call.requireId()

            val oldProductCategory = productCategoryDataSource.getCategoryById(productCategoryId)
            if (oldProductCategory == null) {
                call.respond(HttpStatusCode.NotFound, "There is no any product category by this id")
                return@put
            }

            if (productCategoryRequest.parent != null) {
                val parentProductCategory = productCategoryDataSource.getCategoryById(productCategoryRequest.parent)
                if (parentProductCategory == null) {
                    call.respond(HttpStatusCode.Conflict, "The parent product category id does not exists in the database.")
                    return@put
                }
            }

            val isUpdateSuccess = productCategoryDataSource.updateCategory(
                productCategoryRequest.toDatabaseModel(id = oldProductCategory.id)
            )
            if (!isUpdateSuccess) {
                call.respond(HttpStatusCode.BadRequest, "Error while update the data in the database")
                return@put
            }
            call.respond(HttpStatusCode.OK, "Data has been updated successfully")
        }
    }

    fun deleteProductCategoryRoute() = routeManager.authenticate {
        delete(RequestHelpers.ID) {
            call.allowOnlyAdmin()

            val productCategoryId = call.requireId()

            val deletedProductCategory = productCategoryDataSource.getCategoryById(productCategoryId)
            if (deletedProductCategory == null) {
                call.respond(HttpStatusCode.NotFound, "There is no any product category by this id")
                return@delete
            }
            val isDeleteSuccess = productCategoryDataSource.deleteCategory(productCategoryId)
            if (!isDeleteSuccess) {
                call.respond(HttpStatusCode.BadRequest, "Error while delete the data in the database")
                return@delete
            }
            call.respond(HttpStatusCode.NoContent, "Data has been deleted successfully")
        }
    }
}

//@Deprecated("It get quite complex")
//fun Route.productCategoryRoutes(
//    // this might more scalable
////    productCategoryDataSource: ProductCategoryDataSource = DataSources.productCategoryDataSource
//) {}
