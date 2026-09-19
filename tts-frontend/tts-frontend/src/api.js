// Talks to the text-to-speech-backend Spring Boot API.
// Change VITE_API_BASE_URL in a .env file if your backend runs somewhere
// other than http://localhost:8080.
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const AUTH_KEY = 'tts.authToken'
const REFRESH_KEY = 'tts.refreshToken'
const USERNAME_KEY = 'tts.username'

export function getUsername() {
  return localStorage.getItem(USERNAME_KEY) || ''
}

export function isAuthenticated() {
  return Boolean(localStorage.getItem(AUTH_KEY))
}

function getAuthToken() {
  return localStorage.getItem(AUTH_KEY)
}

function getRefreshToken() {
  return localStorage.getItem(REFRESH_KEY)
}

function setTokens({ authToken, refreshToken }) {
  if (authToken) localStorage.setItem(AUTH_KEY, authToken)
  if (refreshToken) localStorage.setItem(REFRESH_KEY, refreshToken)
}

function clearSession() {
  localStorage.removeItem(AUTH_KEY)
  localStorage.removeItem(REFRESH_KEY)
  localStorage.removeItem(USERNAME_KEY)
}

// App.jsx registers a callback here so the api layer can force a logout
// when the refresh token itself has expired.
let onSessionExpired = () => {}
export function setOnSessionExpired(fn) {
  onSessionExpired = fn
}

function friendlyMessage(status, body) {
  if (body && typeof body === 'object' && body.message) return body.message
  if (typeof body === 'string' && body.trim()) return body
  if (status === 401 || status === 403) return 'You need to log in again.'
  if (status === 404) return "That couldn't be found."
  if (status >= 500) return 'The server ran into a problem. Please try again.'
  return `Request failed (${status})`
}

// options.auth: attach the bearer token (default true)
// options.raw: response body is binary audio, resolve to a Blob
// options.rawWithHeaders: like raw, but resolve to { blob, headers } so
//   callers can read response headers (e.g. a speech id)
// options.retry: internal — set on the one allowed retry after a refresh
async function request(path, options = {}) {
  const { auth = true, raw = false, rawWithHeaders = false, retry = false, ...fetchOptions } = options
  const headers = new Headers(fetchOptions.headers || {})

  const isFormData = fetchOptions.body instanceof FormData
  if (!isFormData && fetchOptions.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }
  if (auth) {
    const token = getAuthToken()
    if (token) headers.set('Authorization', `Bearer ${token}`)
  }

  let res
  try {
    res = await fetch(`${BASE_URL}${path}`, { ...fetchOptions, headers })
  } catch (networkError) {
    throw new Error("Can't reach the backend. Is it running, and is CORS enabled for this origin?")
  }

  // Access token expired mid-session: try one silent refresh, then retry once.
  if (res.status === 401 && auth && !retry && getRefreshToken()) {
    const refreshed = await tryRefresh()
    if (refreshed) {
      return request(path, { ...options, retry: true })
    }
    onSessionExpired()
    throw new Error('Your session expired. Please log in again.')
  }

  if (raw || rawWithHeaders) {
    if (!res.ok) {
      let body = null
      try {
        body = await res.json()
      } catch {}
      throw new Error(friendlyMessage(res.status, body))
    }
    const blob = await res.blob()
    return rawWithHeaders ? { blob, headers: res.headers } : blob
  }

  const contentType = res.headers.get('content-type') || ''
  const text = await res.text()
  const body = text && contentType.includes('application/json') ? JSON.parse(text) : text

  if (!res.ok) {
    throw new Error(friendlyMessage(res.status, body))
  }
  return body
}

async function tryRefresh() {
  const refreshToken = getRefreshToken()
  if (!refreshToken) return false
  try {
    const result = await request(`/refreshToken?token=${encodeURIComponent(refreshToken)}`, {
      method: 'POST',
      auth: false,
    })
    if (result && result.authToken) {
      setTokens({ authToken: result.authToken })
      return true
    }
    return false
  } catch {
    return false
  }
}

function base64ToBlob(base64, mime = 'audio/wav') {
  const binary = atob(base64)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i)
  return new Blob([bytes], { type: mime })
}

export const api = {
  async register(username, password) {
    return request('/register', {
      method: 'POST',
      auth: false,
      body: JSON.stringify({ username, password }),
    })
  },

  async login(username, password) {
    const result = await request('/login', {
      method: 'POST',
      auth: false,
      body: JSON.stringify({ username, password }),
    })
    setTokens(result)
    localStorage.setItem(USERNAME_KEY, username)
    return result
  },

  async logout() {
    const refreshToken = getRefreshToken()
    try {
      if (refreshToken) {
        await request(`/logout?token=${encodeURIComponent(refreshToken)}`, {
          method: 'POST',
          auth: false,
        })
      }
    } finally {
      clearSession()
    }
  },

  getLanguages() {
    return request('/languages')
  },

  getVoices() {
    return request('/voices')
  },

  // `id` is only present once the backend is patched to send it back
  // (see README) — an "X-Speech-Id" response header. Older backends will
  // simply omit it, and callers should treat a null id as "feature
  // unavailable for this clip".
  async generateSpeech({ text, voice, language }) {
    const { blob, headers } = await request('/tts', {
      method: 'POST',
      rawWithHeaders: true,
      body: JSON.stringify({ text, voice, language }),
    })
    return { blob, id: parseIdHeader(headers) }
  },

  async generateSpeechFromDocument({ file, voice, language }) {
    const form = new FormData()
    form.append('file', file)
    form.append('voice', voice)
    form.append('language', language)
    const { blob, headers } = await request('/document', {
      method: 'POST',
      rawWithHeaders: true,
      body: form,
    })
    return { blob, id: parseIdHeader(headers) }
  },

  async getSpeechHistory() {
    const items = await request('/speechHistory')
    return (items || []).map((item) => ({
      // present only once SpeechHistoryDTO carries `id` (see README)
      id: item.id ?? null,
      text: item.text,
      audioBlob: item.audioformat ? base64ToBlob(item.audioformat) : null,
    }))
  },

  // AI-reply endpoints — all require the id patch described in the README.
  generateReply(id) {
    return request(`/generateReply/${id}`)
  },

  async generateReplyAudio(id) {
    return request(`/generateAi/${id}/audio`, { raw: true })
  },
}

function parseIdHeader(headers) {
  const raw = headers.get('X-Speech-Id')
  if (!raw) return null
  const id = Number(raw)
  return Number.isFinite(id) ? id : null
}
