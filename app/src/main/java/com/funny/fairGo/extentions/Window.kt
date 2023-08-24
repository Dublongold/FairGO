package com.funny.fairGo.extentions

import android.view.Window

var Window.flags: Pair<Int, Int>?
    get() = null
    set(value) {
        if(value != null) setFlags(value.first, value.second)
    }