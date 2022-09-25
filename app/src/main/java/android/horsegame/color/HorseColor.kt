package android.horsegame.color

import android.graphics.Color
import android.horsegame.Actions
import android.horsegame.R

class HorseColor(_genome: List<String>? = null, _colorTriple: Triple<String, String, List<String>>? = null) {

    val color: String
    val colorInt: Int
    val imageInts: List<Int>
    val genome: List<String>

    init {
        genome = _genome ?: randomGenome()
        val colorTriple = _colorTriple ?: parseGenome()

        color = colorTriple.first
        colorInt = hexToInt(colorTriple.second)
        imageInts = colorTriple.third.map{ imgNameToInt(it) }
    }

    private fun parseGenome(): Triple<String, String, List<String>> {

        val exten = genome[0]
        val agouti = genome[1]
        val creme = genome[2]
        val flaxen = genome[3]
        val champagne = genome[4]
        val dun = genome[5]
        val shade = genome[6]
        val silver = genome[7]
        val splash = genome[8]
        val kit = genome[9]
        val mealy = genome[10]

        val colorList = mutableListOf<String>()
        val imageList = mutableListOf<String>()

        val basecolor: String = if (exten == exten.lowercase()) "chestnut"
        else if (exten.contains('D') && agouti.contains("[A+]".toRegex())) "brown black"
        else if (exten.contains("[EB]".toRegex()) && agouti.contains("[A+]".toRegex())) "bay"
        else if (exten.contains("[EB]".toRegex()) && agouti.contains('t')) "seal brown"
        else "black"
        colorList.add(basecolor)

        var shadeKey = (shade[0].toString().toInt() + shade[1].toString().toInt()) / 2

        with(creme) {
            if (contains('C') && contains('c')) colorList.add("single cream")
            else if (this == "cc") colorList.add("double cream")
            else if (this == "pp" || contains('c') && contains('p')) colorList.add("pearl")
            else { } // "Cp" has no effect
        }
        if (champagne.contains("Ch")) {
            colorList.add("champagne")
            colorList.remove("single cream")
            colorList.remove("pearl")
        }
        if (dun != dun.lowercase()) {
            colorList.add("dun")
            imageList.add("marks")
        }
        if (dun.contains('n')  || dun.contains('D')) {
            colorList.run {
                if (contains("double cream") || contains("silver") || contains("champagne" ) || contains("pearl")) imageList.add("light marks")
                else imageList.add("marks")
            }
        }

        if (silver != silver.lowercase() && basecolor != "chestnut") {
            colorList.add("silver")
        }

        var colorName = getColorName(colorList)

        if (exten.contains('B')) {
            colorName = "sooty $colorName"
            if (colorList.contains("double cream") || colorName == "ivory champagne") shadeKey = minOf(shadeKey + 1, 3)
            else imageList += "sooty"
        }

        val imageName = getImageName(colorList)
        val shadeName = getShadeName(shadeKey)
        val colorHex = getColorHex(colorList, shadeKey)

        imageList += imageName


        // special cases

        if (flaxen == flaxen.lowercase()
            && basecolor == "chestnut"
            && !imageList.contains("flaxen")
            ) {
            colorName = "flaxen $colorName"
            imageList += "flaxen"
        }

        imageList.apply {
            val wildPointsApply = exten.contains("[EB]".toRegex()) && agouti.contains('+')
            if (wildPointsApply && contains("points")) {
                remove("points")
                add("wild points")
            }
            if (wildPointsApply && contains("light points")) {
                remove("light points")
                add("light wild points")
            }
            if (contains("sooty")) {
                remove("sooty")
                add(0, "sooty")
            }
        }

        // spotting patterns
        if (kit.contains('R')) {
            colorName = "$colorName roan"
            imageList += "roan"
        }
        if (kit.contains('T')) {
            colorName = "$colorName tobiano"
            imageList.add("tobiano")
            if (imageList.contains("marks")) imageList.remove("marks")
            else if (imageList.contains("light marks")) imageList.remove("light marks")
        }
        if (splash.contains("SW1")) {
            colorName = "$colorName splash"
            imageList.add("splash")
        }

        imageList.apply { if (size > 1 && !this.contains("sooty")) remove("horse") }


        colorName = if (shadeName != "medium") "$shadeName $colorName" else colorName

        return Triple(colorName, colorHex, imageList)
    }

