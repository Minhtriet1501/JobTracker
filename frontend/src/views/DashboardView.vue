<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api.js'

const stats = ref(null)

onMounted(async () => {
  const res = await api.get('/applications/stats')
  stats.value = res.data
})
</script>

<template>
  <div class="p-8">
    <h1 class="text-2xl font-bold mb-6">Dashboard</h1>
    <div v-if="stats" class="grid grid-cols-4 gap-4">
      <div class="bg-white rounded shadow p-4 text-center">
        <p class="text-gray-500">Applied</p>
        <p class="text-3xl font-bold">{{ stats.byStatus.APPLIED ?? 0 }}</p>
      </div>
      <div class="bg-white rounded shadow p-4 text-center">
        <p class="text-gray-500">Interviewing</p>
        <p class="text-3xl font-bold">{{ stats.byStatus.INTERVIEWING ?? 0 }}</p>
      </div>
      <div class="bg-white rounded shadow p-4 text-center">
        <p class="text-gray-500">Offer</p>
        <p class="text-3xl font-bold">{{ stats.byStatus.OFFER ?? 0 }}</p>
      </div>
      <div class="bg-white rounded shadow p-4 text-center">
        <p class="text-gray-500">Rejected</p>
        <p class="text-3xl font-bold">{{ stats.byStatus.REJECTED ?? 0 }}</p>
      </div>
    </div>
    <p v-else>Loading...</p>
  </div>
</template>
