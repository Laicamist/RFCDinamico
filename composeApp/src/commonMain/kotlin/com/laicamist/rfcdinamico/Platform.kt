package com.laicamist.rfcdinamico

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform