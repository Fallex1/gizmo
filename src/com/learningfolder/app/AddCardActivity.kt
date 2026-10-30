package com.learningfolder.app

import android.app.Activity
import android.os.Bundle
import android.widget.*

class AddCardActivity : Activity() {
    private lateinit var db: DbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcard)
        db = DbHelper(this)
        val deckId = intent.getLongExtra("deckId", -1)
        findViewById<Button>(R.id.save).setOnClickListener {
            val front = findViewById<EditText>(R.id.front).text.toString().trim()
            val back = findViewById<EditText>(R.id.back).text.toString().trim()
            if (front.isEmpty() || back.isEmpty()) { Toast.makeText(this, "Fill both fields", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            db.addCard(deckId, front, back)
            Toast.makeText(this, "Card added", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
