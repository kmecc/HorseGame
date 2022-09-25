package android.horsegame

object Actions {

    fun weightedRandom(strings: List<String>, weights: List<Int>): String {
        if (strings.size != weights.size) throw Exception("Improperly defined weighted random values.")
        val total = weights.sum()
        val rand = (1..total).random()
        var cumul = 0
        for (i in 0..weights.lastIndex) {
            cumul += weights[i]
            if (rand <= cumul) return strings[i]
        }
        throw Exception("Didn't find random value $rand in weight list $weights")
    }

    fun weightedTrueFalse(trueWeight: Int = 1, falseWeight: Int = 1): Boolean {
        val rand = (1..trueWeight + falseWeight).random()
        return rand <= trueWeight
    }

    fun catchMustangs(): MutableList<Int> {

        fun makeMine(id: Int) {
            Inventory.allHorses[id]!!.apply {
                isMine = true
                position = Inventory.nextPosition++
            }
        }

        val retVal = mutableListOf<Int>()
        val mustangId: Int = Inventory.getMustangs().filter{ it.age >=1 }.takeUnless { it.isEmpty() }?.random()?.id ?: Horse().addMustang().id
        makeMine(mustangId)
        retVal.add(mustangId)

        if (Inventory.allHorses[mustangId]!!.isPregnant) {
            val foalId: Int = Inventory.allHorses.filterValues { foal -> foal.damId == mustangId && !foal.isBorn }.values.toList().first().id
            makeMine(foalId)
            retVal.add(foalId)
        }
        return retVal
    }
}