    private fun getColorName(colorList: List<String>): String =
        colorMap[colorList]?.get(0) ?: throw MapException(colorList.joinToString(", "))

    private fun getShadeName(shadeKey: Int): String = when (shadeKey) {
        1 -> "light"
        2 -> "medium"
        3 -> "dark"
        else -> throw Exception()
    }

    private fun getColorHex(colorList: List<String>, shadeKey: Int): String =
        colorMap[colorList]?.get(shadeKey) ?: throw MapException(colorList.joinToString(", ").plus("(shade key: $shadeKey)"))

    private fun getImageName(colorList: List<String>): String =
        colorMap[colorList]?.get(4) ?: throw MapException(colorList.joinToString(", "))

    private fun imgNameToInt(imageName: String): Int {
        return when (imageName) {
            "horse" -> R.drawable.horse
            "points" -> R.drawable.points
            "flaxen" -> R.drawable.flaxen_mane
            "silver mane" -> R.drawable.silver_mane
            "silver points" -> R.drawable.silver_points
            "light points" -> R.drawable.light_points
            "wild points" -> R.drawable.wild_points
            "light wild points" -> R.drawable.light_wild_points
            "roan" -> R.drawable.roan
            "marks" -> R.drawable.wild_marks
            "light marks" -> R.drawable.light_wild_marks
            "tobiano" -> R.drawable.tobiano
            "splash" -> R.drawable.splash
            "sooty" -> R.drawable.sooty
            "shaded mane" -> R.drawable.shaded_mane
            else -> throw Exception("Image name $imageName not found when trying to convert to resource id.")
        }
    }

    private fun hexToInt(hex: String): Int {
        val red     = hex.take(2)    .toInt(16)
        val green   = hex.substring(2,4).toInt(16)
        val blue    = hex.drop(4)    .toInt(16)
        return Color.argb(255, red, green, blue)
    }

