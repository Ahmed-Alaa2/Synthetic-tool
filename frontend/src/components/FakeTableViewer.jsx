// src/components/FakeTableViewer.jsx
import React from 'react';

export default function FakeTableViewer({ data, onRestart, onUpload }) {
  if (!data || data.length === 0) {
    return (
      <div className="selector">
        <h3>No fake data</h3>
        <button onClick={onRestart}>Start Over</button>
      </div>
    );
  }

  const cols = Object.keys(data[0]);

  return (
    <div className="selector">
      <h3>Fake Table Preview</h3>

      <div style={{ marginBottom: 12 }}>
        <button onClick={onRestart}>Start Over</button>
        <button onClick={onUpload} style={{ marginLeft: 8 }}>
          Upload to your database
        </button>
      </div>

      <div style={{ overflowX: 'auto' }}>
        <table>
          <thead>
            <tr>{cols.map(c => <th key={c}>{c}</th>)}</tr>
          </thead>
          <tbody>
            {data.map((row, i) => (
              <tr key={i}>
                {cols.map(c => <td key={c}>{row[c]}</td>)}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
