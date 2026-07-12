<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api.js'

const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

async function login() {
  try {
    const res = await api.post('/auth/login', { email: email.value, password: password.value })
    localStorage.setItem('token', res.data.token)
    router.push('/dashboard')
  } catch (e) {
    error.value = 'Email or Password is incorrect'
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded shadow w-96">
      <h1 class="text-2xl font-bold mb-6">Job Tracker</h1>
      <input
        v-model="email"
        type="email"
        placeholder="Email"
        class="w-full border p-2 rounded mb-3"
      />
      <input
        v-model="password"
        type="password"
        placeholder="Password"
        class="w-full border p-2 rounded mb-3"
      />
      <p v-if="error" class="text-red-500 mb-3">{{ error }}</p>
      <button @click="login" class="w-full bg-blue-500 text-white p-2 rounded">Login</button>
      <p class="mt-4 text-center">
        Don't have an account? <a href="/register" class="text-blue-500">Register</a>
      </p>
    </div>
  </div>
</template>