    companion object {
        fun randomGenome(): List<String> {
            return listOf(
                randomGene(Locus.Extension()),
                randomGene(Locus.Agouti()),
                randomGene(Locus.Creme()),
                randomGene(Locus.Flaxen()),
                randomGene(Locus.Champagne()),
                randomGene(Locus.Dun()),
                randomGene(Locus.Shade()),
                randomGene(Locus.Silver()),
                randomGene(Locus.Splash()),
                randomGene(Locus.KIT()),
                randomGene(Locus.Mealy())
            )
        }

        private fun randomGene(locus: Locus): String {
            var retVal = ""
            repeat(2) {
                retVal += Actions.weightedRandom(locus.strings, locus.weights)
            }
            return retVal
        }

        val colorMap = mapOf(
            listOf("chestnut") to listOf("chestnut","AF6954","a05040","87412C","horse"),
            listOf("bay") to listOf("bay","A45E49","954535","7C3621","points"),
            listOf("seal brown") to listOf("seal brown","5F432F","4B2F25","371B07","points"),
            listOf("black") to listOf("black","2D2525","191111","050000","horse"),
            listOf("brown black") to listOf("brown black","34251E","20110A","0C0000","horse"),
            listOf("white") to listOf("white","FFFFFF","FFFFFF","FFFFFF","horse"),
            listOf("chestnut","single cream") to listOf("palomino","F0C390","DCAA7C","C89168","flaxen"),
            listOf("bay","single cream") to listOf("buckskin","E5B885","D19F71","BD865D","points"),
            listOf("seal brown","single cream") to listOf("burnt buckskin","785748","5F432F","4B2A1B","points"),
            listOf("black","single cream") to listOf("smokey black","413939","2D2525","191111","horse"),
            listOf("brown black","single cream") to listOf("smokey brown","483932","34251E","20110A","horse"),
            listOf("chestnut","double cream") to listOf("cremello","FFFAF1","F8EBDD","EEDCC9","horse"),
            listOf("bay","double cream") to listOf("perlino","FFFAF1","F8EBDD","EEDCC9","shaded mane"),
            listOf("seal brown","double cream") to listOf("seal perlino","FFFAF1","F8EBDD","EEDCC9","shaded mane"),
            listOf("black","double cream") to listOf("smokey cream","FFF7EA","EFE3D6","DBCFC2","horse"),
            listOf("brown black","double cream") to listOf("smokey brown cream","FFF7EA","EFE3D6","DBCFC2","horse"),
            listOf("chestnut","pearl") to listOf("gold pearl","F8D5B4","d0ad96","B28F6E","flaxen"),
            listOf("bay","pearl") to listOf("amber pearl","EDCAA9","C5A28B","A78463","light points"),
            listOf("seal brown","pearl") to listOf("sable pearl","E1C8A3","b9a085","9B785D","light points"),
            listOf("black","pearl") to listOf("classic pearl","D8C7B1","b09f89","887761","light points"),
            listOf("brown black","pearl") to listOf("classic brown pearl","DEC7B1","b69f89","8E7761","light points"),
            listOf("chestnut","champagne") to listOf("gold champagne","F8D5B4","d0ad96","B28F6E","flaxen"),
            listOf("bay","champagne") to listOf("amber champagne","EDCAA9","C5A28B","A78463","light points"),
            listOf("seal brown","champagne") to listOf("sable champagne","E1C8A3","b9a085","9B785D","light points"),
            listOf("black","champagne") to listOf("classic champagne","D8C7B1","b09f89","887761","light points"),
            listOf("brown black","champagne") to listOf("classic brown champagne","DEC7B1","b69f89","8E7761","light points"),
            listOf("chestnut","double cream","champagne") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("bay","double cream","champagne") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("seal brown","double cream","champagne") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("black","double cream","champagne") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("brown black","double cream","champagne") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("chestnut","dun") to listOf("red dun","E9B38F","df9a67","C1815D","horse"),
            listOf("bay","dun") to listOf("dun","DEA884","D48F5C","B67652","points"),
            listOf("seal brown","dun") to listOf("lobo dun","BCA793","ad8e75","8F7566","points"),
            listOf("black","dun") to listOf("grullo","C6C1BE","a8a3a0","8F8A87","points"),
            listOf("brown black","dun") to listOf("olive grullo","D2C0AE","b4a290","9B8977","points"),
            listOf("chestnut","single cream","dun") to listOf("dunalino","F8E7C5","e4ba98","C6A184","flaxen"),
            listOf("bay","single cream","dun") to listOf("dunskin","EDDCBA","D9AF8D","BB9679","points"),
            listOf("seal brown","single cream","dun") to listOf("lobo dunskin","E9CAA7","CBAC93","A88E7A","points"),
            listOf("black","single cream","dun") to listOf("smokey grullo","E4DFDC","C6C1BE","A8A3A0","points"),
            listOf("brown black","single cream","dun") to listOf("smokey olive grullo","EBD9C7","D2C0AE","B4A290","points"),
            listOf("chestnut","double cream","dun") to listOf("cremello","FFFAF1","F8EBDD","EEDCC9","horse"),
            listOf("bay","double cream","dun") to listOf("perlino","FFFAF1","F8EBDD","EEDCC9","shaded mane"),
            listOf("seal brown","double cream","dun") to listOf("seal perlino","FFFAF1","F8EBDD","EEDCC9","shaded mane"),
            listOf("black","double cream","dun") to listOf("smokey cream","FFF7EA","EFE3D6","DBCFC2","horse"),
            listOf("brown black","double cream","dun") to listOf("smokey brown cream","FFF7EA","EFE3D6","DBCFC2","horse"),
            listOf("chestnut","pearl","dun") to listOf("gold pearl dun","FCDABC","debc9e","CAA88A","flaxen"),
            listOf("bay","pearl","dun") to listOf("amber pearl dun","F1CFB1","D3B193","BF9D7F","light points"),
            listOf("seal brown","pearl","dun") to listOf("sable pearl dun","E5CDAB","C7AF8D","B39B79","light points"),
            listOf("black","pearl","dun") to listOf("classic pearl dun","DCCCAF","BEAE91","AA9A7D","light points"),
            listOf("brown black","pearl","dun") to listOf("classic brown pearl dun","E2CCAF","C4AE91","B09A7D","light points"),
            listOf("chestnut","champagne","dun") to listOf("gold champagne dun","FCDABC","debc9e","CAA88A","flaxen"),
            listOf("bay","champagne","dun") to listOf("amber champagne dun","F1CFB1","D3B193","BF9D7F","light points"),
            listOf("seal brown","champagne","dun") to listOf("sable champagne dun","E5CDAB","C7AF8D","B39B79","light points"),
            listOf("black","champagne","dun") to listOf("classic champagne dun","DCCCAF","BEAE91","AA9A7D","light points"),
            listOf("brown black","champagne","dun") to listOf("classic brown champagne dun","E2CCAF","C4AE91","B09A7D","light points"),
            listOf("chestnut","double cream","champagne","dun") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("bay","double cream","champagne","dun") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("seal brown","double cream","champagne","dun") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("black","double cream","champagne","dun") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("brown black","double cream","champagne","dun") to listOf("ivory champagne","FEFEFB","f4efe7","EAE5D8","horse"),
            listOf("bay","silver") to listOf("silver bay","A9633F","954535","813B17","silver points"),
            listOf("seal brown","silver") to listOf("silver seal","947D69","80695F","6C5541","silver points"),
            listOf("black","silver") to listOf("silver","625F5F","4e4b4b","3A3737","silver mane"),
            listOf("brown black","silver") to listOf("silver brown","695F5F","554B4B","413737","silver mane"),
            listOf("bay","single cream","silver") to listOf("silver buckskin","E5B885","D19F71","BD865D","silver points"),
            listOf("seal brown","single cream","silver") to listOf("silver burnt buckskin","AD9182","947D69","806455","silver points"),
            listOf("black","single cream","silver") to listOf("silver smokey black","767373","625F5F","4E4B4B","silver mane"),
            listOf("brown black","single cream","silver") to listOf("silver smokey brown","7D7373","695F5F","554B4B","silver mane"),
            listOf("bay","double cream","silver") to listOf("silver perlino","FFFAF1","F8EBDD","EEDCC9","silver mane"),
            listOf("seal brown","double cream","silver") to listOf("silver seal perlino","FFFAF1","F8EBDD","EEDCC9","silver mane"),
            listOf("black","double cream","silver") to listOf("silver smokey cream","FFF7EA","EFE3D6","DBCFC2","silver mane"),
            listOf("brown black","double cream","silver") to listOf("silver smokey brown cream","FFF7EA","EFE3D6","DBCFC2","silver mane"),
            listOf("bay","pearl","silver") to listOf("silver amber pearl","F8D5B4","d0ad96","BC9982","silver points"),
            listOf("seal brown","pearl","silver") to listOf("silver sable pearl","D0B89F","B29A81","9E866D","silver points"),
            listOf("black","pearl","silver") to listOf("silver classic pearl","BBAFA0","9D9182","897D6E","silver mane"),
            listOf("brown black","pearl","silver") to listOf("silver classic brown pearl","C1AFA1","A39183","8F7D6F","silver mane"),
            listOf("bay","champagne","silver") to listOf("silver amber champagne","F8D5B4","d0ad96","BC9982","silver points"),
            listOf("seal brown","champagne","silver") to listOf("silver sable champagne","D0B89F","B29A81","9E866D","silver points"),
            listOf("black","champagne","silver") to listOf("silver classic champagne","BBAFA0","9D9182","897D6E","silver mane"),
            listOf("brown black","champagne","silver") to listOf("silver classic brown champagne","C1AFA1","A39183","8F7D6F","silver mane"),
            listOf("bay","double cream","champagne","silver") to listOf("silver ivory champagne","FEFEFB","f4efe7","EAE5D8","silver mane"),
            listOf("seal brown","double cream","champagne","silver") to listOf("silver ivory champagne","FEFEFB","f4efe7","EAE5D8","silver mane"),
            listOf("black","double cream","champagne","silver") to listOf("silver ivory champagne","FEFEFB","f4efe7","EAE5D8","silver mane"),
            listOf("brown black","double cream","champagne","silver") to listOf("silver ivory champagne","FEFEFB","f4efe7","EAE5D8","silver mane"),
            listOf("bay","dun","silver") to listOf("silver dun","DEA884","D48F5C","B67652","silver points"),
            listOf("seal brown","dun","silver") to listOf("silver dun","C6A573","B2875F","9E7D4B","silver points"),
            listOf("black","dun","silver") to listOf("silver grullo","B3B2B0","959492","777674","silver mane"),
            listOf("brown black","dun","silver") to listOf("silver grullo","BDB1A5","9F9387","817569","silver mane"),
            listOf("bay","single cream","dun","silver") to listOf("silver dunskin","F7CDAB","D9AF8D","BB916F","silver points"),
            listOf("seal brown","single cream","dun","silver") to listOf("silver lobo dunskin","FFE4C3","E8C6A5","CAA887","silver points"),
            listOf("black","single cream","dun","silver") to listOf("silver smokey grullo","F5F4F2","D7D6D4","B9B8B6","silver mane"),
            listOf("brown black","single cream","dun","silver") to listOf("silver smokey olive grullo","FFF4E8","E2D6CA","C4B8AC","silver mane"),
            listOf("bay","double cream","dun","silver") to listOf("silver perlino","FFFAF1","F8EBDD","EEDCC9","silver mane"),
            listOf("seal brown","double cream","dun","silver") to listOf("silver seal perlino","FFFAF1","F8EBDD","EEDCC9","silver mane"),
            listOf("black","double cream","dun","silver") to listOf("silver smokey cream","FFF7EA","EFE3D6","DBCFC2","silver mane"),
            listOf("brown black","double cream","dun","silver") to listOf("silver smokey brown cream","FFF7EA","EFE3D6","DBCFC2","silver mane"),
            listOf("bay","pearl","dun","silver") to listOf("silver amber pearl dun","FCDABC","debc9e","CAA88A","silver points"),
            listOf("seal brown","pearl","dun","silver") to listOf("silver sable pearl dun","D0AE8E","B29070","9E7C5C","silver points"),
            listOf("black","pearl","dun","silver") to listOf("silver classic pearl dun","B7B0A8","99928A","857E76","silver mane"),
            listOf("brown black","pearl","dun","silver") to listOf("silver classic brown pearl dun","BFB0A3","A19285","8D7E71","silver mane"),
            listOf("bay","champagne","dun","silver") to listOf("silver amber champagne dun","E5CDAB","C7AF8D","B39B79","silver points"),
            listOf("seal brown","champagne","dun","silver") to listOf("silver sable champagne dun","DCCCAF","BEAE91","AA9A7D","silver points"),
            listOf("black","champagne","dun","silver") to listOf("silver classic champagne dun","E2CCAF","C4AE91","B09A7D","silver mane"),
            listOf("brown black","champagne","dun","silver") to listOf("classic brown champagne dun","ABA199","A19285","978876","silver mane"),
            listOf("bay","double cream","champagne","dun","silver") to listOf("silver ivory champagne","FEFEFB","f4efe7","EAE5D8","silver mane"),
            listOf("seal brown","double cream","champagne","dun","silver") to listOf("silver ivory champagne","FEFEFB","f4efe7","EAE5D8","silver mane"),
            listOf("black","double cream","champagne","dun","silver") to listOf("silver ivory champagne","FEFEFB","f4efe7","EAE5D8","silver mane"),
            listOf("brown black","double cream","champagne","dun","silver") to listOf("silver ivory champagne","FEFEFB","f4efe7","EAE5D8","silver mane"),
        )
    }

    class MapException(key: String) : Exception ("No key $key found in map.")
}
