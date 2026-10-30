package com.learningfolder.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*

class DecksActivity : Activity() {
    private lateinit var db: DbHelper
    private lateinit var adapter: ArrayAdapter<String>
    private val decks = ArrayList<Pair<Long, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decks)
        db = DbHelper(this)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList<String>())
        findViewById<ListView>(R.id.list).adapter = adapter
        findViewById<ListView>(R.id.list).setOnItemClickListener { _, _, pos, _ ->
            val i = Intent(this, FlashcardActivity::class.java)
            i.putExtra("deckId", decks[pos].first); i.putExtra("deckName", decks[pos].second); startActivity(i)
        }
        findViewById<Button>(R.id.addBtn).setOnClickListener {
            val input = EditText(this)
            AlertDialog.Builder(this).setTitle("New deck").setView(input)
                .setPositiveButton("Create") { _, _ ->
                    val name = input.text.toString().trim()
                    if (name.isNotEmpty()) { db.addDeck(name); refresh() }
                }.setNegativeButton("Cancel", null).show()
        }
        refresh()
    }
    private fun refresh() {
        decks.clear(); decks.addAll(db.decks())
        val names = decks.map { "${it.second}  (${db.cards(it.first).size} cards)" }
        adapter.clear(); adapter.addAll(names); adapter.notifyDataSetChanged()
    }
}
