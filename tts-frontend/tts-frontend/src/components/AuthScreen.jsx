import { useState } from 'react'
import { api } from '../api'
import Waveform from './Waveform.jsx'

export default function AuthScreen({ onAuthenticated }) {
  const [mode, setMode] = useState('login') // login | register
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [info, setInfo] = useState(null)

  function switchMode(next) {
    setMode(next)
    setError(null)
    setInfo(null)
  }

  async function handleSubmit(e) {
    e.preventDefault()
    if (!username.trim() || !password) {
      setError('Enter a username and password.')
      return
    }
    setLoading(true)
    setError(null)
    setInfo(null)
    try {
      if (mode === 'login') {
        await api.login(username.trim(), password)
        onAuthenticated(username.trim())
      } else {
        await api.register(username.trim(), password)
        setMode('login')
        setInfo('Account created — log in below.')
        setPassword('')
      }
    } catch (err) {
      setError(
        mode === 'login'
          ? err.message || 'Login failed — check your username and password.'
          : err.message || "Couldn't create that account — try a different username."
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-wrap">
      <div className="wordmark">
        <h1>Speak</h1>
      </div>
      <Waveform />

      <form className="panel" onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="username">Username</label>
          <input
            id="username"
            type="text"
            autoComplete="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>
        <div className="field">
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button className="btn" type="submit" disabled={loading} style={{ width: '100%' }}>
          {loading ? 'Please wait…' : mode === 'login' ? 'Log in' : 'Create account'}
        </button>
        {error && <div className="error-msg">{error}</div>}
        {info && <div className="info-msg">{info}</div>}
      </form>

      <div className="auth-switch">
        {mode === 'login' ? (
          <>
            New here?{' '}
            <button className="btn-link" type="button" onClick={() => switchMode('register')}>
              Create an account
            </button>
          </>
        ) : (
          <>
            Already have an account?{' '}
            <button className="btn-link" type="button" onClick={() => switchMode('login')}>
              Log in
            </button>
          </>
        )}
      </div>
    </div>
  )
}
