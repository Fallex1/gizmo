import { useState } from 'react';
import { View, Text, TextInput, FlatList, ActivityIndicator, StyleSheet, TouchableOpacity } from 'react-native';
import { searchUsers, getSuggestedFriends, getSortedContactBook } from '../../lib/api';

export default function UserSearch() {
  const [q, setQ] = useState('');
  const [results, setResults] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState('');
  const [tab, setTab] = useState<'search' | 'suggested' | 'contacts'>('search');

  const run = async (fn: () => Promise<any>) => {
    setLoading(true); setErr('');
    try {
      const data = await fn();
      const list = Array.isArray(data) ? data : data?.users || data?.results || data?.items || data?.friends || [];
      setResults(list);
    } catch (e: any) { setErr(String(e?.message || e)); setResults([]); }
    finally { setLoading(false); }
  };

  const search = () => { if (q.trim()) run(() => searchUsers(q.trim())); };

  const loadSuggested = () => { setTab('suggested'); run(() => getSuggestedFriends()); };
  const loadContacts = () => { setTab('contacts'); run(() => getSortedContactBook()); };

  return (
    <View style={styles.container}>
      <View style={styles.row}>
        <TextInput style={styles.input} placeholder="Search users..." value={q} onChangeText={setQ} onSubmitEditing={search} returnKeyType="search" />
        <TouchableOpacity style={styles.btn} onPress={search}><Text style={styles.btnText}>Go</Text></TouchableOpacity>
      </View>
      <View style={styles.tabs}>
        <TouchableOpacity onPress={loadSuggested}><Text style={[styles.tab, tab==='suggested' && styles.tabActive]}>Suggested</Text></TouchableOpacity>
        <TouchableOpacity onPress={loadContacts}><Text style={[styles.tab, tab==='contacts' && styles.tabActive]}>Contacts</Text></TouchableOpacity>
      </View>
      {loading && <ActivityIndicator />}
      {err ? <Text style={styles.err}>{err}</Text> : null}
      <FlatList data={results} keyExtractor={(_, i) => String(i)} renderItem={({ item }) => (
        <View style={styles.item}>
          <Text style={styles.itemTitle}>{item.name || item.username || item.display_name || item.full_name || JSON.stringify(item).slice(0,60)}</Text>
          {item.bio ? <Text style={styles.itemSub}>{item.bio}</Text> : null}
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
  tabs: { flexDirection: 'row', gap: 16, marginBottom: 12 },
  tab: { color: '#666', paddingVertical: 6 },
  tabActive: { color: '#2563eb', fontWeight: 'bold' },
  item: { padding: 12, borderBottomWidth: 1, borderBottomColor: '#eee' },
  itemTitle: { fontSize: 16, fontWeight: '600' },
  itemSub: { color: '#666', fontSize: 13, marginTop: 2 },
  err: { color: 'red', marginBottom: 8 },
  empty: { color: '#999', textAlign: 'center', marginTop: 20 },
});
