import React, { useState, useEffect } from 'react';
import { FaTable } from 'react-icons/fa';
import './TablePreview.css';

export default function TablePreview({ previewData, onConfigure, onBack }) {
  const cols = previewData[0] ? Object.keys(previewData[0]) : [];

  return (
    <div className="selector">
      <h3>Table Preview</h3>
      <button onClick={onBack}>Back</button>

      <div style={{ overflowX:'auto', marginTop:16 }}>
        <table>
          <thead>
            <tr>{cols.map(c => <th key={c}>{c}</th>)}</tr>
          </thead>
          <tbody>
            {previewData.map((row,i) => (
              <tr key={i}>
                {cols.map(c => <td key={c}>{String(row[c])}</td>)}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <button
        onClick={onConfigure}
        style={{ marginTop:12 }}
      >
        Configure Columns
      </button>
    </div>
  );
}
