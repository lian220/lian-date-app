package com.dateclick.api.domain.place.vo

data class BusinessHours(
    val day: String,
    val open: String?,
    val close: String?
) {
    init {
        require(isClosed || (open != null && close != null)) {
            "BusinessHours는 open/close가 둘 다 null(isClosed) 이거나 둘 다 non-null이어야 합니다. 현재 open=$open, close=$close"
        }
    }

    val isClosed: Boolean
        get() = open == null && close == null
}
