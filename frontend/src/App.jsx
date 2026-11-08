import { useCallback, useEffect, useState } from 'react';
import { api, clearCredentials, setCredentials } from './api.js';
import LoginForm from './components/LoginForm.jsx';
import AppointmentForm from './components/AppointmentForm.jsx';
import AppointmentList from './components/AppointmentList.jsx';

const STATUSES = ['BOOKED', 'CHECKED_IN', 'COMPLETED', 'CANCELLED'];

export default function App() {
  const [user, setUser] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [filter, setFilter] = useState('');
  const [editing, setEditing] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    try {
      setAppointments(await api.list(filter));
      setError('');
    } catch (e) {
      setError(e.message);
    }
  }, [filter]);

  useEffect(() => {
    if (user) load();
  }, [user, load]);

  async function handleLogin(username, password) {
    setCredentials(username, password);
    try {
      await api.list(); // confirms the credentials work
      setUser(username);
      setError('');
    } catch (e) {
      clearCredentials();
      throw e;
    }
  }

  function handleLogout() {
    clearCredentials();
    setUser(null);
    setAppointments([]);
  }

  // Runs an API call, then reloads the list; shows any error message.
  async function run(action) {
    try {
      await action();
      setError('');
      await load();
      return true;
    } catch (e) {
      setError(e.message);
      return false;
    }
  }

  async function handleSave(data) {
    const ok = await run(() => (editing ? api.update(editing.id, data) : api.create(data)));
    if (ok) setEditing(null);
    return ok;
  }

  if (!user) return <LoginForm onLogin={handleLogin} />;

  return (
    <div className="page">
      <header className="topbar">
        <h1>Clinic Appointment Tracker</h1>
        <div>
          <span className="muted">Signed in as {user}</span>
          <button className="link" onClick={handleLogout}>Sign out</button>
        </div>
      </header>

      {error && <p className="error" role="alert">{error}</p>}

      <section className="card">
        <h2>{editing ? 'Edit appointment' : 'Book an appointment'}</h2>
        <AppointmentForm
          key={editing?.id ?? 'new'}
          initial={editing}
          onSave={handleSave}
          onCancel={() => setEditing(null)}
        />
      </section>

      <section className="card">
        <div className="row">
          <h2>Appointments</h2>
          <label>
            Show{' '}
            <select value={filter} onChange={(e) => setFilter(e.target.value)}>
              <option value="">All</option>
              {STATUSES.map((s) => (
                <option key={s} value={s}>{s.replace('_', ' ').toLowerCase()}</option>
              ))}
            </select>
          </label>
        </div>
        <AppointmentList
          appointments={appointments}
          onEdit={setEditing}
          onStatus={(id, value) => run(() => api.setStatus(id, value))}
          onDelete={(id) => run(() => api.remove(id))}
        />
      </section>
    </div>
  );
}
