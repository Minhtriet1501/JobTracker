import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token'))
  const isAuthenticated = computed(() => !!token.value)

  function login(newToken) {
    localStorage.setItem('token', newToken)
    token.value = newToken
  }

  function logout() {
    localStorage.removeItem('token')
    token.value = null
  }

  return { token, isAuthenticated, login, logout }
})
