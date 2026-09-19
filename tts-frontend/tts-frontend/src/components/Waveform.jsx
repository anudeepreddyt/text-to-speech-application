const HEIGHTS = [6, 12, 18, 10, 20, 14, 8, 16, 11, 19, 7, 13]

export default function Waveform() {
  return (
    <div className="waveform" aria-hidden="true">
      {HEIGHTS.map((h, i) => (
        <span key={i} style={{ height: `${h}px` }} />
      ))}
    </div>
  )
}
