package com.ahmedhnewa.plugins

import com.ahmedhnewa.data.product.ProductDataSource
import com.ahmedhnewa.data.product.category.ProductCategoryDataSource
import com.ahmedhnewa.data.user.UserDataSource
import com.ahmedhnewa.routes.auth.*
import com.ahmedhnewa.routes.product.*
import com.ahmedhnewa.routes.product.category.*
import com.ahmedhnewa.services.mail.MailSenderService
import com.ahmedhnewa.services.security.hashing.HashingService
import com.ahmedhnewa.services.security.token.TokenService
import com.ahmedhnewa.utils.helpers.ktor.RequestHelpers
import com.ahmedhnewa.utils.helpers.ktor.getServerUrl
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val userDataSource by inject<UserDataSource>()
    val productCategoryDataSource by inject<ProductCategoryDataSource>()
    val productDataSource by inject<ProductDataSource>()
    val tokenService by inject<TokenService>()
    val hashingService by inject<HashingService>()
    val mailSenderService by inject<MailSenderService>()

    routing {
        route("/authentication") {
            val authRoutes = AuthRoutes(
                userDataSource = userDataSource,
                tokenService = tokenService,
                hashingService = hashingService,
                mailSenderService = mailSenderService,
                routeManager = this
            )
            authRoutes.signup()
            authRoutes.signIn()
            authRoutes.activeUserAccount()
            authRoutes.getUserInfo()
        }
        route("/products") {
            route("/category") {
                val productCategoryRoutes = ProductCategoryRoutes(
                    productCategoryDataSource = productCategoryDataSource,
                    routeManager = this
                )
                productCategoryRoutes.getAllProductCategoriesRoute()
                productCategoryRoutes.getProductCategoryRoute()
                productCategoryRoutes.addProductCategoryRoute()
                productCategoryRoutes.updateProductCategoryRoute()
                productCategoryRoutes.deleteProductCategoryRoute()
            }
            val productRoutes = ProductRoutes(
                productCategoryDataSource = productCategoryDataSource,
                productDataSource = productDataSource,
                routeManager = this
            )
            productRoutes.getAllProductsRoute()
            productRoutes.getProductRoute()
            productRoutes.addProductRoute()
            productRoutes.updateProductRoute()
            productRoutes.deleteProductRoute()
        }

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }
    }
}
