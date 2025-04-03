const API_BASE = '/api/v1/chat';

document.getElementById('scan-button').addEventListener('click', () => {
    const scanButton = document.getElementById('scan-button');
    scanButton.disabled = true;
    scanButton.textContent = '⏳ Сканирование...';

    showResults('<div class="loading">Сканирование страницы... ⏳</div>');

    fetch(`${API_BASE}/scan`, { method: 'POST' })
        .then(res => res.json())
        .then(data => {
            const formatted = formatJson(data);
            showResults(`<pre class="pretty-json">${formatted}</pre>`);
        })
        .catch(err => {
            showResults(`<pre class="error">Ошибка при сканировании:\n${err}</pre>`);
        })
        .finally(() => {
            scanButton.disabled = false;
            scanButton.textContent = '🔍 Сканировать страницу';
        });
});

document.getElementById('all-button').addEventListener('click', () => {
    fetch(`${API_BASE}/all`, { method: 'GET' })
        .then(res => res.json())
        .then(data => {
            const messages = data.data?.messages || [];

            if (messages.length === 0) {
                showResults('<p>Ничего не найдено.</p>');
            } else {
                const html = messages
                    .map(m => `
            <div class="qa-block">
              <div class="question"> ${escapeHtml(m.question)}</div>
              <button class="toggle-btn" onclick="toggleAnswer(this)">Свернуть</button>
              <div class="gpt-answer-box">${m.answerHtml}</div>
            </div>
          `)
                    .join('');
                showResults(html);
            }
        })
        .catch(err => {
            showResults(`<pre>Ошибка при поиске:\n${err}</pre>`);
        });
});

function formatJson(obj) {
    return JSON.stringify(obj, null, 2)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

function search(type) {
    const inputId = type === 'question' ? 'question-search' : 'answer-search';
    const query = document.getElementById(inputId).value;

    if (!query.trim()) {
        showResults('<p>Введите запрос для поиска.</p>');
        return;
    }

    fetch(`${API_BASE}/search/${type}?query=${encodeURIComponent(query)}&limit=10`, {
        method: 'POST'
    })
        .then(res => res.json())
        .then(data => {
            const messages = data.data?.messages || [];

            if (messages.length === 0) {
                showResults('<p>Ничего не найдено.</p>');
            } else {
                const html = messages
                    .map(m => `
            <div class="qa-block">
              <div class="question"><strong>Вопрос:</strong> ${escapeHtml(m.question)}</div>
              <button class="toggle-btn" onclick="toggleAnswer(this)">Свернуть</button>
              <div class="gpt-answer-box">${m.answerHtml}</div>
            </div>
          `)
                    .join('');
                showResults(html);
            }
        })
        .catch(err => {
            showResults(`<pre>Ошибка при поиске:\n${err}</pre>`);
        });
}

function showResults(html) {
    const resultsBlock = document.getElementById('results-content');
    resultsBlock.innerHTML = html;

    if (typeof hljs !== 'undefined') {
        hljs.highlightAll();
    }

    addCopyButtonsToCodeBlocks();
}

function toggleAnswer(button) {
    const answerBox = button.nextElementSibling;
    const isHidden = answerBox.classList.toggle('hidden');
    button.textContent = isHidden ? 'Развернуть' : 'Свернуть';
}

function addCopyButtonsToCodeBlocks() {
    document.querySelectorAll('pre code').forEach((block) => {
        const wrapper = block.parentElement;
        wrapper.style.position = 'relative';

        const copyBtn = document.createElement('button');
        copyBtn.textContent = '📋';
        copyBtn.className = 'copy-btn';

        copyBtn.addEventListener('click', () => {
            navigator.clipboard.writeText(block.textContent);
            copyBtn.textContent = '✅';
            setTimeout(() => (copyBtn.textContent = '📋'), 2000);
        });

        wrapper.appendChild(copyBtn);
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
