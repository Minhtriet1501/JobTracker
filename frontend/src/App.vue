<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth.js'
import NotificationBell from './components/NotificationBell.vue'

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <nav v-if="auth.isAuthenticated" class="bg-blue-600 text-white px-6 py-3 flex gap-6 items-center">
    <span class="font-bold text-lg">Job Tracker</span>
    <a href="/dashboard" class="hover:underline">Dashboard</a>
    <a href="/applications" class="hover:underline">Applications</a>
    <a href="/profile" class="hover:underline">Profile</a>
    <NotificationBell class="ml-auto" />
    <button @click="logout" class="hover:underline">Logout</button>
  </nav>
  <RouterView />
</template>
