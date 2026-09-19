import { useEffect, useRef, useState } from 'react'
import { api } from '../api'

export default function AiReplyBlock({ id }) {
  const [reply, setReply] = useState(null)
  const [loadingReply, setLoadingReply] = useState(false)
  const [loadingAudio, setLoadingAudio] = useState(false)
  const [error, setError] = useState(null)
  const [audioUrl, setAudioUrl] = useState(null)
  const urlRef = useRef(null)

  useEffect(() => {
    return () => {
      if (urlRef.current) URL.revokeObjectURL(urlRef.current)
    }
  }, [])

  async function handleGenerateReply() {
    setLoadingReply(true)
    setError(null)
    try {
      const text = await api.generateReply(id)
      setReply(text)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoadingReply(false)
    }
  }

  async function handleGenerateAudio() {
    setLoadingAudio(true)
    setError(null)
    try {
      const blob = await api.generateReplyAudio(id)
      if (urlRef.current) URL.revokeObjectURL(urlRef.current)
      const url = URL.createObjectURL(blob)
      urlRef.current = url
      setAudioUrl(url)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoadingAudio(false)
    }
  }

  return (
    <div className="ai-reply">
      {!reply && (
        <button className="btn btn-secondary" type="button" onClick={handleGenerateReply} disabled={loadingReply}>
          {loadingReply ? 'Thinking…' : 'Generate AI reply'}
        </button>
      )}

      {reply && (
        <>
          <p className="ai-reply-text">{reply}</p>
          {!audioUrl && (
            <button className="btn btn-secondary" type="button" onClick={handleGenerateAudio} disabled={loadingAudio}>
              {loadingAudio ? 'Generating audio…' : 'Play as speech'}
            </button>
          )}
          {audioUrl && <audio controls src={audioUrl} />}
        </>
      )}

      {error && <div className="error-msg">{error}</div>}
    </div>
  )
}
