# Gizmo APK — Reverse-Engineered Evidence

Source: /home/lefi/Downloads/Gizmo.apk (172.9 MB), extracted to /home/lefi/.hermes/cache/scratch/gizmo-extracted

## Stack
- Expo Router + React Native, Hermes bytecode (index.android.bundle, 53 MB)
- tRPC backend at *.gizmo.ai (auth.gizmo.ai, api.gizmo.ai, coeus.gizmo.ai, pixie.gizmo.ai, ai.gizmo.ai)
- Auth: Firebase/Auth (identitytoolkit.googleapis.com), Google/Apple/Snapchat OAuth
- Payments: RevenueCat, Apple IAP
- Analytics: Sentry, LogRocket
- AI: gizmo.ai AI tutor (removed per user request)

## Search domains (from bundle strings)
- search_users
- search_classes
- search_decks_and_cards
- search_schools
- search_educational_institutions
- search_queries (history)
- search_metadata, search_intent, search_domain_filter
- search_after_date / search_before_date (date filters)
- search_recency_filter, search_language_filter
- search_exhausted (pagination end)

## UI components referenced
- NativeSearchBar, DockedSearchBar
- debouncerDelay (input debounce)
- useSearchUsersInfiniteQuery (infinite scroll via search_after/search_before)
- searchClassesQuery, searchSchools

## Routes
- (root)/(authenticated)/(tabs)/(home,progress,decks,conversations,profile)
- users/search.tsx — main search screen

## Note
Full JS source is Hermes bytecode and is not recoverable as readable source.
This app (LearningFolder) ports the SEARCH + DECKS + CARDS + SCHOOLS + PROFILE features
using local SQLite + a public placeholder users API, with NO account and NO AI tutor,
as requested.
