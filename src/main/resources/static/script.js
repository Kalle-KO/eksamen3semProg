const API = '/api';
let editingId = null; // Bruges ift at sættes efter om man redigerer eller opretter en sirene (hvis redigere, editingId = s.sirenId)

// — Sirener CRUD —

async function fetchSirens() {
    const res = await fetch(`${API}/sirens`);
    if (!res.ok) throw new Error(`Error getting: ${res.status}`);
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
    if (!res.ok) throw new Error('Could not delete');
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
          <td class="status-${s.status}" style="width: 10px">${s.status} </td>
          <td>${s.disabled ? 'Yes' : 'No'}</td>
          <td>
            <button class="edit">✏️</button>
            <button class="del">🗑️</button>
          </td>
        `;

        tr.querySelector('.edit').addEventListener('click', () => startEdit(s));
        tr.querySelector('.del').addEventListener('click', async () => {
            if (!confirm(`Delete siren #${s.sirenId}?`)) return;
            try {
                await deleteSiren(s.sirenId);
                await loadSirens();
            } catch (err) {
                alert("Could not delete the siren: " + err.message);
            }
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
        .textContent = 'Update #' + s.sirenId;
}

document.getElementById('cancelEdit').addEventListener('click', () => {
    editingId = null;
    document.getElementById('createSirenForm').reset();
    document.querySelector('#createSirenForm button[type=submit]')
        .textContent = 'Create';
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
                .textContent = 'Create';
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
    if (!res.ok) throw new Error(`Error collecting events: ${res.status}`);
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
        const time = e.timestamp ? new Date(e.timestamp + "Z").toLocaleString() : "Unknown time"; // Udskiftet for at rette Invalid date fejl. Var før: const time = new Date(e.timestamp).toLocaleString();
        const li = document.createElement('li');
        li.className = 'event-item';
        li.innerHTML = `
      <span>#${e.fireEventId} – (${e.latitude.toFixed(4)}, ${e.longitude.toFixed(4)}) @ ${time}</span>
      <button>Cancel</button>
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
        e.preventDefault(); // for at undgå side-refresh, og i stedet håndterer jeg data med JavaScript, som så kalder en POST eller PUT request til backend.
        const lat = parseFloat(document.getElementById('latInput').value);
        const lon = parseFloat(document.getElementById('lonInput').value);
        if (isNaN(lat) || isNaN(lon)) {
            return alert('Type valid coordinates');
        }
        try {
            const evt = await registerEvent(lat, lon);
            alert(`Fire #${evt.fireEventId} registered at (${evt.latitude}, ${evt.longitude})`);
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
