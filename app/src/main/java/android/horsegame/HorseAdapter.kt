package android.horsegame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class HorseAdapter(
    val context: Context,
    private var data: List<Horse>,
    val clickListener: (Horse) -> Unit,
    private val smallImageWidth: Int? = null
) : RecyclerView.Adapter<HorseAdapter.HorseHolder>() {

    inner class HorseHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val textView = view.findViewById<TextView>(R.id.recycler_text)!!
        private val cardView = view.findViewById<CardView>(R.id.recycler_card)!!

        fun bind(horse: Horse) {
            textView.text = horse.shortDesc()
            cardView.setOnClickListener { clickListener(horse) }
            horse.customizeFrame(context, view, R.id.recycler_frame)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        if (smallImageWidth != null) { view.findViewById<TextView>(R.id.recycler_text)!!.textSize = 12F }
        Horse.setupFrame(context, view, R.id.recycler_frame, smallImageWidth)
        return HorseHolder(view)
    }

    override fun onBindViewHolder(holder: HorseHolder, position: Int) {
        val horse = data[position]
        holder.bind(horse)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setItems(_data: List<Horse>) {
        data = _data
    }
}