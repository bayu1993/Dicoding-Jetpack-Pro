package com.example.dicodingjetpackpro.repository.remote

import com.example.dicodingjetpackpro.api.ApiService
import com.example.dicodingjetpackpro.base.BaseDataSource
import com.example.dicodingjetpackpro.base.ResourceState
import com.example.dicodingjetpackpro.base.ResponseWrapper
import retrofit2.Response
import java.net.UnknownHostException

class RemoteDataSource(private val apiService: ApiService) : BaseDataSource() {
    private suspend fun <T> getResult(request: suspend () -> Response<T>): ResourceState<ResponseWrapper<T>> {
        return try {
            val response = request()
            val body = response.body()
            if (response.isSuccessful.not() || body == null) {
                return errorState(response.code(), response.message())
            }

            return ResourceState.Success(
                ResponseWrapper(
                    null,
                    null,
                    body,
                    null
                )
            )
        } catch (e: Exception) {
            errorState(msg = if (e is UnknownHostException) NO_INTERNET else e.localizedMessage.orEmpty())
        }
    }

    suspend fun getDiscoverMovies() = suspendDataResult { getResult { apiService.getDiscoverMovies() } }

    suspend fun getDiscoverTvs() = suspendDataResult { getResult { apiService.getDiscoverTvs() } }

    suspend fun getMovieDetail(movieId: String) = suspendDataResult { getResult { apiService.getMovieDetail(movieId) } }

    suspend fun getSimilarMovies(movieId: String) = suspendDataResult { getResult { apiService.getSimilarMovies(movieId) } }

    companion object {
        const val NO_INTERNET = "No internet connection!"
    }
}