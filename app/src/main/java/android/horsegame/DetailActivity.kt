package android.horsegame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

private const val EXTRA_HORSE_INDEX = "horsegame.horse_index"

class DetailActivity : AppCompatActivity() {

    private var horseIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        horseIndex = intent.getIntExtra(EXTRA_HORSE_INDEX, -1)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.detail_fragment_container)
        if (currentFragment == null) {
            val fragment = Info.newInstance(horseIndex)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.detail_fragment_container, fragment)
                .commit()
        }
    }

}