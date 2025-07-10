import React, { useState, useEffect } from 'react';

const COLUMN_TYPES = [
  'CUSTOMER_NAME',
  'PHONE_NUMBER',
  'EMAIL',
  'BALANCE',
  'ACCOUNT_NUMBER',
  'IBAN',
  'IS_CORPORATE_CUSTOMER',
  'CUSTOMER_NUMBER',
  'NATIONAL_ID',
  'ADDRESS',
  'UNKNOWN',
];

export default function ColumnClassifier({
  previewData,   // real rows: [{ col1:…, col2:…, … }, …]
  columns,       // suggestions: [{ columnName: 'col1', suggestion: 'CUSTOMER_NAME' }, …]
  onComplete,
  onBack,
}) {
  // 1) get the raw SQL keys
  const realKeys = previewData && previewData.length > 0
    ? Object.keys(previewData[0])
    : [];

  // 2) build working list of { columnName: rawKey, suggestion: inferredOrOverride }
  const [working, setWorking] = useState([]);
  useEffect(() => {
    const list = realKeys.map((col, i) => ({
      columnName: col,
      suggestion: columns[i]?.suggestion || 'UNKNOWN'
    }));
    setWorking(list);
  }, [previewData, columns, realKeys]);

  const [idx, setIdx] = useState(0);
  const [editing, setEditing] = useState(false);

  const curr = working[idx] || { columnName: '', suggestion: '' };

  const confirm = () => {
    if (idx + 1 === working.length) {
      onComplete(working);
    } else {
      setIdx(idx + 1);
      setEditing(false);
    }
  };

  const change = (val) => {
    const next = [...working];
    next[idx].suggestion = val;
    setWorking(next);
    setEditing(false);
  };

  return (
    <div className="classifier">
      {/* ===== REAL DATA PREVIEW ===== */}
      <h3>Table Preview</h3>
      <div style={{ overflowX: 'auto', marginBottom: 24 }}>
        <table>
          <thead>
            <tr>
              {realKeys.map((col) => (
                <th key={col}>{col}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {previewData.map((row, rIdx) => (
              <tr key={rIdx}>
                {realKeys.map((col) => (
                  <td key={col}>{row[col]}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* ===== CLASSIFICATION WIZARD ===== */}
      <h3>
        Classify Columns ({idx + 1} / {working.length})
      </h3>
      <button onClick={onBack}>Back</button>

      <p>
        <strong>Column:</strong> {curr.columnName}
      </p>
      <p>
        <strong>Suggested Type:</strong> {curr.suggestion}
      </p>

      {editing ? (
        <>
          <label htmlFor="edit">Override Type</label>
          <select
            id="edit"
            value={curr.suggestion}
            onChange={(e) => change(e.target.value)}
          >
            {COLUMN_TYPES.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        </>
      ) : (
        <button onClick={() => setEditing(true)}>✎ Edit</button>
      )}

      <button onClick={confirm}>✔ Confirm</button>
    </div>
  );
}
