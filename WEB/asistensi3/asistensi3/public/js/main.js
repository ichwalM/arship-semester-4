
let taskModalInstance;
let currentCategory = 'Semua'; 


const taskForm = document.getElementById('taskForm');
const taskIdInput = document.getElementById('taskId'); 
const taskTitle = document.getElementById('taskTitle'); 
const taskCategory = document.getElementById('taskCategory'); 
const taskDate = document.getElementById('taskDate'); 
const taskStatus = document.getElementById('taskStatus'); 
const taskTableBody = document.getElementById('taskTableBody');
const searchInput = document.getElementById('searchInput'); 
const saveTaskBtn = document.getElementById('saveTaskBtn'); 

const categoryLinks = {
    semua: document.getElementById('category-semua'),
    kerja: document.getElementById('category-kerja'),
    pribadi: document.getElementById('category-pribadi'),
    sekolah: document.getElementById('category-sekolah'),
};

const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

document.addEventListener('DOMContentLoaded', () => {
    taskModalInstance = new bootstrap.Modal(document.getElementById('taskModal')); 
    
    currentCategory = typeof currentGlobalCategory !== 'undefined' ? currentGlobalCategory : 'Semua';
    setActiveCategory(currentCategory);
    loadTasks();
    setupEventListeners();
});

function setupEventListeners() {
    saveTaskBtn.addEventListener('click', saveTask); 

    searchInput.addEventListener('input', () => { 
        filterTasks(currentCategory);
    });

    categoryLinks.semua.addEventListener('click', (e) => { 
        e.preventDefault();
        setActiveCategory('Semua');
        filterTasks('Semua');
    });
    categoryLinks.kerja.addEventListener('click', (e) => { 
        e.preventDefault();
        setActiveCategory('Kerja');
        filterTasks('Kerja');
    });
    categoryLinks.pribadi.addEventListener('click', (e) => { 
        e.preventDefault();
        setActiveCategory('Pribadi');
        filterTasks('Pribadi');
    });
    categoryLinks.sekolah.addEventListener('click', (e) => { 
        e.preventDefault();
        setActiveCategory('Sekolah');
        filterTasks('Sekolah');
    });

    
    document.getElementById('taskModal').addEventListener('show.bs.modal', function (event) { 
        
        if (event.relatedTarget && !event.relatedTarget.classList.contains('btn-edit')) {
            resetForm();
            document.getElementById('taskModalLabel').textContent = 'Tambah Tugas';
        }
    });
}

function setActiveCategory(categoryName) {
    currentCategory = categoryName; 
    for (const key in categoryLinks) {
        categoryLinks[key].classList.remove('active');
    }

    switch(categoryName) {
        case 'Semua':
            categoryLinks.semua.classList.add('active'); 
            break;
        case 'Kerja':
            categoryLinks.kerja.classList.add('active');
            break;
        case 'Pribadi':
            categoryLinks.pribadi.classList.add('active');
            break;
        case 'Sekolah':
            categoryLinks.sekolah.classList.add('active'); 
            break;
    }
}


async function loadTasks() {
    filterTasks(currentCategory); 
}

async function filterTasks(category) {
    const searchTerm = searchInput.value.toLowerCase();
    const url = `/tasks?category=${encodeURIComponent(category)}&search=${encodeURIComponent(searchTerm)}`;

    try {
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest', 
                'Accept': 'application/json',
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        renderTasks(data.tasks);
    } catch (error) {
        console.error('Error fetching tasks:', error);
        taskTableBody.innerHTML = '<tr><td colspan="5">Gagal memuat tugas. Coba lagi nanti.</td></tr>';
    }
}

