package com.learningfolder.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import kotlin.random.Random

class FlashcardActivity : Activity() {
    private lateinit var db: DbHelper
    private var deckId: Long = -1
    private var cards = ArrayList<Triple<Long, String, String>>()
    private var idx = 0
    private var showingAnswer = false
    private var knownCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)
        db = DbHelper(this)
        deckId = intent.getLongExtra("deckId", -1)
        val deckName = findViewById<TextView>(R.id.deckName)
        val cardText = findViewById<TextView>(R.id.cardText)
        val progress = findViewById<TextView>(R.id.progress)
        deckName.text = intent.getStringExtra("deckName") ?: "Deck"
        fun show() {
            if (cards.isEmpty()) { cardText.text = "No cards. Tap Add Card."; progress.text = "0/0"; return }
            cardText.text = if (showingAnswer) cards[idx].third else cards[idx].second
            progress.text = "${idx + 1}/${cards.size}  •  Known: $knownCount"
        }
        fun load() { cards = ArrayList(db.cards(deckId)); idx = 0; knownCount = 0; showingAnswer = false; show() }
        load()
        findViewById<Button>(R.id.flip).setOnClickListener { showingAnswer = !showingAnswer; show() }
        findViewById<Button>(R.id.known).setOnClickListener {
            if (cards.isEmpty()) return@setOnClickListener
            db.setKnown(cards[idx].first, true); knownCount++; idx = (idx + 1) % cards.size; showingAnswer = false; show()
        }
        findViewById<Button>(R.id.next).setOnClickListener {
            if (cards.isEmpty()) return@setOnClickListener
            idx = (idx + 1) % cards.size; showingAnswer = false; show()
        }
        findViewById<Button>(R.id.shuffle).setOnClickListener { cards.shuffle(Random.Default); idx = 0; showingAnswer = false; knownCount = 0; show() }
        findViewById<Button>(R.id.addCard).setOnClickListener {
            val frontInput = EditText(this)
            val backInput = EditText(this)
            val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; addView(frontInput); addView(backInput) }
            frontInput.hint = "Front (question)"
            backInput.hint = "Back (answer)"
            AlertDialog.Builder(this).setTitle("Add Card").setView(layout)
                .setPositiveButton("Save") { _, _ ->
                    val f = frontInput.text.toString().trim()
                    val b = backInput.text.toString().trim()
                    if (f.isNotEmpty() && b.isNotEmpty()) {
                        db.addCard(deckId, f, b)
                        load()
                    }
                }.setNegativeButton("Cancel", null).show()
        }
    }
}
