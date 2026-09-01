package com.huevo.app

import android.app.Application
import com.huevo.app.data.HuevoRepository

class HuevoApplication : Application() {
    val repository: HuevoRepository by lazy { HuevoRepository(this) }
}
