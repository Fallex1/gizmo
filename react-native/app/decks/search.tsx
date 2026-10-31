import { useState } from 'react';
import { View, Text, TextInput, FlatList, ActivityIndicator, StyleSheet, TouchableOpacity } from 'react-native';
import { searchDecksAndCards } from '../../lib/api';

export default function DeckSearch() {
  const [q, setQ] = useState('');
  const [results, setResults] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState('');

  const search = async () => {
    if (!q.trim()) return;
    setLoading(true); setErr('');
    try {
      const data = await searchDecksAndCards(q.trim());
      const list = Array.isArray(data) ? data : data?.decks || data?.results || data?.cards || [];
      setResults(list);
    } catch (e: any) { setErr(String(e?.message || e)); setResults([]); }
    finally { setLoading(false); }
  };

  return (
    <View style={styles.container}>
      <View style={styles.row}>
        <TextInput style={styles.input} placeholder="Search decks & cards..." value={q} onChangeText={setQ} onSubmitEditing={search} returnKeyType="search" />
        <TouchableOpacity style={styles.btn} onPress={search}><Text style={styles.btnText}>Go</Text></TouchableOpacity>
      </View>
      {loading && <ActivityIndicator />}
      {err ? <Text style={styles.err}>{err}</Text> : null}
      <FlatList data={results} keyExtractor={(_, i) => String(i)} renderItem={({ item }) => (
        <View style={styles.item}>
          <Text style={styles.itemTitle}>{item.title || item.name || item.deck_name || JSON.stringify(item).slice(0,60)}</Text>
          {item.description ? <Text style={styles.itemSub}>{item.description}</Text> : null}
        </View>
      )} ListEmptyComponent={!loading ? <Text style={styles.empty}>No results</Text> : undefined} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, paddingTop: 16 },
  row: { flexDirection: 'row', marginBottom: 12 },
  input: { flex: 1, borderWidth: 1, borderColor: '#ccc', padding: 10, borderRadius: 8, marginRight: 8 },
  btn: { backgroundColor: '#2563eb', paddingHorizontal: 16, justifyContent: 'center', borderRadius: 8 },
  btnText: { color: '#fff', fontWeight: 'bold' },
  item: { padding: 12, borderBottomWidth: 1, borderBottomColor: '#eee' },
  itemTitle: { fontSize: 16, fontWeight: '600' },
  itemSub: { color: '#666', fontSize: 13, marginTop: 2 },
  err: { color: 'red', marginBottom: 8 },
  empty: { color: '#999', textAlign: 'center', marginTop: 20 },
});
