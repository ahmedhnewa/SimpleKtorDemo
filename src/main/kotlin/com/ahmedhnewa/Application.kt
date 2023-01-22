package com.ahmedhnewa

import com.ahmedhnewa.di.mainModule
import com.ahmedhnewa.plugins.*
import com.ahmedhnewa.utils.GlobalAppObjects
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import java.net.InetAddress

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    GlobalAppObjects.jwtConfig = environment.config.config("jwt")

    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
