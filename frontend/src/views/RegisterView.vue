<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../services/api.js'

const name = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const router = useRouter()

async function register() {
  try {
    await api.post('/auth/register', {
      name: name.value,
      email: email.value,
      password: password.value,
    })
    router.push('/login')
  } catch (e) {
    error.value = 'Registration failed. Email may already exist.'
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="bg-white p-8 rounded shadow w-96">
      <h1 class="text-2xl font-bold mb-6">Create Account</h1>
      <input v-model="name" type="text" placeholder="Name" class="w-full border p-2 rounded mb-3" />
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
      <button @click="register" class="w-full bg-blue-500 text-white p-2 rounded">Register</button>
      <p class="mt-4 text-center">
        Already have an account? <a href="/login" class="text-blue-500">Login</a>
      </p>
    </div>
  </div>
</template>
