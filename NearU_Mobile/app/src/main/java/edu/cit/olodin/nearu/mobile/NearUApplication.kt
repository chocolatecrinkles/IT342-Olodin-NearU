package edu.cit.olodin.nearu.mobile

import android.app.Application
import edu.cit.olodin.nearu.mobile.api.RetrofitClient

class NearUApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        RetrofitClient.init(this)
    }
}