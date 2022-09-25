package android.horsegame.database

//import android.horsegame.Horse
//import android.horsegame.color.HorseColor
//import androidx.room.TypeConverter
//
//class HorseTypeConverters {
//
//    @TypeConverter
//    fun fromColor(color: HorseColor): String {
//        return color.genome.joinToString(", ")
//    }
//
//    @TypeConverter
//    fun toColor(genome: String): HorseColor {
//        val lst = genome.split(", ")
//        return HorseColor(lst)
//    }
//
//    @TypeConverter
//    fun fromGender(gender: Horse.Gender): String {
//        return gender.toString()
//    }
//
//    @TypeConverter
//    fun toGender(gender: String): Horse.Gender {
//        return Horse.Gender.values().filter{ it.toString() == gender }.get(0)
//    }
//}