@file:OptIn(ExperimentalForeignApi::class, kotlin.experimental.ExperimentalNativeApi::class)

import platform.posix.*
import kotlinx.cinterop.*

data class User(val id: Int, val name: String, val username: String, val email: String)

// ponytail: popen+curl avoids ktor dependency; swap to ktor-client when real API needs auth/pagination
fun fetchJson(url: String): String {
    val fp = popen("curl -s '$url'", "r") ?: return ""
    val sb = StringBuilder()
    memScoped {
        val buf = allocArray<ByteVar>(4096)
        while (fgets(buf, 4096, fp) != null) {
            sb.append(buf.toKString())
        }
    }
    pclose(fp)
    return sb.toString()
}

// ponytail: hand-rolled parser avoids kotlinx-serialization dep; add it when schema stabilizes
fun parseUsers(json: String): List<User> {
    val users = mutableListOf<User>()
    var i = 0
    fun skipWs() { while (i < json.length && json[i].isWhitespace()) i++ }
    fun expect(c: Char) { skipWs(); if (i < json.length && json[i] == c) i++ }
    fun readString(): String {
        skipWs()
        if (i >= json.length || json[i] != '"') return ""
        i++
        val start = i
        while (i < json.length && json[i] != '"') {
            if (json[i] == '\\') i++
            i++
        }
        val s = json.substring(start, i)
        if (i < json.length) i++
        return s
    }
    fun readInt(): Int {
        skipWs()
        val start = i
        if (i < json.length && json[i] == '-') i++
        while (i < json.length && json[i].isDigit()) i++
        return json.substring(start, i).toIntOrNull() ?: 0
    }
    fun findKey(key: String): Boolean {
        val target = "\"$key\""
        val idx = json.indexOf(target, i)
        if (idx < 0) return false
        i = idx + target.length
        skipWs()
        if (i < json.length && json[i] == ':') i++
        return true
    }

    expect('[')
    while (i < json.length) {
        skipWs()
        if (i < json.length && json[i] == ']') break
        if (i < json.length && json[i] == ',') { i++; continue }
        expect('{')
        var id = 0; var name = ""; var username = ""; var email = ""
        val objStart = i
        val objEnd = json.indexOf('}', i)
        if (objEnd < 0) break
        val savedI = i
        if (findKey("id")) id = readInt()
        i = savedI
        if (findKey("name")) name = readString()
        i = savedI
        if (findKey("username")) username = readString()
        i = savedI
        if (findKey("email")) email = readString()
        i = objEnd + 1
        users.add(User(id, name, username, email))
    }
    return users
}

fun searchUsers(query: String): List<User> {
    if (query.isBlank()) return emptyList()
    // ponytail: jsonplaceholder only supports exact prefix match on name; client-side filter for substring
    val url = "https://jsonplaceholder.typicode.com/users"
    val json = fetchJson(url)
    return parseUsers(json).filter {
        it.name.contains(query, ignoreCase = true) ||
        it.username.contains(query, ignoreCase = true)
    }
}

fun main() {
    runTests()
    print("Search users: ")
    val query = readlnOrNull()?.trim() ?: return
    println("Searching for '$query'...")
    val results = searchUsers(query)
    if (results.isEmpty()) {
        println("No users found.")
    } else {
        results.forEach { println("[${it.id}] ${it.name} (@${it.username}) - ${it.email}") }
    }
}

fun runTests() {
    val sample = """[{"id":1,"name":"Leanne Graham","username":"Bret","email":"Sincere@april.biz"},{"id":2,"name":"Ervin Howell","username":"Antonette","email":"Shanna@melissa.tv"}]"""
    val parsed = parseUsers(sample)
    assert(parsed.size == 2) { "Expected 2, got ${parsed.size}" }
    assert(parsed[0].name == "Leanne Graham") { "Wrong name: ${parsed[0].name}" }
    assert(parsed[1].id == 2) { "Wrong id: ${parsed[1].id}" }
    val filtered = parsed.filter { it.name.contains("leanne", ignoreCase = true) }
    assert(filtered.size == 1) { "Filter failed" }
    println("Tests passed.")
}
