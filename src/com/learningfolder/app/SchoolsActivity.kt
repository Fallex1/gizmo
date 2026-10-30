package com.learningfolder.app

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*

class SchoolsActivity : Activity() {
    data class School(val name: String, val weight: Int)
    private val schools = listOf(
        School("SMAN 1 Jakarta", 9), School("ITB", 8), School("UI", 7),
        School("UGM", 6), School("ITS", 5), School("UNAIR", 4),
        School("IPB", 4), School("UNDIP", 3)
    )
    private lateinit var adapter: ArrayAdapter<String>
    private val items = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schools)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        findViewById<ListView>(R.id.list).adapter = adapter
        val q = findViewById<EditText>(R.id.query)
        fun refresh(text: String) {
            items.clear()
            schools.filter { it.name.contains(text, true) }
                .sortedByDescending { it.weight }
                .forEach { items.add("${it.name}  (weight ${it.weight})") }
            adapter.notifyDataSetChanged()
        }
        q.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { refresh(s?.toString() ?: "") }
        })
        refresh("")
    }
}
