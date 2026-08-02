import axios, { type AxiosResponse } from "axios";

const TOKEN_KEY = "rabittel_token";

function normalizeUserFields(obj: unknown): unknown {
  if (!obj || typeof obj !== "object") return obj;
  if (Array.isArray(obj)) return obj.map(normalizeUserFields);

  const o = obj as Record<string, unknown>;

  if ("active" in o && "email" in o && "role" in o && !("isActive" in o)) {
    o.isActive = o.active;
  }

  for (const key of Object.keys(o)) {
    o[key] = normalizeUserFields(o[key]);
  }

  return o;
}

export const getToken = (): string | null => {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
};

export const setToken = (t: string | null) => {
  if (t) localStorage.setItem(TOKEN_KEY, t);
  else localStorage.removeItem(TOKEN_KEY);
};

export const clearToken = () => localStorage.removeItem(TOKEN_KEY);

const api = axios.create({
  baseURL: "/api",
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const t = getToken();
  if (t) {
    config.headers.Authorization = `Bearer ${t}`;
  }
  return config;
});

api.interceptors.response.use(
  (r: AxiosResponse) => {
    normalizeUserFields(r.data);
    return r;
  },
  (err: unknown) => {
    const e = err as {
      response?: { status?: number; data?: unknown };
      message?: string;
    };
    if (e.response?.status === 401) {
      clearToken();
      if (location.pathname !== "/login") {
        location.href =
          "/login?redirect=" +
          encodeURIComponent(location.pathname + location.search);
      }
    }
    console.error("API Error:", e.response?.data ?? e.message);
    return Promise.reject(err);
  },
);

export default api;
