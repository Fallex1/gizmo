package com.learningfolder.app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(ctx: Context) : SQLiteOpenHelper(ctx, "lf.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE decks (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)")
        db.execSQL("CREATE TABLE cards (id INTEGER PRIMARY KEY AUTOINCREMENT, deck_id INTEGER NOT NULL, front TEXT NOT NULL, back TEXT NOT NULL, known INTEGER DEFAULT 0)")
        // seed
        val d1 = db.insert("decks", null, ContentValues().apply { put("name", "Math Basics") })
        val d2 = db.insert("decks", null, ContentValues().apply { put("name", "Kotlin Fundamentals") })
        val d3 = db.insert("decks", null, ContentValues().apply { put("name", "Android UI") })
        val seeds = listOf(
            d1 to listOf("What is a prime?" to "A number divisible only by 1 and itself.", "2+2?" to "4"),
            d2 to listOf("What is Kotlin?" to "Modern language for JVM/Android/Native.", "Null safety?" to "Use ? and !! operators."),
            d3 to listOf("Activity lifecycle?" to "onCreate, onStart, onResume, onPause, onStop, onDestroy.", "What is Intent?" to "Messaging object.")
        )
        seeds.forEach { (did, cards) ->
            cards.forEach { (f, b) ->
                db.insert("cards", null, ContentValues().apply {
                    put("deck_id", did); put("front", f); put("back", b); put("known", 0)
                })
            }
        }
    }
    override fun onUpgrade(db: SQLiteDatabase, o: Int, n: Int) {}

    fun decks(): List<Pair<Long, String>> {
        val out = mutableListOf<Pair<Long, String>>()
        val c = readableDatabase.rawQuery("SELECT id,name FROM decks ORDER BY id", null)
        while (c.moveToNext()) out.add(c.getLong(0) to c.getString(1))
        c.close(); return out
    }
    fun addDeck(name: String): Long =
        writableDatabase.insert("decks", null, ContentValues().apply { put("name", name) })

    fun cards(deckId: Long): List<Triple<Long, String, String>> {
        val out = mutableListOf<Triple<Long, String, String>>()
        val c = readableDatabase.rawQuery("SELECT id,front,back FROM cards WHERE deck_id=? ORDER BY id", arrayOf(deckId.toString()))
        while (c.moveToNext()) out.add(Triple(c.getLong(0), c.getString(1), c.getString(2)))
        c.close(); return out
    }
    fun addCard(deckId: Long, front: String, back: String) {
        writableDatabase.insert("cards", null, ContentValues().apply {
            put("deck_id", deckId); put("front", front); put("back", back); put("known", 0)
        })
    }
    fun setKnown(cardId: Long, known: Boolean) {
        val cv = ContentValues().apply { put("known", if (known) 1 else 0) }
        writableDatabase.update("cards", cv, "id=?", arrayOf(cardId.toString()))
    }
    fun stats(): Triple<Int, Int, Int> {
        val d = readableDatabase
        fun count(sql: String): Int {
            val c = d.rawQuery(sql, null); c.moveToFirst(); val n = c.getInt(0); c.close(); return n
        }
        return Triple(count("SELECT COUNT(*) FROM decks"), count("SELECT COUNT(*) FROM cards"), count("SELECT COUNT(*) FROM cards WHERE known=1"))
    }
    fun searchCards(q: String): List<String> {
        val out = mutableListOf<String>()
        val c = readableDatabase.rawQuery("SELECT front,back FROM cards WHERE front LIKE ? OR back LIKE ?", arrayOf("%$q%", "%$q%"))
        while (c.moveToNext()) out.add("Card: ${c.getString(0)}  →  ${c.getString(1)}")
        c.close(); return out
    }
    fun searchDecks(q: String): List<String> {
        val out = mutableListOf<String>()
        val c = readableDatabase.rawQuery("SELECT name FROM decks WHERE name LIKE ?", arrayOf("%$q%"))
        while (c.moveToNext()) out.add("Deck: ${c.getString(0)}")
        c.close(); return out
    }
}
