import { useState } from 'react';

const empty = { patientName: '', providerName: '', startTime: '', reason: '' };

// Used for both "book" and "edit". `initial` is the appointment being edited, or null.
export default function AppointmentForm({ initial, onSave, onCancel }) {
  const [form, setForm] = useState(
    initial
      ? {
          patientName: initial.patientName,
          providerName: initial.providerName,
          startTime: initial.startTime.slice(0, 16), // fits <input type="datetime-local">
          reason: initial.reason ?? '',
        }
      : empty
  );

  function change(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function submit(e) {
    e.preventDefault();
    const ok = await onSave({ ...form, reason: form.reason || null });
    if (ok && !initial) setForm(empty);
  }

  return (
    <form className="grid" onSubmit={submit}>
      <label>
        Patient name
        <input name="patientName" value={form.patientName} onChange={change} required maxLength={100} />
      </label>
      <label>
        Provider
        <input name="providerName" value={form.providerName} onChange={change} required maxLength={100} />
      </label>
      <label>
        Date and time
        <input type="datetime-local" name="startTime" value={form.startTime} onChange={change} required />
      </label>
      <label>
        Reason for visit
        <input name="reason" value={form.reason} onChange={change} maxLength={255} />
      </label>
      <div className="actions">
        <button type="submit">{initial ? 'Save changes' : 'Book appointment'}</button>
        {initial && <button type="button" className="secondary" onClick={onCancel}>Cancel</button>}
      </div>
    </form>
  );
}
