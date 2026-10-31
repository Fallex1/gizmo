import { useState, useEffect } from 'react';
import { View, Text, TextInput, FlatList, ActivityIndicator, StyleSheet, TouchableOpacity } from 'react-native';
import { getTopPublicDecks, searchDecksAndCards } from '../../lib/api';

export default function Decks() {
  const [decks, setDecks] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [q, setQ] = useState('');
  const [searching, setSearching] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const data = await getTopPublicDecks();
        setDecks(Array.isArray(data) ? data : data?.decks || data?.results || []);
      } catch (e) { setDecks([]); }
      finally { setLoading(false); }
    })();
  }, []);

  const search = async () => {
    if (!q.trim()) return;
    setSearching(true);
    try {
      const data = await searchDecksAndCards(q.trim());
      const list = Array.isArray(data) ? data : data?.decks || data?.results || data?.cards || [];
      setDecks(list);
    } catch (e) { setDecks([]); }
    finally { setSearching(false); }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Decks</Text>
      <View style={styles.row}>
        <TextInput style={styles.input} placeholder="Search decks/cards..." value={q} onChangeText={setQ} onSubmitEditing={search} returnKeyType="search" />
        <TouchableOpacity style={styles.btn} onPress={search}><Text style={styles.btnText}>{searching ? '...' : 'Search'}</Text></TouchableOpacity>
      </View>
      {loading ? <ActivityIndicator /> : (
        <FlatList data={decks} keyExtractor={(_, i) => String(i)} renderItem={({ item }) => (
          <View style={styles.item}>
            <Text style={styles.itemTitle}>{item.title || item.name || item.deck_name || JSON.stringify(item).slice(0,60)}</Text>
            {item.description ? <Text style={styles.itemSub}>{item.description}</Text> : null}
          </View>
        )} ListEmptyComponent={<Text style={styles.empty}>No decks</Text>} />
      )}
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
  itemTitle: { fontSize: 16, fontWeight: '600' },
  itemSub: { color: '#666', fontSize: 13, marginTop: 2 },
  empty: { color: '#999', textAlign: 'center', marginTop: 20 },
});
