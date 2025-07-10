import React, { useState, useEffect } from 'react';
import './ColumnConfigurator.css';

const BASE_TYPES = [
  'CUSTOMER_NAME','PHONE_NUMBER','EMAIL','BALANCE',
  'ACCOUNT_NUMBER','IBAN','IS_CORPORATE_CUSTOMER',
  'CUSTOMER_NUMBER','NATIONAL_ID','ADDRESS'
];

export default function ColumnConfigurator({
  configData,   // from /configure: [{ columnName,dataType,suggestion },…]
  onGenerate,
  onBack
}) {
  const [rows, setRows] = useState([]);

  // seed on mount
  useEffect(() => {
    // extend each row with our custom fields placeholders
    setRows(configData.map(r => ({
      ...r,
      // text
      textPrefix: '',
      textSuffix: '',
      textLength: '',
      // number
      numStart: '',
      numEnd: '',
      numLength: ''
    })));
  }, [configData]);

  const update = (idx, field, val) => {
    const all = [...rows];
    all[idx][field] = val;
    setRows(all);
  };

  return (
    <div className="selector column-configurator">
      <h3>Configure Columns</h3>
      <button onClick={onBack}>Back</button>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Column Name</th>
              <th>SQL Type</th>
              <th>Suggestion</th>
              <th>Custom Params</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((r, i) => (
              <tr key={r.columnName}>
                <td>{r.columnName}</td>
                <td>{r.dataType}</td>
                <td>
                  <select
                    value={r.suggestion}
                    onChange={e => update(i, 'suggestion', e.target.value)}
                  >
                    {BASE_TYPES.map(t => (
                      <option key={t} value={t}>
                        {t.replace(/_/g,' ')}
                      </option>
                    ))}
                    <option value="CUSTOM_TEXT">➕ Custom Text…</option>
                    <option value="CUSTOM_NUMBER">➕ Custom Number…</option>
                  </select>
                </td>
                <td>
                  {r.suggestion === 'CUSTOM_TEXT' && (
                    <div className="custom-params">
                      <label>
                        Text starts with
                        <input
                          placeholder="prefix"
                          value={r.textPrefix}
                          onChange={e => update(i, 'textPrefix', e.target.value)}
                        />
                      </label>
                      <label>
                        Text ends with
                        <input
                          placeholder="suffix"
                          value={r.textSuffix}
                          onChange={e => update(i, 'textSuffix', e.target.value)}
                        />
                      </label>
                      <label>
                        Text length
                        <input
                          type="number"
                          placeholder="chars"
                          value={r.textLength}
                          onChange={e => update(i, 'textLength', e.target.value)}
                        />
                      </label>
                    </div>
                  )}
                  {r.suggestion === 'CUSTOM_NUMBER' && (
                    <div className="custom-params">
                      <label>
                        Number start
                        <input
                          type="number"
                          placeholder="min"
                          value={r.numStart}
                          onChange={e => update(i, 'numStart', e.target.value)}
                        />
                      </label>
                      <label>
                        Number end
                        <input
                          type="number"
                          placeholder="max"
                          value={r.numEnd}
                          onChange={e => update(i, 'numEnd', e.target.value)}
                        />
                      </label>
                      <label>
                        Digit length
                        <input
                          type="number"
                          placeholder="digits"
                          value={r.numLength}
                          onChange={e => update(i, 'numLength', e.target.value)}
                        />
                      </label>
                    </div>
                  )}
                  {!(r.suggestion.startsWith('CUSTOM_')) && (
                    <em className="no-custom">–</em>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <button className="generate-btn" onClick={() => onGenerate(rows)}>
        Generate Fake Data
      </button>
    </div>
  );
}
