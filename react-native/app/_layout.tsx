import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';

export default function RootLayout() {
  return (
    <>
      <StatusBar style="dark" />
      <Stack>
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="users/search" options={{ title: 'Search Users' }} />
        <Stack.Screen name="decks/search" options={{ title: 'Search Decks' }} />
        <Stack.Screen name="global-search" options={{ title: 'Global Search' }} />
        <Stack.Screen name="schools/search" options={{ title: 'Search Schools' }} />
      </Stack>
    </>
  );
}
