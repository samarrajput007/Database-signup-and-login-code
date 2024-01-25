import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "YourDatabaseName"
        private const val DATABASE_VERSION = 1

       
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
    }

    
    private val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS ("
            + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "$COLUMN_USERNAME TEXT, "
            + "$COLUMN_PASSWORD TEXT)")

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_USERS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}




data class UserModel(val id: Int, val username: String, val password: String)




import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class DatabaseManager(context: Context) {

    private val dbHelper = DatabaseHelper(context)
    private var database: SQLiteDatabase? = null

    @Throws(SQLException::class)
    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
    }

    fun insertUser(username: String, password: String): Long {
        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_USERNAME, username)
        values.put(DatabaseHelper.COLUMN_PASSWORD, password)

        return database?.insert(DatabaseHelper.TABLE_USERS, null, values) ?: -1
    }

    fun getUserByUsername(username: String): UserModel? {
        val columns = arrayOf(
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_USERNAME,
            DatabaseHelper.COLUMN_PASSWORD
        )
        val selection = "${DatabaseHelper.COLUMN_USERNAME} = ?"
        val selectionArgs = arrayOf(username)

        val cursor: Cursor? = database?.query(
            DatabaseHelper.TABLE_USERS,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getInt(it.getColumnIndex(DatabaseHelper.COLUMN_ID))
                val retrievedUsername = it.getString(it.getColumnIndex(DatabaseHelper.COLUMN_USERNAME))
                val retrievedPassword = it.getString(it.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD))

                return UserModel(id, retrievedUsername, retrievedPassword)
            }
        }

        return null
    }
}





val dbManager = DatabaseManager(this)
dbManager.open()
val newUserId = dbManager.insertUser("exampleUser", "examplePassword")
val retrievedUser = dbManager.getUserByUsername("exampleUser")
dbManager.close()
