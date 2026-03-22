const API_BASE = 'http://localhost:8080/api/users';

const form = document.getElementById('userForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
const formError = document.getElementById('formError');
const usersBody = document.getElementById('usersBody');
const tableMessage = document.getElementById('tableMessage');
const refreshBtn = document.getElementById('refreshBtn');

let isEditing = false;
let editingUserId = null;

document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
    setupEventListeners();
});

function setupEventListeners() {
    form.addEventListener('submit', handleSubmit);
    cancelBtn.addEventListener('click', cancelEdit);
    refreshBtn.addEventListener('click', loadUsers);
}

async function loadUsers() {
    try {
        showLoading(true);

        const response = await fetch(API_BASE);

        if (!response.ok) {
            throw new Error(`Ошибка: ${response.status}`);
        }

        const users = await response.json();
        renderUsers(users);

    } catch (error) {
        console.error('Ошибка загрузки пользователей:', error);
        showMessage('Не удалось загрузить пользователей');
    } finally {
        showLoading(false);
    }
}

function renderUsers(users) {
    if (users.length === 0) {
        usersBody.innerHTML = '';
        tableMessage.textContent = 'Пользователи не найдены';
        tableMessage.style.display = 'block';
        return;
    }

    tableMessage.style.display = 'none';

    usersBody.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${escapeHtml(user.name)}</td>
            <td>${escapeHtml(user.email)}</td>
            <td>${user.age}</td>
            <td>${formatDate(user.createdAt)}</td>
            <td class="actions-cell">
                <button class="btn-edit" onclick="editUser(${user.id})">✏️</button>
                <button class="btn-delete" onclick="deleteUser(${user.id})">🗑️</button>
            </td>
        </tr>
    `).join('');
}

function handleSubmit(e) {
    e.preventDefault();

    const userData = {
        name: document.getElementById('name').value.trim(),
        email: document.getElementById('email').value.trim(),
        age: parseInt(document.getElementById('age').value)
    };

    if (!userData.name || userData.name.length < 2) {
        showError('Имя должно содержать минимум 2 символа');
        return;
    }

    if (!userData.email || !isValidEmail(userData.email)) {
        showError('Введите корректный email');
        return;
    }

    if (userData.age < 0 || userData.age > 150) {
        showError('Возраст должен быть от 0 до 150');
        return;
    }

    if (isEditing && editingUserId) {
        updateUser(editingUserId, userData);
    } else {
        createUser(userData);
    }
}

async function createUser(userData) {
    try {
        disableForm(true);

        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || `Ошибка: ${response.status}`);
        }

        resetForm();
        loadUsers();
        showMessage('Пользователь успешно создан!');

    } catch (error) {
        console.error('Ошибка создания:', error);
        showError(error.message || 'Не удалось создать пользователя');
    } finally {
        disableForm(false);
    }
}

async function updateUser(id, userData) {
    try {
        disableForm(true);

        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || `Ошибка: ${response.status}`);
        }

        resetForm();
        loadUsers();
        showMessage('Пользователь успешно обновлён!');

    } catch (error) {
        console.error('Ошибка обновления:', error);
        showError(error.message || 'Не удалось обновить пользователя');
    } finally {
        disableForm(false);
    }
}

async function deleteUser(id) {
    if (!confirm('Вы уверены, что хотите удалить этого пользователя?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok && response.status !== 204) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.message || `Ошибка: ${response.status}`);
        }

        loadUsers();
        showMessage('Пользователь удалён');

    } catch (error) {
        console.error('Ошибка удаления:', error);
        showError(error.message || 'Не удалось удалить пользователя');
    }
}

window.editUser = async function(id) {
    try {
        const response = await fetch(`${API_BASE}/${id}`);

        if (!response.ok) {
            throw new Error('Пользователь не найден');
        }

        const user = await response.json();

        document.getElementById('userId').value = user.id;
        document.getElementById('name').value = user.name;
        document.getElementById('email').value = user.email;
        document.getElementById('age').value = user.age;

        isEditing = true;
        editingUserId = user.id;
        formTitle.textContent = 'Редактировать пользователя';
        submitBtn.textContent = 'Обновить';
        cancelBtn.style.display = 'inline-block';

        document.querySelector('.form-card').scrollIntoView({ behavior: 'smooth' });

    } catch (error) {
        console.error('Ошибка загрузки пользователя:', error);
        showError('Не удалось загрузить данные пользователя');
    }
};

function cancelEdit() {
    resetForm();
}

function resetForm() {
    form.reset();
    document.getElementById('userId').value = '';
    formTitle.textContent = '+ Создать пользователя';
    submitBtn.textContent = 'Создать';
    cancelBtn.style.display = 'none';
    isEditing = false;
    editingUserId = null;
    formError.classList.remove('show');
}

function disableForm(disabled) {
    form.querySelectorAll('input, button').forEach(el => {
        if (el.id !== 'cancelBtn') {
            el.disabled = disabled;
        }
    });
    submitBtn.textContent = disabled ? 'Загрузка...' : (isEditing ? 'Обновить' : 'Создать');
}

function showLoading(show) {
    if (show) {
        usersBody.innerHTML = '';
        tableMessage.textContent = 'Загрузка...';
        tableMessage.style.display = 'block';
    }
}

function showMessage(text) {
    const notification = document.createElement('div');
    notification.className = 'error-message show';
    notification.style.background = '#d4edda';
    notification.style.color = '#155724';
    notification.textContent = text;

    document.querySelector('.form-card').insertBefore(notification, form);

    setTimeout(() => notification.remove(), 3000);
}

function showError(text) {
    formError.textContent = text;
    formError.classList.add('show');

    setTimeout(() => {
        formError.classList.remove('show');
    }, 5000);
}

function isValidEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('ru-RU', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}