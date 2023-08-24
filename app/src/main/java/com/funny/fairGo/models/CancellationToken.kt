package com.funny.fairGo.models

class CancellationToken {
    var isCancelled: Boolean = false
        set(_) {
            field = true
        }
    fun cancel() {
        isCancelled = true
    }
}