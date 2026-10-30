import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// ponytail: ceiling = stdlib only, no coroutines. Upgrade path: kotlinx-coroutines + ktor server.

data class Deck(
    val id: String,
    val title: String,
    val description: String,
    val author: String,
    val cardCount: Int
)

val sampleDecks = listOf(
    Deck("1", "Spanish Basics", "Common Spanish words", "alice", 50),
    Deck("2", "Kotlin Syntax", "Kotlin language features", "bob", 120),
    Deck("3", "Medical Terms", "Anatomy vocabulary", "carol", 200),
    Deck("4", "Spanish Verbs", "Conjugation practice", "dave", 80),
    Deck("5", "History Quiz", "World history facts", "eve", 35)
)

fun searchDecks(query: String, decks: List<Deck>): List<Deck> {
    if (query.isBlank()) return emptyList()
    val lower = query.lowercase()
    // Gizmo pattern: CARDS_SEARCH_MIN_LENGTH enforced before query fires
    if (lower.length < 2) return emptyList()
    return decks.filter { deck ->
        deck.title.lowercase().contains(lower) ||
        deck.description.lowercase().contains(lower) ||
        deck.author.lowercase().contains(lower)
    }
}

fun debounce(delayMs: Long, action: (String) -> Unit): (String) -> Unit {
    val scheduler = Executors.newSingleThreadScheduledExecutor()
    var lastFuture: java.util.concurrent.ScheduledFuture<*>? = null
    return { input: String ->
        lastFuture?.cancel(false)
        lastFuture = scheduler.schedule({ action(input) }, delayMs, TimeUnit.MILLISECONDS)
    }
}

fun main() {
    println("=== Gizmo-style Search (Kotlin/JVM) ===")
    println("Evidence from bundle: useSearchUsersInfiniteQuery, public-deck-search, debouncerDelay, CARDS_SEARCH_MIN_LENGTH\n")

    // Direct search
    val results = searchDecks("spanish", sampleDecks)
    println("Search 'spanish': ${results.size} results")
    results.forEach { println("  [${it.id}] ${it.title} by ${it.author} (${it.cardCount} cards)") }

    println()

    // Min length gate
    val shortResults = searchDecks("s", sampleDecks)
    println("Search 's' (below min length): ${shortResults.size} results")

    println()

    // Debounced search (Gizmo pattern: debouncerDelay + SearchTermChange)
    val debouncedSearch = debounce(300) { term ->
        val r = searchDecks(term, sampleDecks)
        println("Debounced search '$term': ${r.size} results")
        r.forEach { println("  [${it.id}] ${it.title}") }
    }

    debouncedSearch("kotlin")
    Thread.sleep(500) // wait for debounce to fire

    println("\nDone.")
}
