import React, { useState, useEffect } from 'react';
import { FaTable } from 'react-icons/fa';
import './TableSelector.css';

export default function TableSelector({
  tables,
  onPreviewTable,
  onBack,
}) {
  const [selected, setSelected] = useState('');
  const [mounted, setMounted]   = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  return (
    <div className={`selector table-selector ${mounted ? 'fade-in' : ''}`}>
      <h3>Select Table</h3>
      <button className="back-btn" onClick={onBack}>← Back</button>

      <div className="table-list">
        {tables.map((tbl) => (
          <div
            key={tbl}
            className={`table-item ${selected === tbl ? 'selected' : ''}`}
            onClick={() => setSelected(tbl)}
          >
            <FaTable className="table-icon" />
            <span className="table-name">{tbl}</span>
          </div>
        ))}
      </div>

      <button
        className="preview-btn"
        onClick={() => onPreviewTable(selected)}
        disabled={!selected}
      >
        Preview Table
      </button>
    </div>
  );
}
