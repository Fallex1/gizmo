package com.learningfolder.app

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class SearchActivity : Activity() {
    private lateinit var query: EditText
    private lateinit var list: ListView
    private lateinit var progress: ProgressBar
    private lateinit var status: TextView
    private lateinit var loadMore: Button
    private lateinit var adapter: ArrayAdapter<String>
    private val items = ArrayList<String>()
    private var mode = "users"
    private val handler = Handler(Looper.getMainLooper())
    private var debounce: Runnable? = null
    private lateinit var db: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        db = DbHelper(this)
        query = findViewById(R.id.query)
        list = findViewById(R.id.list)
        progress = findViewById(R.id.progress)
        status = findViewById(R.id.status)
        loadMore = findViewById(R.id.loadMore)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        list.adapter = adapter
        findViewById<Button>(R.id.tUsers).setOnClickListener { switchMode("users") }
        findViewById<Button>(R.id.tDecks).setOnClickListener { switchMode("decks") }
        findViewById<Button>(R.id.tCards).setOnClickListener { switchMode("cards") }
        findViewById<Button>(R.id.tSchools).setOnClickListener { switchMode("schools") }
        query.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                debounce?.let { handler.removeCallbacks(it) }
                val r = Runnable { doSearch(s?.toString()?.trim() ?: "") }
                debounce = r
                handler.postDelayed(r, 400)
            }
        })
        loadMore.visibility = View.GONE
        val q0 = intent.getStringExtra("q") ?: ""
        if (q0.isNotEmpty()) query.setText(q0)
        switchMode("users")
    }

    private fun switchMode(m: String) { mode = m; status.text = "Mode: $m"; doSearch(query.text.toString().trim()) }
    private fun doSearch(q: String) {
        if (q.isEmpty()) { items.clear(); adapter.notifyDataSetChanged(); status.text = "Type to search ($mode)"; return }
        progress.visibility = View.VISIBLE
        thread {
            val res = try { search(q, mode) } catch (e: Exception) { "ERR:${e.message}" }
            runOnUiThread {
                progress.visibility = View.GONE
                items.clear()
                if (res.startsWith("ERR:")) { status.text = res; return@runOnUiThread }
                items.addAll(res.split("\n").filter { it.isNotBlank() })
                adapter.notifyDataSetChanged()
                status.text = "${items.size} result(s) for \"$q\" ($mode)"
            }
        }
    }
    private fun search(q: String, mode: String): String = when (mode) {
        "users" -> searchUsers(q)
        "decks" -> db.searchDecks(q).joinToString("\n").ifEmpty { "No decks found." }
        "cards" -> db.searchCards(q).joinToString("\n").ifEmpty { "No cards found." }
        "schools" -> searchSchools(q)
        else -> ""
    }
    private fun searchUsers(q: String): String {
        val url = URL("https://jsonplaceholder.typicode.com/users")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"; conn.connectTimeout = 10000; conn.readTimeout = 10000
        val json = conn.inputStream.bufferedReader().readText(); conn.disconnect()
        val sb = StringBuilder(); var count = 0
        Regex("\\{[^}]+\\}").findAll(json).forEach { m ->
            val o = m.value
            fun get(key: String): String = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"").find(o)?.groupValues?.get(1) ?: ""
            val name = get("name"); val username = get("username"); val email = get("email")
            val id = Regex("\"id\"\\s*:\\s*(\\d+)").find(o)?.groupValues?.get(1) ?: ""
            if (name.contains(q, true) || username.contains(q, true)) { sb.append("[$id] $name (@$username)\n$email\n"); count++ }
        }
        return if (count == 0) "No users found." else sb.toString().trim()
    }
    private fun searchSchools(q: String): String {
        data class S(val name: String, val w: Int)
        val schools = listOf(S("SMAN 1 Jakarta", 9), S("ITB", 8), S("UI", 7), S("UGM", 6), S("ITS", 5), S("UNAIR", 4))
        val res = schools.filter { it.name.contains(q, true) }.sortedByDescending { it.w }
        return if (res.isEmpty()) "No schools found." else res.joinToString("\n") { "${it.name}  (weight ${it.w})" }
    }
}
