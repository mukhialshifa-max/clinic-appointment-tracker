const formatter = new Intl.DateTimeFormat('en-CA', { dateStyle: 'medium', timeStyle: 'short' });

export default function AppointmentList({ appointments, onEdit, onStatus, onDelete }) {
  if (appointments.length === 0) {
    return <p className="muted">No appointments to show.</p>;
  }

  return (
    <div className="table-wrap">
      <table>
        <thead>
          <tr>
            <th>When</th>
            <th>Patient</th>
            <th>Provider</th>
            <th>Reason</th>
            <th>Status</th>
            <th aria-label="Actions" />
          </tr>
        </thead>
        <tbody>
          {appointments.map((a) => {
            const open = a.status === 'BOOKED' || a.status === 'CHECKED_IN';
            return (
              <tr key={a.id}>
                <td>{formatter.format(new Date(a.startTime))}</td>
                <td>{a.patientName}</td>
                <td>{a.providerName}</td>
                <td>{a.reason || '—'}</td>
                <td><span className={`badge ${a.status.toLowerCase()}`}>{a.status.replace('_', ' ')}</span></td>
                <td className="row-actions">
                  {a.status === 'BOOKED' && (
                    <button onClick={() => onStatus(a.id, 'CHECKED_IN')}>Check in</button>
                  )}
                  {a.status === 'CHECKED_IN' && (
                    <button onClick={() => onStatus(a.id, 'COMPLETED')}>Complete</button>
                  )}
                  {open && (
                    <>
                      <button className="secondary" onClick={() => onEdit(a)}>Edit</button>
                      <button className="secondary" onClick={() => onStatus(a.id, 'CANCELLED')}>Cancel</button>
                    </>
                  )}
                  <button
                    className="danger"
                    onClick={() => window.confirm('Delete this appointment?') && onDelete(a.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
