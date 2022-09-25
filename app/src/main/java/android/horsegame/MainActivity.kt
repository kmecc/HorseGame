package android.horsegame

import android.content.Intent
import android.content.res.Configuration
import android.horsegame.data.GameData
//import android.horsegame.database.HorseDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val EXTRA_HORSE_INDEX = "horsegame.horse_index"
private const val EXTRA_SELECT_BREEDING = "horsegame.select_breeding"

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val isBreedingSelect: Boolean by lazy { intent.getBooleanExtra(EXTRA_SELECT_BREEDING, false) }
    private val currentHorseId: Int by lazy { intent.getIntExtra(EXTRA_HORSE_INDEX, -1) }
    private val currentHorse: Horse by lazy { Inventory.allHorses[currentHorseId]!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        updateRecyclerView()

//        val repo = HorseRepository.get()
//        repo.clear()

//        val hrse = Inventory.allHorses.values.toList().get(0)
//        Log.d("MY DATABASE","horse id: ${hrse.id}")
//        repo.addHorse(hrse)

//        Inventory.allHorses.values.toList().forEach {
//            Log.d("MY DATABASE","horse id: ${it.id} horse name: ${it.name}")
//            repo.addHorse(it)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isBreedingSelect) menuInflater.inflate(R.menu.activity_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_new_horse -> userSelectAddHorse()
            R.id.menu_advance_year -> userSelectAdvanceYear()
            R.id.menu_reset -> userSelectReset()
            R.id.menu_toggle_image_size -> userSelectToggleImageSize()
            else -> return super.onOptionsItemSelected(item)
        }
        updateRecyclerView()
        return true
    }

    override fun onResume() {
        updateRecyclerView()
        super.onResume()
    }

    private fun userSelectAddHorse() { Actions.catchMustangs() }

    private fun userSelectAdvanceYear() {
        GameState.advanceYear().also { s -> if (s.isNotBlank()) Toast.makeText(this, "Died of old age: $s", Toast.LENGTH_SHORT).show() }
        logGenomes()
    }

    private fun userSelectReset() = GameState.reset()

    private fun userSelectToggleImageSize() {
        GameData.useSmallImages = !GameData.useSmallImages
        GameData.span = null
        recyclerView.adapter = null
    }

    private fun updateRecyclerView() {
        val data: List<Horse> = selectData().sortedBy { it.position }
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager = GridLayoutManager(this, setHowManyThumbnailsFit())
            val width = if (GameData.useSmallImages) resources.getDimension(R.dimen.recycler_image_width_small).toInt() else null
            HorseAdapter(this, data, { horse: Horse -> recyclerOnClick(horse) }, width)
                .apply { stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY }
                .also { recyclerView.adapter = it }
        } else {
            (recyclerView.adapter as HorseAdapter).apply {
                setItems(data)
                notifyDataSetChanged()
            }
        }
    }

    private fun recyclerOnClick(horse : Horse) {
        if (isBreedingSelect) {
            currentHorse.userMakeAddFoal(horse)
            Toast.makeText(this,"Foal due next year", Toast.LENGTH_LONG).show()
            finish()
        }
        else startDetailActivity(horse)
    }

    private fun startDetailActivity(horse: Horse) {
        Intent(this, DetailActivity::class.java)
            .apply { putExtra(EXTRA_HORSE_INDEX, horse.id) }
            .also { startActivity(it) }
    }

    private fun selectData(): List<Horse> {
        return if (isBreedingSelect) Inventory.getMine().filter { it.isMale == !currentHorse.isMale && it.isBreedable }.toList()
        else Inventory.getMine().toList()
    }

    private fun getHowManyThumbnailsFit(screenWidthDP: Int): Int {
        var number = 0
        val mainPaddingDP = resources.getDimension(R.dimen.main_padding) / resources.displayMetrics.density
        val thumbWidthDP = if (GameData.useSmallImages) resources.getDimension(R.dimen.recycler_image_width_small) / resources.displayMetrics.density
        else resources.getDimension(R.dimen.recycler_image_width) / resources.displayMetrics.density
        val margin = resources.getDimension(R.dimen.card_margin) / resources.displayMetrics.density
        val corner = resources.getDimension(R.dimen.card_corner) / resources.displayMetrics.density
        val innerPadding = resources.getDimension(R.dimen.card_padding) / resources.displayMetrics.density

        while (true) {
            val totalPadding = (mainPaddingDP * 2) + (margin * 2 * number) +
                    (corner * number) - (innerPadding * 2 * number)
            val totalWidth = screenWidthDP - totalPadding
            val newNumber = (totalWidth / thumbWidthDP).toInt()
            if (newNumber <= number) {
                number = newNumber
                break
            }
            else number = newNumber
        }
        return number
    }

    private fun setHowManyThumbnailsFit(): Int {
        if (GameData.span == null) {
            when (resources.configuration.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> {
                    val portraitSpan = getHowManyThumbnailsFit(resources.configuration.screenWidthDp)
                    val landscapeSpan = getHowManyThumbnailsFit(resources.configuration.screenHeightDp)
                    GameData.span = Pair(portraitSpan, landscapeSpan)
                }
                Configuration.ORIENTATION_LANDSCAPE -> {
                    val portraitSpan = getHowManyThumbnailsFit(resources.configuration.screenHeightDp)
                    val landscapeSpan = getHowManyThumbnailsFit(resources.configuration.screenWidthDp)
                    GameData.span = Pair(portraitSpan, landscapeSpan)
                }
                else -> { }
            }
        }
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> GameData.span!!.first
            Configuration.ORIENTATION_LANDSCAPE -> GameData.span!!.second
            else -> getHowManyThumbnailsFit(resources.configuration.screenWidthDp)
        }
    }

    private fun logGenomes() {
        Inventory.allHorses.filterValues { it.isAlive }.values.forEach {
            val log = buildString {
                val str = it.color.genome.joinToString("-")
                val mf = if (it.isMale) "male  " else "female"
                val name = it.name.padEnd(21)
                append("$str $mf Pos: ${it.position} ID: ${it.id} Name: $name Sire: ${it.sireId} Dam: ${it.damId}\n")
            }
            Log.d("MY GENOME", log)
        }
        Log.d("MY NUM TOTAL", "${Inventory.allHorses.filterValues { it.isAlive }.size}")
        Log.d("MY NUM MUSTANGS", "${Inventory.getMustangs().size}")
    }
}