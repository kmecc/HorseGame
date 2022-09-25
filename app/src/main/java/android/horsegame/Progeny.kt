package android.horsegame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_HORSE_INDEX = "horsegame.horse_index"

class Progeny : Fragment() {

    private var horseIndex: Int? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { horseIndex = it.getInt(ARG_HORSE_INDEX) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_progeny, container, false)

        recyclerView = v.findViewById(R.id.progeny_recycler_view)

        val progeny = Inventory.getProgeny(horseIndex!!)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val data: List<Horse> = progeny.sortedBy { it.position }
        HorseAdapter(requireContext(), data, { horse: Horse -> recyclerOnClick(horse) })
            .apply { stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY }
            .also { recyclerView.adapter = it }

        return v
    }

    companion object {
        @JvmStatic
        fun newInstance(horseIndex: Int) =
            Progeny().apply { arguments = Bundle().apply { putInt(ARG_HORSE_INDEX, horseIndex) } }
    }

    private fun recyclerOnClick(horse: Horse) {
        val fragment = Info.newInstance(horse.id)
        parentFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .hide(this)
            .add(R.id.detail_fragment_container, fragment)
            .commit()
    }
}
