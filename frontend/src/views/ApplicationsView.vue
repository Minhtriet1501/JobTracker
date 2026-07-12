<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api.js'

const applications = ref([])
const router = useRouter()

const emptyForm = () => ({
  companyName: '',
  position: '',
  status: 'APPLIED',
  notes: '',
  jobUrl: '',
  appliedDate: '',
  deadLine: '',
  salaryRange: '',
})

const form = ref(emptyForm())
const editingId = ref(null)
const showForm = ref(false)
const filterStatus = ref('')
const filterCompany = ref('')

async function load() {
  const res = await api.get('/applications', {
    params: {
      status: filterStatus.value || undefined,
      companyName: filterCompany.value || undefined,
    },
  })
  applications.value = res.data
}

async function save() {
  const payload = {
    ...form.value,
    appliedDate: form.value.appliedDate || null,
    deadLine: form.value.deadLine || null,
  }
  if (editingId.value) {
    await api.put(`/applications/${editingId.value}`, payload)
  } else {
    await api.post('/applications', payload)
  }
  form.value = emptyForm()
  editingId.value = null
  showForm.value = false
  load()
}

function startEdit(app) {
  form.value = {
    companyName: app.companyName,
    position: app.position,
    status: app.status,
    notes: app.notes ?? '',
    jobUrl: app.jobUrl ?? '',
    appliedDate: app.appliedDate ?? '',
    deadLine: app.deadLine ?? '',
    salaryRange: app.salaryRange ?? '',
  }
  editingId.value = app.id
  showForm.value = true
}

async function remove(id) {
  await api.delete(`/applications/${id}`)
  load()
}

onMounted(load)
</script>

<template>
  <div class="p-8">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold">Applications</h1>
      <button
        @click="
          () => {
            form = emptyForm()
            editingId = null
            showForm = !showForm
          }
        "
        class="bg-blue-500 text-white px-4 py-2 rounded"
      >
        + Add
      </button>
    </div>

    <div class="flex gap-3 mb-4">
      <input v-model="filterCompany" placeholder="Filter by company" class="border p-2 rounded" />
      <select v-model="filterStatus" class="border p-2 rounded">
        <option value="">All Status</option>
        <option>WISHLIST</option>
        <option>APPLIED</option>
        <option>INTERVIEWING</option>
        <option>OFFER</option>
        <option>ACCEPTED</option>
        <option>REJECTED</option>
        <option>GHOSTED</option>
      </select>
      <button @click="load" class="bg-gray-200 px-4 py-2 rounded">Search</button>
    </div>

    <div v-if="showForm" class="bg-white p-4 rounded shadow mb-4 grid grid-cols-2 gap-3">
      <input v-model="form.companyName" placeholder="Company *" class="border p-2 rounded" />
      <input v-model="form.position" placeholder="Position *" class="border p-2 rounded" />
      <select v-model="form.status" class="border p-2 rounded">
        <option>WISHLIST</option>
        <option>APPLIED</option>
        <option>INTERVIEWING</option>
        <option>OFFER</option>
        <option>ACCEPTED</option>
        <option>REJECTED</option>
        <option>GHOSTED</option>
      </select>
      <input v-model="form.salaryRange" placeholder="Salary Range" class="border p-2 rounded" />
      <input v-model="form.jobUrl" placeholder="Job URL" class="border p-2 rounded" />
      <input v-model="form.notes" placeholder="Notes" class="border p-2 rounded" />
      <div>
        <label class="text-sm text-gray-500">Applied Date</label>
        <input v-model="form.appliedDate" type="date" class="border p-2 rounded w-full" />
      </div>
      <div>
        <label class="text-sm text-gray-500">Deadline</label>
        <input v-model="form.deadLine" type="date" class="border p-2 rounded w-full" />
      </div>
      <div class="col-span-2 flex justify-end gap-2">
        <button @click="showForm = false" class="bg-gray-200 px-4 py-2 rounded">Cancel</button>
        <button @click="save" class="bg-green-500 text-white px-4 py-2 rounded">
          {{ editingId ? 'Update' : 'Save' }}
        </button>
      </div>
    </div>

    <table class="w-full bg-white rounded shadow">
      <thead class="bg-gray-100">
        <tr>
          <th class="p-3 text-left">Company</th>
          <th class="p-3 text-left">Position</th>
          <th class="p-3 text-left">Status</th>
          <th class="p-3 text-left">Deadline</th>
          <th class="p-3 text-left">Salary</th>
          <th class="p-3 text-left">Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="app in applications" :key="app.id" class="border-t">
          <td class="p-3">{{ app.companyName }}</td>
          <td class="p-3">{{ app.position }}</td>
          <td class="p-3">{{ app.status }}</td>
          <td class="p-3">{{ app.deadLine ?? '-' }}</td>
          <td class="p-3">{{ app.salaryRange ?? '-' }}</td>
          <td class="p-3 flex gap-2">
            <button @click="router.push(`/applications/${app.id}`)" class="text-blue-500">
              Detail
            </button>
            <button @click="startEdit(app)" class="text-yellow-500">Edit</button>
            <button @click="remove(app.id)" class="text-red-500">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
