import { useState } from 'react';
import { View, Text, TextInput, FlatList, ActivityIndicator, StyleSheet, TouchableOpacity } from 'react-native';
import { searchUsers, searchDecksAndCards } from '../lib/api';

export default function GlobalSearch() {
  const [q, setQ] = useState('');
  const [results, setResults] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState('');

  const search = async () => {
    if (!q.trim()) return;
    setLoading(true); setErr('');
    try {
      const [u, d] = await Promise.allSettled([searchUsers(q.trim()), searchDecksAndCards(q.trim())]);
      const users = u.status === 'fulfilled' ? (Array.isArray(u.value) ? u.value : u.value?.users || u.value?.results || []) : [];
      const decks = d.status === 'fulfilled' ? (Array.isArray(d.value) ? d.value : d.value?.decks || d.value?.results || []) : [];
      setResults([...users.map((x: any) => ({ ...x, _type: 'user' })), ...decks.map((x: any) => ({ ...x, _type: 'deck' }))]);
    } catch (e: any) { setErr(String(e?.message || e)); setResults([]); }
    finally { setLoading(false); }
  };

  return (
    <View style={styles.container}>
      <View style={styles.row}>
        <TextInput style={styles.input} placeholder="Global search..." value={q} onChangeText={setQ} onSubmitEditing={search} returnKeyType="search" />
        <TouchableOpacity style={styles.btn} onPress={search}><Text style={styles.btnText}>Go</Text></TouchableOpacity>
      </View>
      {loading && <ActivityIndicator />}
      {err ? <Text style={styles.err}>{err}</Text> : null}
      <FlatList data={results} keyExtractor={(_, i) => String(i)} renderItem={({ item }) => (
        <View style={styles.item}>
          <Text style={styles.type}>{item._type}</Text>
          <Text style={styles.itemTitle}>{item.name || item.username || item.title || item.deck_name || JSON.stringify(item).slice(0,60)}</Text>
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
  type: { fontSize: 11, color: '#999', textTransform: 'uppercase' },
  itemTitle: { fontSize: 16, fontWeight: '600' },
  err: { color: 'red', marginBottom: 8 },
  empty: { color: '#999', textAlign: 'center', marginTop: 20 },
});
