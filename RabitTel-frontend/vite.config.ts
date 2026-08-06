import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      "/api/auth": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
      "/api/admin": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
      "/api/profile": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
      "/api/password": {
        target: "http://localhost:8082",
        changeOrigin: true,
      },
      "/api/v1/notifications": {
        target: "http://localhost:8086",
        changeOrigin: true,
      },
      "/api": {
        target: "http://localhost:8081",
        changeOrigin: true,
      },
    },
  },
});