function renderTasks(tasks) {
    taskTableBody.innerHTML = ''; 
    if (!tasks || tasks.length === 0) { 
        taskTableBody.innerHTML = '<tr><td colspan="5">Tidak ada tugas ditemukan.</td></tr>'; 
        return; 
    }

    tasks.forEach((task) => {
        const row = document.createElement('tr');
        if (task.status === 'Selesai') { 
            row.classList.add('task-completed');
        }

        const statusButtonClass = task.status === 'Selesai' ? 'badge bg-success text-light' : 'badge bg-warning text-light';

        
        const displayDate = task.date ? formatDate(task.date.split('T')[0]) : 'N/A';


        row.innerHTML = `
            <td>${task.title}</td>
            <td>${task.category}</td>
            <td>${displayDate}</td>
            <td><span class="${statusButtonClass} enabled p-2 text-dark">${task.status}</span></td>
            <td>
                <button class="btn btn-sm btn-primary btn-edit me-1 fw-bold" onclick="editTask(${task.id})">Edit</button>
                <button class="btn btn-sm btn-danger fw-bold" onclick="confirmDelete(${task.id})">Hapus</button> </td>
        `;
        taskTableBody.appendChild(row);
    });
}

function formatDate(dateString) {
    if (!dateString || dateString.length < 10) return 'Invalid Date';
    
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); 
    const day = date.getDate().toString().padStart(2, '0'); 
    return `${year}-${month}-${day}`; 
}

async function saveTask() {
    if (!taskTitle.value || !taskCategory.value || !taskDate.value || !taskStatus.value) {
        alert('Semua form harus diisi!'); 
        return; 
    }

    const taskData = {
        title: taskTitle.value,
        category: taskCategory.value,
        date: taskDate.value,
        status: taskStatus.value,
    };

    const currentTaskId = taskIdInput.value;
    let method = 'POST';
    let url = '/tasks';

    if (currentTaskId) { 
        method = 'PUT';
        url = `/tasks/${currentTaskId}`;
    }

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken,
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'application/json',
            },
            body: JSON.stringify(taskData)
        });

        const result = await response.json();

        if (!response.ok) {
            if (response.status === 422 && result.errors) { 
                let errorMessages = Object.values(result.errors).map(err => err.join(', ')).join('\n');
                alert('Gagal menyimpan:\n' + errorMessages);
            } else {
                throw new Error(result.message || `HTTP error! status: ${response.status}`);
            }
            return;
        }

        alert(result.message || 'Tugas berhasil disimpan!');
        filterTasks(currentCategory); 
        resetForm();
        taskModalInstance.hide();

    } catch (error) {
        console.error('Error saving task:', error);
        alert('Terjadi kesalahan saat menyimpan tugas: ' + error.message);
    }
}

async function editTask(id) { 
    try {
        const response = await fetch(`/tasks/${id}`, { 
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'application/json',
            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const task = await response.json(); 

        if (task) {
            taskIdInput.value = task.id;
            taskTitle.value = task.title; 
            taskCategory.value = task.category; 
            taskDate.value = task.date.split('T')[0]; 
            taskStatus.value = task.status; 

            document.getElementById('taskModalLabel').textContent = 'Edit Tugas';
            taskModalInstance.show(); 
        }
    } catch (error) {
        console.error('Error fetching task for edit:', error);
        alert('Gagal memuat data tugas untuk diedit.');
    }
}

function confirmDelete(id) { 
    if (confirm('Yakin ingin menghapus tugas ini?')) {
        deleteTask(id);
    }
}

async function deleteTask(id) { 
    try {
        const response = await fetch(`/tasks/${id}`, {
            method: 'DELETE',
            headers: {
                'X-CSRF-TOKEN': csrfToken,
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'application/json',
            }
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.message || `HTTP error! status: ${response.status}`);
        }

        alert(result.message || 'Tugas berhasil dihapus!');
        filterTasks(currentCategory); 

    } catch (error) {
        console.error('Error deleting task:', error);
        alert('Gagal menghapus tugas: ' + error.message);
    }
}

function resetForm() {
    taskIdInput.value = ''; 
    taskForm.reset(); 
    document.getElementById('taskModalLabel').textContent = 'Tambah Tugas'; 
}


window.editTask = editTask; 
window.confirmDelete = confirmDelete; 

