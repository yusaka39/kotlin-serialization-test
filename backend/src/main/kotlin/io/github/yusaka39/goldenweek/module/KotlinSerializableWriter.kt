package io.github.yusaka39.goldenweek.module

import kotlinx.io.PrintWriter
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.compiledSerializer
import kotlinx.serialization.internal.defaultSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.io.OutputStream
import java.lang.IllegalArgumentException
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider

private infix fun Class<*>.isA(superClazz: Class<*>): Boolean =
        superClazz.isAssignableFrom(this)

@Provider
@Produces("application/json")
class KotlinSerializableWriter : MessageBodyWriter<Any> {
    override fun isWriteable(
            type: Class<*>?, genericType: Type?, annotations: Array<out Annotation>?,
            mediaType: MediaType?
    ): Boolean {
        return genericType?.let { this.canSerialize(it) } ?: false
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    private fun canSerialize(type: Type): Boolean {
        return when (type) {
            is GenericArrayType -> this.canSerialize(type.genericComponentType)
            is Class<*> -> type.isArray || type.kotlin.let {
                it.compiledSerializer() ?: it.defaultSerializer()
            } != null
            is ParameterizedType -> {
                val clazz = type.rawType as Class<*>
                when {
                    clazz isA List::class.java || clazz isA Set::class.java  ->
                        this.canSerialize(type.actualTypeArguments[0])
                    clazz isA Map::class.java -> type.actualTypeArguments.let {
                        this.canSerialize(it[0]) && this.canSerialize(it[1])
                    }
                    else -> false
                }
            }
            else -> false
        }
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    override fun writeTo(
            t: Any?, type: Class<*>?, genericType: Type?, annotations: Array<out Annotation>?,
            mediaType: MediaType?, httpHeaders: MultivaluedMap<String, Any>?,
            entityStream: OutputStream?
    ) {
        PrintWriter(entityStream).use {
            t ?: return it.print("null")
            val serializer = type?.kotlin?.serializer() as? KSerializer<Any>
                    ?: throw IllegalArgumentException()
            it.print(Json.stringify(serializer, t))
        }
    }
}
