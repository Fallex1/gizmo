export const API_BASE = 'https://coeus.gizmo.ai';

export async function api(path: string, opts: RequestInit = {}) {
  const url = path.startsWith('http') ? path : `${API_BASE}${path}`;
  const res = await fetch(url, {
    ...opts,
    headers: { 'Content-Type': 'application/json', ...(opts.headers || {}) },
  });
  const text = await res.text();
  let data: any;
  try { data = JSON.parse(text); } catch { data = text; }
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}: ${typeof data === 'string' ? data.slice(0,200) : JSON.stringify(data).slice(0,200)}`);
  return data;
}

export async function searchUsers(query: string) {
  return api('/api/search_users', { method: 'POST', body: JSON.stringify({ query }) });
}

export async function searchDecksAndCards(query: string) {
  return api('/api/search_decks_and_cards', { method: 'POST', body: JSON.stringify({ query }) });
}

export async function getEducationalInstitutions(query: string) {
  return api('/api/get_educational_institution_info', { method: 'POST', body: JSON.stringify({ query }) });
}

export async function getClassesAvailable() {
  return api('/api/get_classes_available', { method: 'GET' });
}

export async function getSortedContactBook() {
  return api('/api/get_sorted_contact_book', { method: 'GET' });
}

export async function getSuggestedFriends() {
  return api('/api/get_suggested_friends', { method: 'GET' });
}

export async function getTopPublicDecks() {
  return api('/api/top_public_decks', { method: 'GET' });
}
