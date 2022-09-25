package android.horsegame

import android.horsegame.data.GameData
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

private const val ARG_HORSE_INDEX = "horsegame.horse_index"

class Pedigree : Fragment() {

    private var horseIndex: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { horseIndex = it.getInt(ARG_HORSE_INDEX) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_pedigree, container, false)

        val detailHorse = Inventory.allHorses[horseIndex]!!

        val pedigree = buildList {
            GameData.pedigree_resource_names.forEach {
                add(MutableList<Horse>(it.size) { Inventory.emptyHorse })
            }
        }

        var ancestor: Horse
        var thisHorse: Horse

        GameData.pedigree_resource_names.forEachIndexed { j, colList ->

            var pairCount = 0

            colList.forEachIndexed { i, textView ->

                val frameInt = GameData.pedigree_resource_images[j][i]
                val isMaleSpot = i % 2 == 0
                ancestor = if (j == 0) detailHorse
                else pedigree[j - 1][pairCount]
                val id = if (isMaleSpot) ancestor.sireId else ancestor.damId
                thisHorse = Inventory.allHorses[id] ?: Inventory.dummyHorse

                pedigree[j][i] = thisHorse

                v.findViewById<TextView>(textView).text = thisHorse.name
                thisHorse.frame(requireContext(), v, frameInt)

                v.findViewById<FrameLayout>(frameInt).setOnClickListener {
//                    val id = pedigree[j][i].id // magic
                    if (id != -1) {
                        Log.d("ONCLICK","Id: $id Info: ${Inventory.allHorses[id]!!.name}")
                        val fragment = Info.newInstance(id)
                        parentFragmentManager
                            .beginTransaction()
                            .addToBackStack(null)
                            .hide(this)
                            .add(R.id.detail_fragment_container, fragment)
                            .commit()
                    } // else invalidClickAnimation(it)
                }

                if (!isMaleSpot) pairCount++
            }
        }

        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(horseIndex: Int) =
            Pedigree().apply { arguments = Bundle().apply { putInt(ARG_HORSE_INDEX, horseIndex) } }
    }

//    private fun invalidClickAnimation(v: View) {
//        val animShake: Animation =
//            AnimationUtils.loadAnimation(this, R.anim.shake)
//        v.startAnimation(animShake)
//    }
}