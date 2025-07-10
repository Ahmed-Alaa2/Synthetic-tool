import React, { useState, useEffect } from 'react';
import { FaDatabase } from 'react-icons/fa';
import './DatabaseSelector.css';

export default function DatabaseSelector({ databases, onSelectDb, onBack }) {
  const [selected, setSelected] = useState('');

  // small fade-in when component mounts
  const [mounted, setMounted] = useState(false);
  useEffect(() => {
    setMounted(true);
  }, []);

  return (
    <div className={`selector db-selector ${mounted ? 'fade-in' : ''}`}>
      <h3>Select Database</h3>
      <button className="back-btn" onClick={onBack}>← Back</button>

      <div className="db-list">
        {databases.map((db) => (
          <div
            key={db}
            className={`db-item ${selected === db ? 'selected' : ''}`}
            onClick={() => setSelected(db)}
          >
            <FaDatabase className="db-icon" />
            <span className="db-name">{db}</span>
          </div>
        ))}
      </div>

      <button
        className="load-btn"
        onClick={() => onSelectDb(selected)}
        disabled={!selected}
      >
        Load Tables
      </button>
    </div>
  );
}
