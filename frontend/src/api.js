// Small wrapper around fetch() for the Spring Boot REST API.
// Every request carries an HTTP Basic "Authorization" header.

let authHeader = null;

export function setCredentials(username, password) {
  authHeader = 'Basic ' + btoa(`${username}:${password}`);
}

export function clearCredentials() {
  authHeader = null;
}

async function request(path, options = {}) {
  const response = await fetch(`/api${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      Authorization: authHeader,
      ...options.headers,
    },
  });

  if (response.status === 204) return null;

  const body = await response.json().catch(() => null);
  if (!response.ok) {
    // The back end returns RFC 7807 problem details: { detail, errors? }
    const fieldErrors = body?.errors ? Object.values(body.errors).join('. ') : '';
    const message =
      response.status === 401 ? 'Wrong username or password'
      : response.status === 403 ? 'You do not have permission to do that'
      : fieldErrors || body?.detail || `Request failed (${response.status})`;
    throw new Error(message);
  }
  return body;
}

export const api = {
  list: (status) => request(status ? `/appointments?status=${status}` : '/appointments'),
  create: (data) => request('/appointments', { method: 'POST', body: JSON.stringify(data) }),
  update: (id, data) => request(`/appointments/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  setStatus: (id, value) => request(`/appointments/${id}/status?value=${value}`, { method: 'PATCH' }),
  remove: (id) => request(`/appointments/${id}`, { method: 'DELETE' }),
};
