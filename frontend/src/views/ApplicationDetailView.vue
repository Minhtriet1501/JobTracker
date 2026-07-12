<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '../services/api.js'

const route = useRoute()
const id = route.params.id

const application = ref(null)
const interviews = ref([])
const analysis = ref(null)

const showInterviewForm = ref(false)
const editingInterviewId = ref(null)
const interviewForm = ref({ round: 1, type: 'PHONE', notes: '', scheduleAt: '', outcome: '' })

const jobDescription = ref('')
const analyzing = ref(false)

const errorMsg = ref('')

async function load() {
  const [appRes, intRes] = await Promise.all([
    api.get(`/applications/${id}`),
    api.get(`/applications/${id}/interviews`),
  ])
  application.value = appRes.data
  interviews.value = intRes.data

  try {
    const aiRes = await api.get(`/applications/${id}/analyze`)
    analysis.value = aiRes.data
  } catch (e) {
    // no analysis yet
  }
}

function startEditInterview(i) {
  interviewForm.value = {
    round: i.round,
    type: i.type,
    notes: i.notes ?? '',
    scheduleAt: i.scheduleAt ? i.scheduleAt.slice(0, 16) : '',
    outcome: i.outcome ?? '',
  }
  editingInterviewId.value = i.id
  showInterviewForm.value = true
}

async function saveInterview() {
  const payload = {
    round: interviewForm.value.round,
    type: interviewForm.value.type,
    notes: interviewForm.value.notes,
    scheduleAt: interviewForm.value.scheduleAt || null,
    outcome: interviewForm.value.outcome || null,
  }
  if (editingInterviewId.value) {
    await api.put(`/applications/${id}/interviews/${editingInterviewId.value}`, payload)
  } else {
    await api.post(`/applications/${id}/interviews`, payload)
  }
  showInterviewForm.value = false
  editingInterviewId.value = null
  interviewForm.value = { round: 1, type: 'PHONE', notes: '', scheduleAt: '', outcome: '' }
  load()
}

async function deleteInterview(interviewId) {
  await api.delete(`/applications/${id}/interviews/${interviewId}`)
  load()
}

async function analyze() {
  analyzing.value = true
  try {
    const res = await api.post(`/applications/${id}/analyze`, {
      jobDescription: jobDescription.value,
    })
    analysis.value = res.data
  } catch (e) {
    errorMsg.value = e.response?.data?.error || 'Analysis failed. Please try again.'
  } finally {
    analyzing.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="p-8" v-if="application">
    <h1 class="text-2xl font-bold mb-1">{{ application.companyName }}</h1>
    <p class="text-gray-500 mb-6">{{ application.position }} · {{ application.status }}</p>

    <!-- Interviews -->
    <div class="mb-8">
      <div class="flex justify-between items-center mb-3">
        <h2 class="text-xl font-semibold">Interviews</h2>
        <button
          @click="
            () => {
              showInterviewForm = !showInterviewForm
              editingInterviewId = null
              interviewForm = { round: 1, type: 'PHONE', notes: '', scheduleAt: '', outcome: '' }
            }
          "
          class="bg-blue-500 text-white px-3 py-1 rounded"
        >
          + Add
        </button>
      </div>

      <div v-if="showInterviewForm" class="bg-white p-4 rounded shadow mb-3 flex gap-2 flex-wrap">
        <input
          v-model.number="interviewForm.round"
          type="number"
          placeholder="Round"
          class="border p-2 rounded w-20"
        />
        <select v-model="interviewForm.type" class="border p-2 rounded">
          <option>PHONE</option>
          <option>VIDEO</option>
          <option>ONSITE</option>
          <option>TECHNICAL</option>
          <option>HR</option>
        </select>
        <input
          v-model="interviewForm.scheduleAt"
          type="datetime-local"
          class="border p-2 rounded"
        />
        <input
          v-model="interviewForm.notes"
          placeholder="Notes"
          class="border p-2 rounded flex-1"
        />
        <select v-model="interviewForm.outcome" class="border p-2 rounded">
          <option value="">No outcome</option>
          <option>PASSED</option>
          <option>FAILED</option>
          <option>PENDING</option>
          <option>CANCELLED</option>
        </select>
        <button @click="saveInterview" class="bg-green-500 text-white px-4 py-2 rounded">
          {{ editingInterviewId ? 'Update' : 'Save' }}
        </button>
      </div>

      <table class="w-full bg-white rounded shadow">
        <thead class="bg-gray-100">
          <tr>
            <th class="p-3 text-left">Round</th>
            <th class="p-3 text-left">Type</th>
            <th class="p-3 text-left">Scheduled At</th>
            <th class="p-3 text-left">Outcome</th>
            <th class="p-3 text-left">Notes</th>
            <th class="p-3 text-left">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="i in interviews" :key="i.id" class="border-t">
            <td class="p-3">{{ i.round }}</td>
            <td class="p-3">{{ i.type }}</td>
            <td class="p-3">{{ i.scheduleAt ?? '-' }}</td>
            <td class="p-3">{{ i.outcome ?? '-' }}</td>
            <td class="p-3">{{ i.notes }}</td>
            <td class="p-3 flex gap-2">
              <button @click="startEditInterview(i)" class="text-yellow-500">Edit</button>
              <button @click="deleteInterview(i.id)" class="text-red-500">Delete</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- AI Analysis -->
    <div>
      <h2 class="text-xl font-semibold mb-3">AI Analysis</h2>
      <textarea
        v-model="jobDescription"
        placeholder="Paste job description here..."
        class="w-full border p-2 rounded mb-2 h-32"
      ></textarea>
      <button
        @click="analyze"
        :disabled="analyzing"
        class="bg-purple-500 text-white px-4 py-2 rounded mb-4"
      >
        {{ analyzing ? 'Analyzing...' : 'Analyze with AI' }}
      </button>
      <p v-if="errorMsg" class="text-red-500 mt-2">{{ errorMsg }}</p>

      <div v-if="analysis" class="bg-white p-4 rounded shadow">
        <p class="text-lg font-bold mb-2">Match Score: {{ analysis.matchScore }}/100</p>
        <p class="mb-2"><span class="font-semibold">Summary:</span> {{ analysis.summary }}</p>
        <p class="mb-2"><span class="font-semibold">Suggestion:</span> {{ analysis.suggestion }}</p>
        <p><span class="font-semibold">Interview Questions:</span> {{ analysis.interviewQs }}</p>
      </div>
    </div>
  </div>
  <div v-else class="p-8">Loading...</div>
</template>
