import React, { useState } from "react";
import { API_BASE_URL } from "../api/config";

type Props = {
  mode: "login" | "register";
  onLogin: (token: string, email: string) => void;
  onSwitchMode: (mode: "login" | "register") => void;
};

const LoginPage: React.FC<Props> = ({ mode, onLogin, onSwitchMode }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [fullName, setFullName] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    const url = mode === "login" ? "/auth/login" : "/auth/register";
    const body =
      mode === "login"
        ? { email, password }
        : { email, password, fullName };

    try {
      const response = await fetch(API_BASE_URL + url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
      if (response.ok) {
        const data = await response.json();
        console.log("accessToken:", data.accessToken);
        onLogin(data.accessToken, email);
      } else {
        const err = await response.json().catch(() => ({}));
        setError(
          err?.message ||
            (mode === "login"
              ? "Ошибка авторизации"
              : "Ошибка регистрации")
        );
      }
    } catch (e) {
      setError("Ошибка сети или сервера");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="max-w-xs mx-auto p-0 bg-white">
      <h2 className="text-xl font-bold mb-4 text-center">
        {mode === "login" ? "Вход" : "Регистрация"}
      </h2>
      {mode === "register" && (
        <input
          className="w-full mb-2 p-2 border rounded"
          placeholder="ФИО"
          type="text"
          value={fullName}
          onChange={e => setFullName(e.target.value)}
          required
        />
      )}
      <input
        className="w-full mb-2 p-2 border rounded"
        placeholder="E-mail"
        type="email"
        value={email}
        onChange={e => setEmail(e.target.value)}
        required
      />
      <input
        className="w-full mb-2 p-2 border rounded"
        placeholder="Пароль"
        type="password"
        value={password}
        onChange={e => setPassword(e.target.value)}
        required
      />
      {error && <div className="text-red-500 mb-2">{error}</div>}
      <button
        className="w-full border border-blue-700 text-blue-700 py-2 rounded hover:bg-blue-50 transition font-semibold"
        type="submit"
      >
        {mode === "login" ? "Войти" : "Зарегистрироваться"}
      </button>
      <div className="mt-4 text-center">
        {mode === "login" ? (
          <span>
            Нет аккаунта?{" "}
            <button
              type="button"
              className="text-blue-700 underline"
              onClick={() => onSwitchMode("register")}
            >
              Зарегистрироваться
            </button>
          </span>
        ) : (
          <span>
            Уже есть аккаунт?{" "}
            <button
              type="button"
              className="text-blue-700 underline"
              onClick={() => onSwitchMode("login")}
            >
              Войти
            </button>
          </span>
        )}
      </div>
    </form>
  );
};

export default LoginPage;