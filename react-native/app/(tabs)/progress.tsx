import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';

export default function Progress() {
  const router = useRouter();
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Progress</Text>
      <TouchableOpacity style={styles.link} onPress={() => router.push('/global-search')}><Text style={styles.linkText}>Global Search</Text></TouchableOpacity>
      <TouchableOpacity style={styles.link} onPress={() => router.push('/users/search')}><Text style={styles.linkText}>Search Users</Text></TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, paddingTop: 60 },
  title: { fontSize: 24, fontWeight: 'bold', marginBottom: 16 },
  link: { padding: 12, borderBottomWidth: 1, borderBottomColor: '#eee' },
  linkText: { fontSize: 16, color: '#2563eb' },
});
