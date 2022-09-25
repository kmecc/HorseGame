package android.horsegame

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

private const val ARG_HORSE_INDEX = "horsegame.horse_index"
private const val EXTRA_HORSE_INDEX = "horsegame.horse_index"
private const val EXTRA_SELECT_BREEDING = "horsegame.select_breeding"

class Info : Fragment() {

    private var horseIndex: Int? = null
    private var editMode = false
    private lateinit var nameText: TextView
    private lateinit var detailText: TextView
    private lateinit var editName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let { horseIndex = it.getInt(ARG_HORSE_INDEX) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_info, container, false)

        val horse = Inventory.allHorses[horseIndex]!!

        horse.frame(requireContext(), v, R.id.detail_frame)

        nameText = v.findViewById<TextView>(R.id.detail_name).apply { text = horse.name }
        editName = v.findViewById<EditText>(R.id.detail_edit_name).apply { hint = horse.name }
        detailText = v.findViewById<TextView>(R.id.detail_text).apply { text = horse.longDesc() }
        if (!horse.isAlive) detailText.setTextColor(Color.parseColor("#AAAAAA"))

        editName.setOnKeyListener(View.OnKeyListener { _, keyCode, event -> // If the event is a key-down event on the "enter" button
            if (event.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                userSelectSave()
                return@OnKeyListener true
            }
            false
        })

        v.findViewById<Button>(R.id.button_pedigree).setOnClickListener {
            val fragment = Pedigree.newInstance(horseIndex!!)
            parentFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .hide(this)
                .add(R.id.detail_fragment_container, fragment)
                .commit()
        }

        v.findViewById<Button>(R.id.button_progeny).setOnClickListener {
            val fragment = Progeny.newInstance(horseIndex!!)
            parentFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .hide(this)
                .add(R.id.detail_fragment_container, fragment)
                .commit()
        }

        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_info, menu)
        val horse = Inventory.allHorses[horseIndex]!!
        if (horse.isMine && !editMode) { return }
        else {
            menu.findItem(R.id.menu_breed).isVisible = false
            menu.findItem(R.id.menu_edit).isVisible = false
            menu.findItem(R.id.menu_set_free).isVisible = false
            menu.findItem(R.id.menu_save_edit).isVisible = editMode
            menu.findItem(R.id.menu_catch).isVisible = horse.isCatchable
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_breed -> userSelectBreed()
            R.id.menu_edit -> swapMode()
            R.id.menu_save_edit -> userSelectSave()
            R.id.menu_set_free -> userSelectSetFree()
            R.id.menu_catch -> userSelectCatch()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance(horseIndex: Int) =
            Info()
                .apply { arguments = Bundle().apply { putInt(ARG_HORSE_INDEX, horseIndex) } }
                .also { Log.d("FRAGMENT","Info newInstance") }
    }

    private fun userSelectBreed() {
        val horse = Inventory.allHorses[horseIndex]!!
        if (horse.isBreedable) {
            Toast.makeText(activity, "Choose a partner for this horse", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, MainActivity::class.java)
                .apply { putExtra(EXTRA_HORSE_INDEX, horseIndex) }
                .apply { putExtra(EXTRA_SELECT_BREEDING, true) }
            startActivity(intent)
            detailText.text = horse.longDesc()
        } else {
            whyNotBreedable(horse).also{ Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun whyNotBreedable(horse: Horse): String {
        return if (!horse.isEntire) "Geldings cannot breed"
        else if (horse.age < 4) "Horse must be 4 years old or older to breed"
        else if (horse.isPregnant) "Horse is already in foal"
        else throw Exception()
    }

    private fun userSelectSave() {
        val newName = editName.text.toString()
        if (newName.matches("[a-zA-Z ]{1,21}".toRegex())) {
            Inventory.allHorses[horseIndex]!!.name = newName
            nameText.text = newName
            hideKeyboard()
            swapMode()
        } else if (newName.isBlank()) {
            hideKeyboard()
            swapMode()
        }
        else Toast.makeText(context, "Name must be 21 or fewer letters and spaces",Toast.LENGTH_SHORT).show()
    }

    private fun swapMode() {
        editMode = !editMode
        requireActivity().invalidateOptionsMenu()
        nameText.isVisible = !nameText.isVisible
        editName.isVisible = !editName.isVisible
        val constraintLayout = view?.findViewById<ConstraintLayout>(R.id.detail_constraint)
        val anchor = if (editMode) R.id.detail_edit_name else R.id.detail_name
        ConstraintSet().apply {
            clone(constraintLayout)
            connect(R.id.detail_text, ConstraintSet.TOP, anchor, ConstraintSet.BOTTOM)
            applyTo(constraintLayout)
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun userSelectSetFree() {
        Inventory.allHorses[horseIndex]!!.isMine = false
        detailText.text = Inventory.allHorses[horseIndex]!!.longDesc()
        Toast.makeText(context, "Horse has been set free", Toast.LENGTH_SHORT).show()
    }

    private fun userSelectCatch() {
        Inventory.allHorses[horseIndex]!!.catch()
        requireActivity().invalidateOptionsMenu()
        detailText.text = Inventory.allHorses[horseIndex]!!.longDesc()
        Toast.makeText(context, "Horse is now yours", Toast.LENGTH_SHORT).show()
    }
}