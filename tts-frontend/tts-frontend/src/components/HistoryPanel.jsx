import { useEffect, useRef, useState } from 'react'
import { Play, Pause } from 'lucide-react'
import { api } from '../api'
import AiReplyBlock from './AiReplyBlock.jsx'

export default function HistoryPanel() {
  const [items, setItems] = useState(null)
  const [error, setError] = useState(null)
  const [playingIndex, setPlayingIndex] = useState(null)
  const [openReplyIndex, setOpenReplyIndex] = useState(null)
  const audioRef = useRef(null)
  const urlsRef = useRef([])

  useEffect(() => {
    let cancelled = false
    api
      .getSpeechHistory()
      .then((result) => {
        if (cancelled) return
        urlsRef.current = result.map((item) => (item.audioBlob ? URL.createObjectURL(item.audioBlob) : null))
        setItems(result)
      })
      .catch((err) => !cancelled && setError(err.message))

    return () => {
      cancelled = true
      urlsRef.current.forEach((url) => url && URL.revokeObjectURL(url))
    }
  }, [])

  function togglePlay(index) {
    const audio = audioRef.current
    const url = urlsRef.current[index]
    if (!audio || !url) return

    if (playingIndex === index) {
      audio.pause()
      setPlayingIndex(null)
      return
    }
    audio.src = url
    audio.play()
    setPlayingIndex(index)
  }

  if (error) return <div className="error-msg">{error}</div>
  if (!items) return <div className="loading">Loading…</div>

  return (
    <div className="panel">
      <audio
        ref={audioRef}
        onEnded={() => setPlayingIndex(null)}
        style={{ display: 'none' }}
      />
      {items.length === 0 ? (
        <div className="empty">Nothing generated yet — try the Speak tab.</div>
      ) : (
        <div className="history-list">
          {items.map((item, i) => (
            <div className="history-item-wrap" key={i}>
              <div className="history-item">
                <button
                  className="play-btn"
                  onClick={() => togglePlay(i)}
                  aria-label={playingIndex === i ? 'Pause' : 'Play'}
                  disabled={!urlsRef.current[i]}
                >
                  {playingIndex === i ? <Pause size={14} /> : <Play size={14} />}
                </button>
                <div className="text">{item.text}</div>
                {item.id != null && (
                  <button
                    className="reply-toggle"
                    onClick={() => setOpenReplyIndex(openReplyIndex === i ? null : i)}
                  >
                    {openReplyIndex === i ? 'Hide' : 'AI reply'}
                  </button>
                )}
              </div>
              {openReplyIndex === i && item.id != null && (
                <div className="history-item-reply">
                  <AiReplyBlock id={item.id} />
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
