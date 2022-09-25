package android.horsegame

class GameState {
    companion object {

        var date = 1

        fun reset() {
            date = 1
            Inventory.reset()
        }

        fun advanceYear(): String {
            date++
            birthFoals()
            mustangLife()
            return killOldHorses().joinToString(", ")
        }

        fun birthFoals() {
            Inventory.getPregnantHorses().forEach {
                Inventory.allHorses[it.id]!!.isPregnant = false
            }
            Inventory.getFoals().forEach {
                Inventory.allHorses[it.id]!!.position = Inventory.nextPosition++
            }
        }

        fun killOldHorses(): List<String> {
            var ageCutoff = 15
            Inventory.getMustangs().filter { it.age >= ageCutoff && it.isAlive }.forEach { it.kill(ageCutoff) }

            ageCutoff = 20
            val killList = mutableListOf<String>()
            Inventory.getMine().filter { it.age >= ageCutoff && it.isAlive }.forEach {
                if (it.kill(ageCutoff)) killList.add(it.name)
            }
            return killList
        }

        fun mustangLife() {
            val mustangs = Inventory.getMustangs()

            if (mustangs.isEmpty()) {
                repeat(50) {
                    Horse(isMale = true).addMustang()
                    Horse(isMale = false).addMustang()
                }
                return
            }

            val difference = if (100 - mustangs.size > 0) 100 - mustangs.size else 0
            val stallions = mustangs.filter { it.isMale && it.isBreedable }
            val mares = mustangs.filter { !it.isMale && it.isBreedable }

            if (mares.isNotEmpty() && stallions.isNotEmpty()) {
                repeat (difference) {
                    stallions.random().makeFoal(mares.random()).addMustangFoal()
                }
            }

            Horse().addMustang()
        }
    }
}