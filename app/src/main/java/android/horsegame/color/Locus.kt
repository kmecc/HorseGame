package android.horsegame.color

interface Locus {
    val strings: List<String> // strings within a single list must be the same length
    val weights: List<Int>

    class Extension : Locus {
        override val strings = listOf("B", "D", "E", "e")
        override val weights = listOf(25, 1, 59, 15)
    }
    class Agouti : Locus {
        override val strings = listOf("+", "A", "t", "a")
        override val weights = listOf(1, 84, 5, 10)
    }
    class Creme : Locus {
        override val strings = listOf("C","p","c")
        override val weights = listOf(94, 1, 5)
    }
    class Flaxen : Locus {
        override val strings = listOf("F","f")
        override val weights = listOf(75, 25)
    }
    class Champagne : Locus {
        override val strings = listOf("Ch","ch")
        override val weights = listOf(1, 99)
    }
    class Dun : Locus {
        override val strings = listOf("D","d","n")
        override val weights = listOf(5, 94, 1)
    }
    class Shade : Locus {
        override val strings = listOf("1","2","3")
        override val weights = listOf(1,1,1)
    }
    class Silver : Locus {
        override val strings = listOf("Z","z")
        override val weights = listOf(1,99)
    }
    class Splash : Locus {
        override val strings = listOf("SW1","sw1")
        override val weights = listOf(1,99)
    }
    class KIT : Locus {
        override val strings = listOf("T","R","+")
        override val weights = listOf(5,5,90)
    }
    class Mealy: Locus {
        override val strings = listOf("Pa","pa")
        override val weights = listOf(10,90)

    }
}