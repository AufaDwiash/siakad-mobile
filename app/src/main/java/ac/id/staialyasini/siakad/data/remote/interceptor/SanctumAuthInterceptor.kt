package ac.id.staialyasini.siakad.data.remote.interceptor

import ac.id.staialyasini.siakad.data.local.SessionDataStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Interceptor untuk menyisipkan Bearer Token Laravel Sanctum
 * dan header JSON standar pada setiap request ke Backend Laravel 13.
 */
@Singleton
class SanctumAuthInterceptor @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .header("X-Requested-With", "XMLHttpRequest")
            .header("X-Client-Platform", "SIAKAD-AlYasini-Android-Native")

        val token = sessionDataStore.cachedToken
        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
