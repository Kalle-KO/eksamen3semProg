const API = '/api';
let editingId = null;

// — Sirener CRUD —

async function fetchSirens() {
    const res = await fetch(`${API}/sirens`);
    if (!res.ok) throw new Error(`Fejl ved hentning: ${res.status}`);
    return res.json();
}

async function createSiren(data) {
    const res = await fetch(`${API}/sirens`, {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
}

async function updateSiren(id, data) {
    const res = await fetch(`${API}/sirens/${id}`, {
        method: 'PUT',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
}

async function deleteSiren(id) {
    const res = await fetch(`${API}/sirens/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Kunne ikke slette');
}

function renderSirensTable(list) {
    const tbody = document.getElementById('sirensTable');
    tbody.innerHTML = '';
    list.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
      <td>${s.sirenId}</td>
      <td>${s.latitude.toFixed(4)}</td>
      <td>${s.longitude.toFixed(4)}</td>
      <td class="status-${s.status}">${s.status}</td>
      <td>${s.disabled ? 'Ja' : 'Nej'}</td>
      <td>
        <button class="edit">✏️</button>
        <button class="del">🗑️</button>
      </td>
    `;
        tr.querySelector('.edit').addEventListener('click', () => startEdit(s));
        tr.querySelector('.del').addEventListener('click', async () => {
            if (!confirm(`Slet sirene #${s.sirenId}?`)) return;
            await deleteSiren(s.sirenId);
            await loadSirens();
        });
        tbody.appendChild(tr);
    });
}

function startEdit(s) {
    editingId = s.sirenId;
    document.getElementById('newLat').value      = s.latitude;
    document.getElementById('newLon').value      = s.longitude;
    document.getElementById('newStatus').value   = s.status;
    document.getElementById('newDisabled').checked = s.disabled;
    document.querySelector('#createSirenForm button[type=submit]')
        .textContent = 'Opdater #' + s.sirenId;
}

document.getElementById('cancelEdit').addEventListener('click', () => {
    editingId = null;
    document.getElementById('createSirenForm').reset();
    document.querySelector('#createSirenForm button[type=submit]')
        .textContent = 'Opret';
});

document.getElementById('createSirenForm')
    .addEventListener('submit', async e => {
        e.preventDefault();
        const data = {
            latitude:  parseFloat(document.getElementById('newLat').value),
            longitude: parseFloat(document.getElementById('newLon').value),
            status:    document.getElementById('newStatus').value,
            disabled:  document.getElementById('newDisabled').checked
        };
        try {
            if (editingId) {
                await updateSiren(editingId, data);
            } else {
                await createSiren(data);
            }
            editingId = null;
            e.target.reset();
            document.querySelector('#createSirenForm button[type=submit]')
                .textContent = 'Opret';
            await loadSirens();
        } catch (err) {
            alert(err.message);
        }
    });

async function loadSirens() {
    const list = await fetchSirens();
    renderSirensTable(list);
}

// — Brand-events —

async function fetchEvents() {
    const res = await fetch(`${API}/fire-events`);
    if (!res.ok) throw new Error(`Fejl ved hentning af events: ${res.status}`);
    return res.json();
}

async function registerEvent(lat, lon) {
    const res = await fetch(
        `${API}/fire-events/register?latitude=${lat}&longitude=${lon}`,
        { method: 'POST' }
    );
    if (!res.ok) throw new Error(await res.text());
    return res.json();
}

async function closeEvent(id) {
    const res = await fetch(`${API}/fire-events/${id}/close`, {
        method: 'POST'
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
}

function renderEvents(list) {
    const ul = document.getElementById('eventsList');
    ul.innerHTML = '';
    list.filter(e => !e.closed).forEach(e => {
        const time = new Date(e.timestamp).toLocaleString();
        const li = document.createElement('li');
        li.className = 'event-item';
        li.innerHTML = `
      <span>#${e.fireEventId} – (${e.latitude.toFixed(4)}, ${e.longitude.toFixed(4)}) @ ${time}</span>
      <button>Afmeld</button>
    `;
        li.querySelector('button').addEventListener('click', async () => {
            await closeEvent(e.fireEventId);
            // Genindlæs både events og sirener
            await Promise.all([loadEvents(), loadSirens()]);
        });
        ul.appendChild(li);
    });
}

async function loadEvents() {
    const evts = await fetchEvents();
    renderEvents(evts);
}

document.getElementById('registerEventForm')
    .addEventListener('submit', async e => {
        e.preventDefault();
        const lat = parseFloat(document.getElementById('latInput').value);
        const lon = parseFloat(document.getElementById('lonInput').value);
        if (isNaN(lat) || isNaN(lon)) {
            return alert('Indtast gyldige koordinater');
        }
        try {
            const evt = await registerEvent(lat, lon);
            alert(`Brand #${evt.fireEventId} registreret ved (${evt.latitude}, ${evt.longitude})`);
            e.target.reset();
            // Genindlæs både events og sirener
            await Promise.all([loadEvents(), loadSirens()]);
        } catch (err) {
            alert(err.message);
        }
    });

// — Init —
window.addEventListener('DOMContentLoaded', async () => {
    await Promise.all([loadSirens(), loadEvents()]);
});
