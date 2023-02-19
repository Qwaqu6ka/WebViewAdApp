package bk.liga.stavok.ligastavok

import android.app.Application
import com.onesignal.OneSignal

private const val ONESIGNAL_APP_ID = "56bf9c2b-abb5-47d0-bb23-dfb290156e91"

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}