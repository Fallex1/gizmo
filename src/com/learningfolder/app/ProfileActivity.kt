package com.learningfolder.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class ProfileActivity : Activity() {
    private lateinit var db: DbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        db = DbHelper(this)
        val (d, c, k) = db.stats()
        findViewById<TextView>(R.id.stats).text = "Decks: $d\nCards: $c\nKnown: $k\nUnknown: ${c - k}"
    }
}
