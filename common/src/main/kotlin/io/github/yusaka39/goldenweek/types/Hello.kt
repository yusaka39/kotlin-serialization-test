package io.github.yusaka39.goldenweek.types

import kotlinx.serialization.Serializable

@Serializable
data class Hello(
        val hello: String = "hello"
)