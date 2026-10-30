package com.learningfolder.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var db: DbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DbHelper(this)
        val sb = findViewById<EditText>(R.id.searchBar)
        val recent = findViewById<TextView>(R.id.recent)
        val (d, c, k) = db.stats()
        recent.text = "Decks: $d  Cards: $c  Known: $k"
        findViewById<Button>(R.id.btnSearch).setOnClickListener {
            val i = Intent(this, SearchActivity::class.java); i.putExtra("q", sb.text.toString()); startActivity(i)
        }
        findViewById<Button>(R.id.btnDecks).setOnClickListener { startActivity(Intent(this, DecksActivity::class.java)) }
        findViewById<Button>(R.id.btnSchools).setOnClickListener { startActivity(Intent(this, SchoolsActivity::class.java)) }
        findViewById<Button>(R.id.btnProfile).setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }
    }
}
