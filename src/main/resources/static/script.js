const API = '/api';
let editingId = null;

// --- API-kald ---
async function fetchSirens() {
    const res = await fetch(`${API}/sirens`);
    if (!res.ok) throw new Error(`Fejl ved hentning: ${res.status}`);
    return res.json();
}

async function createSiren(data) {
    const res = await fetch(`${API}/sirens`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!res.ok) {
        const err = await res.text();
        throw new Error(`Kunne ikke oprette sirene: ${err}`);
    }
    return res.json();
}

async function updateSiren(id, data) {
    const res = await fetch(`${API}/sirens/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!res.ok) {
        const err = await res.text();
        throw new Error(`Kunne ikke opdatere: ${err}`);
    }
    return res.json();
}

async function deleteSiren(id) {
    const res = await fetch(`${API}/sirens/${id}`, {
        method: 'DELETE'
    });
    if (!res.ok) {
        const err = await res.text();
        throw new Error(`Kunne ikke slette: ${err}`);
    }
}

// --- Rediger-knap handler ---
function startEdit(s) {
    editingId = s.sirenId;
    document.getElementById('newLat').value      = s.latitude;
    document.getElementById('newLon').value      = s.longitude;
    document.getElementById('newStatus').value   = s.status;
    document.getElementById('newDisabled').checked = s.disabled;
    document.querySelector('#createSirenForm button')
        .textContent = 'Opdater sirene #' + s.sirenId;
}

// --- Rendering af tabellen ---
function renderSirensTable(sirens) {
    const tbody = document.getElementById('sirensTable');
    tbody.innerHTML = '';
    sirens.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
      <td>${s.sirenId}</td>
      <td>${s.latitude.toFixed(4)}</td>
      <td>${s.longitude.toFixed(4)}</td>
      <td>${s.status}</td>
      <td>${s.disabled ? 'Ja' : 'Nej'}</td>
      <td>
        <button class="edit" data-id="${s.sirenId}">✏️</button>
        <button class="del"  data-id="${s.sirenId}">🗑️</button>
      </td>
    `;
        tr.querySelector('.edit').addEventListener('click', () => startEdit(s));
        tr.querySelector('.del').addEventListener('click', async () => {
            if (!confirm(`Slet sirene #${s.sirenId}?`)) return;
            try {
                await deleteSiren(s.sirenId);
                const updated = await fetchSirens();
                renderSirensTable(updated);
            } catch (err) {
                alert(err.message);
            }
        });
        tbody.appendChild(tr);
    });
}

// --- Form-submit (Opret / Opdater) ---
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

            // Reset form
            editingId = null;
            e.target.reset();
            document.querySelector('#createSirenForm button').textContent = 'Opret';

            // Opdater tabel
            const sirens = await fetchSirens();
            renderSirensTable(sirens);

        } catch (err) {
            alert(err.message);
            console.error(err);
        }
    });

// --- Initial indlæsning ---
window.addEventListener('DOMContentLoaded', async () => {
    try {
        const sirens = await fetchSirens();
        renderSirensTable(sirens);
    } catch (err) {
        alert('Kunne ikke hente sirener.');
        console.error(err);
    }
});
