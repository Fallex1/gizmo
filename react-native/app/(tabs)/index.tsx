import { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, FlatList, ActivityIndicator } from 'react-native';
import { useRouter } from 'expo-router';
import { searchUsers } from '../../lib/api';

export default function Home() {
  const router = useRouter();
  const [q, setQ] = useState('');
  const [results, setResults] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState('');

  const search = async () => {
    if (!q.trim()) return;
    setLoading(true); setErr('');
    try {
      const data = await searchUsers(q.trim());
      const list = Array.isArray(data) ? data : data?.results || data?.users || data?.items || [];
      setResults(list);
    } catch (e: any) {
      setErr(String(e?.message || e));
      setResults([]);
    } finally { setLoading(false); }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>LearningFolder</Text>
      <View style={styles.row}>
        <TextInput style={styles.input} placeholder="Search users..." value={q} onChangeText={setQ} onSubmitEditing={search} returnKeyType="search" />
        <TouchableOpacity style={styles.btn} onPress={search}><Text style={styles.btnText}>Go</Text></TouchableOpacity>
      </View>
      {loading && <ActivityIndicator />}
      {err ? <Text style={styles.err}>{err}</Text> : null}
      <FlatList data={results} keyExtractor={(_, i) => String(i)} renderItem={({ item }) => (
        <View style={styles.item}><Text style={styles.itemTitle}>{item.name || item.username || item.display_name || JSON.stringify(item).slice(0,80)}</Text></View>
      )} ListEmptyComponent={!loading ? <Text style={styles.empty}>No results</Text> : undefined} />
      <View style={styles.links}>
        <TouchableOpacity onPress={() => router.push('/decks/search')}><Text style={styles.link}>Search Decks</Text></TouchableOpacity>
        <TouchableOpacity onPress={() => router.push('/schools/search')}><Text style={styles.link}>Search Schools</Text></TouchableOpacity>
        <TouchableOpacity onPress={() => router.push('/global-search')}><Text style={styles.link}>Global Search</Text></TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, paddingTop: 60 },
  title: { fontSize: 24, fontWeight: 'bold', marginBottom: 16 },
  row: { flexDirection: 'row', marginBottom: 12 },
  input: { flex: 1, borderWidth: 1, borderColor: '#ccc', padding: 10, borderRadius: 8, marginRight: 8 },
  btn: { backgroundColor: '#2563eb', paddingHorizontal: 16, justifyContent: 'center', borderRadius: 8 },
  btnText: { color: '#fff', fontWeight: 'bold' },
  item: { padding: 12, borderBottomWidth: 1, borderBottomColor: '#eee' },
  itemTitle: { fontSize: 16 },
  err: { color: 'red', marginBottom: 8 },
  empty: { color: '#999', textAlign: 'center', marginTop: 20 },
  links: { flexDirection: 'row', flexWrap: 'wrap', gap: 12, marginTop: 12 },
  link: { color: '#2563eb', marginRight: 12, marginBottom: 8 },
});
