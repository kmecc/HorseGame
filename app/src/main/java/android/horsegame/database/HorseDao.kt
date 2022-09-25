package android.horsegame.database
//
//import android.horsegame.GameState
//import android.horsegame.Horse
//import android.horsegame.data.GameData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Update
//
//
//@Dao
//interface HorseDao {
//
//    @Query("SELECT * FROM horse WHERE isAlive")
//    fun getAll(): List<Horse>
//
//    @Query("SELECT * FROM horse WHERE isMine AND isAlive")
//    fun getMine(): List<Horse>
//
//    @Query("SELECT * FROM horse WHERE NOT isMine AND isAlive")
//    fun getMustangs(): List<Horse>
//
//    @Query("SELECT * FROM horse WHERE id = :id AND ")
//    fun getId(id: Int): Horse
//
//    @Query("SELECT * FROM Age WHERE age >= 4 isMine AND isBreedable AND isMale = :isMale")
//    fun getBreedable(isMale: Boolean)
//
//    @Update
//    fun updateHorse(horse: Horse)
//
//    @Insert
//    fun addHorse(horse: Horse)
//}