import { useEffect, useRef, useState } from 'react'
import { api } from '../api'
import AiReplyBlock from './AiReplyBlock.jsx'

const MAX_CHARS = 500

export default function SpeakPanel({ languages, voices }) {
  const [text, setText] = useState('')
  const [language, setLanguage] = useState(languages[0] || '')
  const [voice, setVoice] = useState(voices[0]?.voiceName || '')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [audioUrl, setAudioUrl] = useState(null)
  const [speechId, setSpeechId] = useState(null)
  const urlRef = useRef(null)

  useEffect(() => {
    return () => {
      if (urlRef.current) URL.revokeObjectURL(urlRef.current)
    }
  }, [])

  async function handleSubmit(e) {
    e.preventDefault()
    if (!text.trim()) {
      setError('Type something to speak.')
      return
    }
    setLoading(true)
    setError(null)
    try {
      const { blob, id } = await api.generateSpeech({ text: text.trim(), voice, language })
      if (urlRef.current) URL.revokeObjectURL(urlRef.current)
      const url = URL.createObjectURL(blob)
      urlRef.current = url
      setAudioUrl(url)
      setSpeechId(id)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const overLimit = text.length > MAX_CHARS

  return (
    <form className="panel" onSubmit={handleSubmit}>
      <div className="field">
        <label htmlFor="speak-text">Text</label>
        <textarea
          id="speak-text"
          placeholder="Type what you'd like to hear…"
          value={text}
          onChange={(e) => setText(e.target.value)}
        />
        <div className={`char-count ${overLimit ? 'limit' : ''}`}>
          {text.length}/{MAX_CHARS}
        </div>
      </div>

      <div className="field-row">
        <div className="field">
          <label htmlFor="speak-language">Language</label>
          <select id="speak-language" value={language} onChange={(e) => setLanguage(e.target.value)}>
            {languages.map((l) => (
              <option key={l} value={l}>
                {l}
              </option>
            ))}
          </select>
        </div>
        <div className="field">
          <label htmlFor="speak-voice">Voice</label>
          <select id="speak-voice" value={voice} onChange={(e) => setVoice(e.target.value)}>
            {voices.map((v) => (
              <option key={v.voiceName} value={v.voiceName}>
                {v.voiceName} ({v.gender})
              </option>
            ))}
          </select>
        </div>
      </div>

      <button className="btn" type="submit" disabled={loading || overLimit}>
        {loading ? 'Generating…' : 'Generate speech'}
      </button>

      {error && <div className="error-msg">{error}</div>}

      {audioUrl && (
        <div className="result">
          <audio controls src={audioUrl} />
          <div className="result-actions">
            <a className="btn btn-secondary" href={audioUrl} download="speech.wav">
              Download
            </a>
          </div>
          {speechId != null && (
            <div className="ai-reply-wrap">
              <AiReplyBlock key={speechId} id={speechId} />
            </div>
          )}
        </div>
      )}
    </form>
  )
}
