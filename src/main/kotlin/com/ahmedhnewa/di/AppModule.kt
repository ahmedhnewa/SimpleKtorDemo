package com.ahmedhnewa.di

import com.ahmedhnewa.data.product.MongoProductDataSource
import com.ahmedhnewa.data.product.ProductDataSource
import com.ahmedhnewa.data.product.category.MongoProductCategoryDataSource
import com.ahmedhnewa.data.product.category.ProductCategoryDataSource
import com.ahmedhnewa.data.user.MongoUserDataSource
import com.ahmedhnewa.data.user.UserDataSource
import com.ahmedhnewa.services.mail.MailSenderService
import com.ahmedhnewa.services.mail.JavaMailSenderService
import com.ahmedhnewa.services.security.hashing.HashingService
import com.ahmedhnewa.services.security.hashing.SHA256HashingService
import com.ahmedhnewa.services.security.token.JwtTokenService
import com.ahmedhnewa.services.security.token.TokenService
import com.ahmedhnewa.utils.Constants
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single<TokenService> {
        JwtTokenService()
    }
    single<HashingService> {
        SHA256HashingService()
    }
    single<MailSenderService> {
        JavaMailSenderService()
    }
    single {
        val databaseUrl = System.getenv("DATABASE_URL").ifEmpty { throw IllegalStateException("DATABASE_URL environment variable is not defined") }
        KMongo.createClient(
            connectionString = databaseUrl
        ).coroutine.getDatabase(Constants.DB_NAME)
    }
    single<UserDataSource> {
        MongoUserDataSource(get())
    }
    single<ProductCategoryDataSource> {
        MongoProductCategoryDataSource(get())
    }
    single<ProductDataSource> {
        MongoProductDataSource(get())
    }
}