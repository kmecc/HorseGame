package android.horsegame
//
//import android.content.Context
//import android.horsegame.database.HorseDao
//import android.horsegame.database.HorseDatabase
//import android.service.autofill.UserData
//import androidx.room.Room
//import java.util.concurrent.Callable
//import java.util.concurrent.ExecutionException
//import java.util.concurrent.Executors
//import java.util.concurrent.Future
//
//
//private const val DATABASE_NAME = "horse-database"
//
//class HorseRepository private constructor(context: Context) {
//
//    private val database : HorseDatabase = Room.databaseBuilder(
//        context.applicationContext,
//        HorseDatabase::class.java,
//        DATABASE_NAME
//    ).allowMainThreadQueries().build()
//
//    private val horseDao = database.horseDao()
//
//    fun getAll(): List<Horse> = horseDao.getAll()
//    fun getMine(): List<Horse> = horseDao.getMine()
//    fun getMustangs(): List<Horse> = horseDao.getMustangs()
//    fun updateHorse(horse: Horse) = horseDao.updateHorse(horse)
//    fun addHorse(horse: Horse) = horseDao.addHorse(horse)
////    fun getId(id: Int): Horse = horseDao.getId(id)
//
//    fun clear() {
//        database.clearAllTables()
//    }
//
//    companion object {
//        private var INSTANCE: HorseRepository? = null
//
//        fun initalize(context: Context) {
//            if (INSTANCE == null) {
//                INSTANCE = HorseRepository(context)
//            }
//        }
//
//        fun get(): HorseRepository {
//            return INSTANCE ?:
//            throw IllegalStateException("HorseRepository must be initialized")
//        }
//    }
//}