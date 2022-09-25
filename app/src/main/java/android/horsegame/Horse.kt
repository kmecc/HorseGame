package android.horsegame

import android.content.Context
import android.horsegame.color.HorseColor
import android.horsegame.data.GameData
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

private const val NUM_IMAGE_LAYERS = 5

//@Entity(tableName = "horse")
open class Horse (
    val dob: Int = GameState.date - (3..16).random(),
    val isMale: Boolean = Actions.weightedTrueFalse(),
    val color: HorseColor = HorseColor(),
    val sireId: Int = -1,
    val damId: Int = -1,
) {
//    @PrimaryKey
    var id: Int = Inventory.allHorses.size + 1
    var name: String = GameData.adjectives.random().replaceFirstChar { it.uppercase() } + " " + GameData.nouns.random()
    var isEntire: Boolean = true
    var isPregnant: Boolean = false
        set(value) {
            if (isMale) Log.d("MY HORSE", "Attempting to set isPregnant for a male horse id: $id name: $name to value: $value")
            field = value
        }
    var isAlive: Boolean = true
    var isMine: Boolean = true
    var position: Int = Inventory.nextPosition++
    val isBorn: Boolean
        get() = age >= 0

    val gender: Gender
        get() = when (isMale) {
            true -> if (!isEntire) Gender.Gelding else if (age < 4) Gender.Colt else Gender.Stallion
            false -> if (age < 4) Gender.Filly else Gender.Mare
            }
    enum class Gender { Colt, Stallion, Filly, Mare, Gelding }

    val age: Int
        get() = GameState.date - dob

    private val agePhrase: String
        get() = if (!isAlive) "deceased" else when (age) {0 -> "foal" 1 -> "yearling" else -> "$age year old" }

    val isBreedable: Boolean
        get() = isAlive && isEntire && !isPregnant && age >= 4

    private val posessionString: String
        get() = if (isMine) "You own this horse" else "This horse is living wild"

    var notes: String = "No notes for this horse."

    val isCatchable: Boolean
        get() = isAlive && !isMine && age >= 1

    fun shortDesc(): String = if (GameData.useSmallImages) {
        "$name\n${agePhrase.replace("year old","y/o")} ${gender.toString().lowercase()}${if (isPregnant) " i/f" else ""}"
    } else {
        "$name\n$agePhrase ${gender.toString().lowercase()}${if (isPregnant) " in foal" else ""}"
    }

    fun longDesc(): String = """
            Owner: $posessionString
            Sex: ${gender.toString().lowercase()} ${if (isPregnant) "(in foal)" else ""}
            Age: ${agePhrase.replace("year ","years ")}
            Color: ${color.color} 
            Genome: ${color.genome.joinToString("-")}
            Sire: ${Inventory.allHorses[sireId]?.name ?: "unknown"}
            Dam: ${Inventory.allHorses[damId]?.name ?: "unknown"}
            Notes: $notes
        """.trimIndent()

    fun userMakeAddFoal(other: Horse) {
        if (this.isMale) this.makeFoal(other).addFoal() else other.makeFoal(this).addFoal()
    }

    fun makeFoal(other: Horse, makeMale: Boolean? = null): Horse {

        val foalGenome = mutableListOf<String>()
        val sireSideLink: Int? = if (Actions.weightedTrueFalse(97, 3)) (0..1).random() else null
        val damSideLink: Int? = if (Actions.weightedTrueFalse(97, 3)) (0..1).random() else null

        this.color.genome.forEachIndexed { index, sireGene ->
            val damGene = other.color.genome[index]
            val half = sireGene.length / 2
            val chooseSire: Int?
            val chooseDam: Int?

            if (index == 0 || index == 9) { // extension and kit loci are linked 97% of the time
                chooseSire = sireSideLink ?: (0..1).random()
                chooseDam = damSideLink ?: (0..1).random()
            } else {
                chooseSire = (0..1).random()
                chooseDam = (0..1).random()
            }
            val alleleFromSire = sireGene.chunked(half)[chooseSire]
            val alleleFromDam = damGene.chunked(half)[chooseDam]

            val foalGene = alleleFromSire + alleleFromDam
            foalGenome.add(foalGene)
        }

        return Horse(
            dob = GameState.date + 1,
            isMale = makeMale ?: Actions.weightedTrueFalse(),
            color = HorseColor(foalGenome),
            sireId = id,
            damId = other.id,
        )
    }

    fun addHorse(): Horse {
        Inventory.allHorses += this.id to this
        return this
    }
    fun addMustang(): Horse {
        isMine = false
        Inventory.allHorses += this.id to this
        return this
    }

    fun addFoal() {
        name = "unnamed #${Inventory.nextHomebred++}"
        Inventory.allHorses += this.id to this
        Inventory.allHorses[damId]!!.isPregnant = true
    }

    fun addMustangFoal() {
        isMine = false
        Inventory.allHorses += this.id to this
        Inventory.allHorses[damId]!!.isPregnant = true
    }

    fun kill(ageCutoff: Int): Boolean {
        val deathOdds = age - ageCutoff - 1
        val isDead = !Actions.weightedTrueFalse(9, deathOdds)
        if (isDead) Inventory.allHorses[id]!!.isAlive = false
        return isDead
    }

    fun catch() {
        isMine = true
        position = Inventory.nextPosition++
        if (!isMale) Inventory.getFoals().filter{ it.damId == id }
            .takeUnless { it.isEmpty() }?.get(0)
            .apply{
                isMine = true
                position = Inventory.nextPosition++
            }
    }

    fun customizeFrame(context: Context, view: View, frameInt: Int) {
        view.findViewById<FrameLayout>(frameInt).apply { setBackgroundColor(color.colorInt) }

        if (color.imageInts.size > NUM_IMAGE_LAYERS) {
            Log.d("GENOMES IMAGES","Horse $id $name has to display ${color.imageInts.size} images for coat color ${color.color}")
        }

        for (i in 0 until NUM_IMAGE_LAYERS) {
            val imageInt = color.imageInts.getOrNull(i)
            if (imageInt == null) view.findViewById<ImageView>(i).setImageDrawable(null)
            else {
                val drawable = AppCompatResources.getDrawable(context, imageInt)
                view.findViewById<ImageView>(i).setImageDrawable(drawable)
            }

        }
    }

    fun frame(context: Context, view: View, frameInt: Int, width: Int? = null) {
        val layout = view.findViewById<FrameLayout>(frameInt)!!.apply {
            setBackgroundColor(color.colorInt)
            if (width != null) {
                layoutParams.width = width
                layoutParams.height = (width * .75).toInt()
            }
        }

        if (color.imageInts.size > NUM_IMAGE_LAYERS) {
            Log.d("GENOMES IMAGES","Horse $id $name has to display ${color.imageInts.size} images for coat color ${color.color}")
        }

        color.imageInts.forEach { imageInt ->
            val drawable = AppCompatResources.getDrawable(context, imageInt)
            val layer = ImageView(context).apply {
                setImageDrawable(drawable)
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_XY
            }
            layout.addView(layer)
        }
    }

    companion object {
        fun setupFrame(context: Context, view: View, frameInt: Int, width: Int? = null) {

            val layout = view.findViewById<FrameLayout>(frameInt)!!.apply {
                if (width != null) {
                    layoutParams.width = width
                    layoutParams.height = (width * .75).toInt()
                }
            }

            for (i in 0 until NUM_IMAGE_LAYERS) {
                val layer = ImageView(context).apply {
                    id = i
                    layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.FIT_XY
                }
                layout.addView(layer)
            }
        }
    }
}