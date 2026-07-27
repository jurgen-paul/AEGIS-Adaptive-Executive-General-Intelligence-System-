package com.example.service

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.UUID

/**
 * OkHttp Interceptor that inspects outbound API requests and ensures
 * all AEGIS security headers are attached to align with security-first protocols.
 */
class AegisSecurityHeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()

        // Attach security headers if not already present
        if (originalRequest.header("X-Aegis-Security-Level") == null) {
            requestBuilder.addHeader("X-Aegis-Security-Level", "STRICT")
        }
        if (originalRequest.header("X-Aegis-Request-ID") == null) {
            requestBuilder.addHeader("X-Aegis-Request-ID", UUID.randomUUID().toString())
        }
        if (originalRequest.header("X-Aegis-Client-Ver") == null) {
            requestBuilder.addHeader("X-Aegis-Client-Ver", "1.0.0-AEGIS")
        }
        if (originalRequest.header("User-Agent") == null) {
            requestBuilder.addHeader("User-Agent", "AEGIS-Executive-Security-Assistant/1.0")
        }
        if (originalRequest.header("X-Content-Type-Options") == null) {
            requestBuilder.addHeader("X-Content-Type-Options", "nosniff")
        }

        val secureRequest = requestBuilder.build()
        return chain.proceed(secureRequest)
    }
}
