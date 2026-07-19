<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import api from '../services/api.js'

const notifications = ref([])
const showPanel = ref(false)
const loading = ref(false)

const unreadCount = computed(() => notifications.value.filter((n) => !n.isRead).length)

async function load() {
  loading.value = true
  try {
    const res = await api.get('/notifications')
    notifications.value = res.data
  } finally {
    loading.value = false
  }
}

function togglePanel() {
  showPanel.value = !showPanel.value
}

async function markRead(notification) {
  if (notification.isRead) return
  notification.isRead = true
  try {
    await api.put(`/notifications/${notification.id}/read`)
  } catch (e) {
    notification.isRead = false
  }
}

async function markAllRead() {
  const previous = notifications.value.map((n) => n.isRead)
  notifications.value.forEach((n) => (n.isRead = true))
  try {
    await api.put('/notifications/read-all')
  } catch (e) {
    notifications.value.forEach((n, i) => (n.isRead = previous[i]))
  }
}

function closeOnOutsideClick(event) {
  if (!event.target.closest('.notification-bell')) {
    showPanel.value = false
  }
}

const TYPE_LABELS = {
  DEADLINE_REMINDER: 'Deadline Reminder',
  INTERVIEW_REMINDER: 'Interview Reminder',
}

function typeLabel(type) {
  return TYPE_LABELS[type] ?? type
}

let pollTimer = null

onMounted(() => {
  load()
  document.addEventListener('click', closeOnOutsideClick)
  pollTimer = setInterval(load, 60000)
})

onUnmounted(() => {
  document.removeEventListener('click', closeOnOutsideClick)
  if (pollTimer) clearInterval(pollTimer)
})
</script>

<template>
  <div class="notification-bell relative">
    <button @click="togglePanel" class="relative hover:underline" aria-label="Notifications">
      🔔
      <span
        v-if="unreadCount > 0"
        class="absolute -top-2 -right-3 bg-red-500 text-white text-xs rounded-full px-1.5 py-0.5 leading-none"
      >
        {{ unreadCount > 9 ? '9+' : unreadCount }}
      </span>
    </button>

    <div
      v-if="showPanel"
      class="absolute right-0 mt-2 w-80 bg-white text-gray-800 rounded shadow-lg border z-50 max-h-96 overflow-y-auto"
    >
      <div class="flex justify-between items-center p-3 border-b">
        <span class="font-semibold">Notifications</span>
        <button
          v-if="unreadCount > 0"
          @click="markAllRead"
          class="text-sm text-blue-500 hover:underline"
        >
          Mark all read
        </button>
      </div>

      <div v-if="loading" class="p-4 text-center text-gray-400">Loading...</div>
      <div v-else-if="notifications.length === 0" class="p-4 text-center text-gray-400">
        No notifications
      </div>
      <ul v-else>
        <li
          v-for="n in notifications"
          :key="n.id"
          @click="markRead(n)"
          class="p-3 border-b cursor-pointer hover:bg-gray-50"
          :class="{ 'bg-blue-50': !n.isRead }"
        >
          <div class="flex justify-between items-start gap-2">
            <span class="text-xs font-medium text-blue-600">{{ typeLabel(n.type) }}</span>
            <span v-if="!n.isRead" class="w-2 h-2 mt-1 rounded-full bg-blue-500 shrink-0"></span>
          </div>
          <p class="text-sm mt-1">{{ n.message }}</p>
          <p class="text-xs text-gray-400 mt-1">{{ new Date(n.sentAt).toLocaleString() }}</p>
        </li>
      </ul>
    </div>
  </div>
</template>
