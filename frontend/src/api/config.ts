interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string;
}

declare global {
  interface ImportMeta {
    readonly env: ImportMetaEnv;
  }
}

export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL ?? "http://localhost:8080/api";

console.log("REACT_APP_API_BASE_URL:", process.env.REACT_APP_API_BASE_URL);