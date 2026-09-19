import { useEffect, useRef, useState } from 'react'
import { api } from '../api'
import AiReplyBlock from './AiReplyBlock.jsx'

export default function DocumentPanel({ languages, voices }) {
  const [file, setFile] = useState(null)
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
    if (!file) {
      setError('Choose a PDF or Word (.docx) file.')
      return
    }
    setLoading(true)
    setError(null)
    try {
      const { blob, id } = await api.generateSpeechFromDocument({ file, voice, language })
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

  return (
    <form className="panel" onSubmit={handleSubmit}>
      <div className="field">
        <label htmlFor="doc-file">Document (PDF or .docx)</label>
        <input
          id="doc-file"
          type="file"
          accept=".pdf,.docx"
          onChange={(e) => setFile(e.target.files?.[0] || null)}
        />
      </div>

      <div className="field-row">
        <div className="field">
          <label htmlFor="doc-language">Language</label>
          <select id="doc-language" value={language} onChange={(e) => setLanguage(e.target.value)}>
            {languages.map((l) => (
              <option key={l} value={l}>
                {l}
              </option>
            ))}
          </select>
        </div>
        <div className="field">
          <label htmlFor="doc-voice">Voice</label>
          <select id="doc-voice" value={voice} onChange={(e) => setVoice(e.target.value)}>
            {voices.map((v) => (
              <option key={v.voiceName} value={v.voiceName}>
                {v.voiceName} ({v.gender})
              </option>
            ))}
          </select>
        </div>
      </div>

      <button className="btn" type="submit" disabled={loading}>
        {loading ? 'Reading document…' : 'Generate speech'}
      </button>

      {error && <div className="error-msg">{error}</div>}

      {audioUrl && (
        <div className="result">
          <audio controls src={audioUrl} />
          <div className="result-actions">
            <a className="btn btn-secondary" href={audioUrl} download="document-speech.wav">
              Download
            </a>
          </div>
          {speechId != null && (
            <div className="ai-reply-wrap">
              <AiReplyBlock id={speechId} />
            </div>
          )}
        </div>
      )}
    </form>
  )
}
