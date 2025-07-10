// src/App.jsx
import React, { useState } from 'react';
import axios from 'axios';

import ConnectionForm     from './components/ConnectionForm';
import DatabaseSelector   from './components/DatabaseSelector';
import TableSelector      from './components/TableSelector';
import TablePreview       from './components/TablePreview';
import ColumnConfigurator from './components/ColumnConfigurator';
import FakeTableViewer    from './components/FakeTableViewer';

import './App.css';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export default function App() {
  const [step, setStep]         = useState(1);
  const [conn, setConn]         = useState(null);
  const [databases, setDatabases] = useState([]);
  const [tables, setTables]     = useState([]);
  const [previewData, setPreviewData] = useState([]);
  const [configData, setConfigData]   = useState([]);
  const [fakeData, setFakeData]     = useState([]);
  const [ctx, setCtx]           = useState({}); // { database, table, totalRows }

  const handleConnect = async (props) => {
    try {
      const res = await axios.post(`${API_URL}/api/connect/databases`, props);
      setDatabases(res.data);
      setConn(props);
      setStep(2);
    } catch {
      alert('Invalid credentials — please check host, port, username & password.');
    }
  };

  const handleSelectDb = async (db) => {
    const res = await axios.post(`${API_URL}/api/${encodeURIComponent(db)}/tables`, conn);
    setTables(res.data);
    setCtx({ database: db });
    setStep(3);
  };

  const handlePreviewTable = async (table) => {
    const previewRes = await axios.post(
      `${API_URL}/api/${encodeURIComponent(ctx.database)}/tables/${encodeURIComponent(table)}/preview?limit=10`,
      conn
    );
    setPreviewData(previewRes.data);
    setCtx(c => ({ ...c, table }));
    setStep(4);
  };

  const handleConfigure = async () => {
    const countRes = await axios.post(
      `${API_URL}/api/${encodeURIComponent(ctx.database)}/tables/${encodeURIComponent(ctx.table)}/count`,
      conn
    );
    setCtx(c => ({ ...c, totalRows: countRes.data }));

    const configRes = await axios.post(
      `${API_URL}/api/${encodeURIComponent(ctx.database)}/${encodeURIComponent(ctx.table)}/configure`,
      { connection: conn, sampleSize: 10, overrides: {} }
    );
    setConfigData(configRes.data);
    setStep(5);
  };

  const handleGenerateFake = async (finalConfig) => {
const overrides = finalConfig.reduce((map, r) => {
  map[r.columnName] = {
    type:       r.suggestion,
    dataType:   r.dataType,
    // custom‐text fields as before...
    startsWith: r.textPrefix  || '',
    endsWith:   r.textSuffix  || '',

    // numeric bounds
    min:   r.numStart  ? parseInt(r.numStart, 10) : null,
    max:   r.numEnd    ? parseInt(r.numEnd,   10) : null,

   // NEW: exact digit length
   length: r.numLength ? parseInt(r.numLength, 10) : null
  };
  return map;
}, {});

    const fakeRes = await axios.post(
      `${API_URL}/api/${encodeURIComponent(ctx.database)}/${encodeURIComponent(ctx.table)}/fake`,
      { connection: conn, rowCount: ctx.totalRows, columns: overrides }
    );
    setFakeData(fakeRes.data);
    setStep(6);
  };

  const handleUploadFake = async () => {
    try {
      await axios.post(
        `${API_URL}/api/${encodeURIComponent(ctx.database)}/${encodeURIComponent(ctx.table)}/upload`,
        { connection: conn, rows: fakeData }
      );
      alert('Upload successful!');
    } catch (e) {
      const msg = e.response?.data?.message || e.message;
      alert('Upload failed: ' + msg);
    }
  };

  return (
    <>
      <header className="app-header">
        <h1>BVS-MEA Synthetic Data Tool</h1>
      </header>

      <main className="app-container">
        {step === 1 && <ConnectionForm onConnect={handleConnect} />}

        {step === 2 && (
          <DatabaseSelector
            databases={databases}
            onSelectDb={handleSelectDb}
            onBack={() => setStep(1)}
          />
        )}

        {step === 3 && (
          <TableSelector
            tables={tables}
            onPreviewTable={handlePreviewTable}
            onBack={() => setStep(2)}
          />
        )}

        {step === 4 && (
          <TablePreview
            previewData={previewData}
            onConfigure={handleConfigure}
            onBack={() => setStep(3)}
          />
        )}

        {step === 5 && (
          <ColumnConfigurator
            configData={configData}
            onGenerate={handleGenerateFake}
            onBack={() => setStep(4)}
          />
        )}

        {step === 6 && (
          <FakeTableViewer
            data={fakeData}
            onUpload={handleUploadFake}
            onRestart={() => setStep(3)}
          />
        )}
      </main>
    </>
  );
}
