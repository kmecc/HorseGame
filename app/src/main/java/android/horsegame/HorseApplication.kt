package android.horsegame

import android.app.Application
import androidx.appcompat.content.res.AppCompatResources

class HorseApplication : Application() {

    override fun onCreate() { // Application.onCreate() is called by the system when the app is first loaded into memory
        super.onCreate()      // so it is a good place to do any one-time initialization operations

//        HorseRepository.initalize(this)

        AppCompatResources.getDrawable(this, R.drawable.flaxen_mane)!!.apply{
            alpha = 245
        }
        AppCompatResources.getDrawable(this, R.drawable.silver_mane)!!.apply{
            alpha = 245
        }
    }
}