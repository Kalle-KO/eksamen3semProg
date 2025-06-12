// Eksempel på fetch + async/await + errorhandling + DOM-manipulation
document.getElementById('loadBtn').addEventListener('click', async () => {
    const output = document.getElementById('output');
    output.textContent = 'Henter data…';

    try {
        const response = await fetch('https://jsonplaceholder.typicode.com/posts/1');
        if (!response.ok) {
            throw new Error(`Netværksfejl: ${response.status}`);
        }
        const data = await response.json();
        // Eksempel på functional programming (map/filter)
        const result = Object.entries(data)
            .map(([key, value]) => `${key}: ${value}`)
            .join('\n');
        output.textContent = result;
    } catch (err) {
        output.textContent = `Fejl: ${err.message}`;
    }
});
