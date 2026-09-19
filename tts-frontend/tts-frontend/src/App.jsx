import { useEffect, useState } from 'react'
import { api, isAuthenticated, getUsername, setOnSessionExpired } from './api'
import AuthScreen from './components/AuthScreen.jsx'
import Waveform from './components/Waveform.jsx'
import SpeakPanel from './components/SpeakPanel.jsx'
import DocumentPanel from './components/DocumentPanel.jsx'
import HistoryPanel from './components/HistoryPanel.jsx'

const TABS = [
  { id: 'speak', label: 'Speak' },
  { id: 'document', label: 'From a document' },
  { id: 'history', label: 'History' },
]

export default function App() {
  const [authed, setAuthed] = useState(isAuthenticated())
  const [username, setUsername] = useState(getUsername())
  const [tab, setTab] = useState('speak')

  const [languages, setLanguages] = useState(null)
  const [voices, setVoices] = useState(null)
  const [loadError, setLoadError] = useState(null)

  useEffect(() => {
    setOnSessionExpired(() => {
      setAuthed(false)
    })
  }, [])

  useEffect(() => {
    if (!authed) return
    let cancelled = false
    Promise.all([api.getLanguages(), api.getVoices()])
      .then(([langs, vs]) => {
        if (cancelled) return
        setLanguages(langs)
        setVoices(vs)
      })
      .catch((err) => !cancelled && setLoadError(err.message))
    return () => {
      cancelled = true
    }
  }, [authed])

  function handleAuthenticated(name) {
    setUsername(name)
    setAuthed(true)
  }

  async function handleLogout() {
    await api.logout().catch(() => {})
    setAuthed(false)
    setLanguages(null)
    setVoices(null)
  }

  if (!authed) {
    return <AuthScreen onAuthenticated={handleAuthenticated} />
  }

  return (
    <div className="page">
      <div className="wordmark">
        <h1>Speak</h1>
        <div className="session">
          <span>{username}</span>
          <button onClick={handleLogout}>Log out</button>
        </div>
      </div>
      <Waveform />

      <div className="tabs">
        {TABS.map((t) => (
          <button
            key={t.id}
            className={`tab-pill ${tab === t.id ? 'active' : ''}`}
            onClick={() => setTab(t.id)}
          >
            {t.label}
          </button>
        ))}
      </div>

      {loadError && <div className="error-msg">{loadError}</div>}

      {!loadError && !languages && <div className="loading">Loading…</div>}

      {languages && voices && (
        <>
          {tab === 'speak' && <SpeakPanel languages={languages} voices={voices} />}
          {tab === 'document' && <DocumentPanel languages={languages} voices={voices} />}
          {tab === 'history' && <HistoryPanel />}
        </>
      )}
    </div>
  )
}
