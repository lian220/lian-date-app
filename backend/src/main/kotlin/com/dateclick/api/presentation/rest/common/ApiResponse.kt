package com.dateclick.api.presentation.rest.common

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ApiError?,
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> =
            ApiResponse(
                success = true,
                data = data,
                error = null,
            )

        fun <T> error(
            code: String,
            message: String,
        ): ApiResponse<T> =
            ApiResponse(
                success = false,
                data = null,
                error = ApiError(code, message),
            )
    }
}

data class ApiError(
    val code: String,
    val message: String,
)
