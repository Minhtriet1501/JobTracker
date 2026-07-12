<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api.js'

const profile = ref({ name: '', resumeText: '' })
const saved = ref(false)

onMounted(async () => {
  const res = await api.get('/users/me')
  profile.value.name = res.data.name ?? ''
  profile.value.resumeText = res.data.resumeText ?? ''
})

async function save() {
  await api.put('/users/me', profile.value)
  saved.value = true
  setTimeout(() => (saved.value = false), 2000)
}
</script>

<template>
  <div class="p-8 max-w-2xl">
    <h1 class="text-2xl font-bold mb-6">Profile</h1>
    <div class="bg-white p-6 rounded shadow">
      <label class="block text-sm text-gray-500 mb-1">Name</label>
      <input v-model="profile.name" class="w-full border p-2 rounded mb-4" />

      <label class="block text-sm text-gray-500 mb-1">Resume Text</label>
      <textarea
        v-model="profile.resumeText"
        rows="10"
        placeholder="Paste your resume here..."
        class="w-full border p-2 rounded mb-4"
      ></textarea>

      <button @click="save" class="bg-blue-500 text-white px-6 py-2 rounded">Save</button>
      <span v-if="saved" class="ml-3 text-green-500">Saved!</span>
    </div>
  </div>
</template>
