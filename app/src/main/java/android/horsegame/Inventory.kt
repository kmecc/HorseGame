package android.horsegame

import android.horsegame.color.HorseColor
import kotlinx.coroutines.coroutineScope

class Inventory {
    companion object {

        val allHorses = mutableMapOf<Int, Horse>()
        var nextPosition = Integer.MIN_VALUE
        var nextHomebred = 1

        init {
            reset()
        }

        val dummyHorse =
            Horse(
                color = HorseColor(_colorTriple = Triple("white","FFFFFF",listOf("horse")))
            ).apply{ name = "unknown" }
        val emptyHorse = Horse().apply{ name = "empty" }

//        fun addHorse(horse: Horse) {
//            horseRepository.addHorse(horse)
//        }
        fun reset() {
            allHorses.clear()

//            repeat(50) {
//                Horse(isMale = true).addMustang()
//                Horse(isMale = false).addMustang()
//            }
//            var makeMale = true
//            val foalList = mutableListOf<Horse>()
//            repeat(8) {
//                Horse(isMale = true).addHorse()
//                    .makeFoal(Horse(isMale = false).addHorse(), makeMale)
//                    .also { foalList.add(it) }
//                    .addFoal()
//                makeMale = !makeMale
//            }
//
//            repeat(5) { GameState.advanceYear() }
//            val foalList2 = mutableListOf<Horse>()
//            makeMale = true
//            for (i in 0 until foalList.size step 2) {
//                val sire = foalList[i]
//                val dam = foalList[i + 1]
//                sire.makeFoal(dam, makeMale)
//                    .also { foalList2.add(it) }
//                    .addFoal()
//                makeMale = !makeMale
//            }
//
//            foalList.clear()
//            makeMale = true
//            repeat(5) { GameState.advanceYear() }
//            for (i in 0 until foalList2.size step 2) {
//                val sireId = i
//                val damId = i+1
//                val sire = foalList2[i]
//                val dam = foalList2[i + 1]
//                sire.makeFoal(dam, makeMale)
//                    .also { foalList.add(it) }
//                    .addFoal()
//                makeMale = !makeMale
//            }
//
//            repeat(5) { GameState.advanceYear() }
//            makeMale = true
//            for (i in 0 until foalList.size step 2) {
//                val sireId = i
//                val damId = i+1
//                val sire = foalList[i]
//                val dam = foalList[i + 1]
//                sire.makeFoal(dam, makeMale)
//                    .addFoal()
//                makeMale = !makeMale
//            }
//            repeat(50) { GameState.advanceYear() }
            repeat(10) {
                val horse = Horse()
                horse.addHorse()
            }
        }

        fun getMine(): Collection<Horse> =
            allHorses.filterValues { it.isMine && it.isAlive && it.isBorn }.values

        fun getMustangs(): Collection<Horse> =
            allHorses.filterValues { !it.isMine && it.isAlive && it.isBorn }.values

        fun getPregnantHorses(): Collection<Horse> =
            allHorses.filterValues { it.isPregnant }.values

        fun getFoals(): Collection<Horse> =
            allHorses.filterValues { it.age == 0 }.values

        fun getProgeny(parentId: Int) =
            allHorses.filterValues { it.isBorn && (it.sireId == parentId || it.damId == parentId) }.values

//        private val horseRepository = HorseRepository.get()
//        val horses = horseRepository.getAll()

    }
}