import React, { useState } from "react";
import TaskTypePage from "./pages/TaskTypePage";
import UploadExcelPage from "./pages/UploadExcelPage";
import CommonFieldsPage from "./pages/CommonFieldsPage";
import { TemplateType } from "./types/TemplateType";
import { API_BASE_URL } from "./api/config";
import ProfileMenu from "./components/ProfileMenu";
import LoginPage from "./pages/LoginPage";

type Step = "select" | "upload" | "fields";
type AuthMode = "login" | "register";

const getTemplateFileName = (type: keyof typeof TemplateType) => {
  switch (type) {
    case "COURSE_PROJECT":
      return "course_project_template.docx";
    case "COURSE_WORK":
      return "course_work_template.docx";
    case "DIPLOMA_PROJECT":
      return "diploma_project_template.docx";
    default:
      return "template.docx";
  }
};

const App: React.FC = () => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("token"));
  const [userEmail, setUserEmail] = useState<string | null>(() => localStorage.getItem("userEmail"));
  const [step, setStep] = useState<Step>("select");
  const [selectedType, setSelectedType] = useState<keyof typeof TemplateType | null>(null);
  const [excelFile, setExcelFile] = useState<File | null>(null);
  const [shuffle, setShuffle] = useState(false);
  const [finished, setFinished] = useState(false);
  const [showAuth, setShowAuth] = useState(false);
  const [authMode, setAuthMode] = useState<AuthMode>("login");
  const [profileMenu, setProfileMenu] = useState<boolean>(false);

  React.useEffect(() => {
    if (!token) return;
  }, [token]);

  const handleTypeSelect = (type: keyof typeof TemplateType) => {
    setSelectedType(type);
    localStorage.setItem("selectedType", type); 
    setStep("upload");
  };

  const handleUploadNext = (file: File, shuffleValue: boolean) => {
    setExcelFile(file);
    setShuffle(shuffleValue);
    setStep("fields");
  };

  const handleGenerate = async (fields: any) => {
    if (!excelFile || !selectedType) return;

    const formData = new FormData();
    formData.append("type", selectedType);
    formData.append("fileName", getTemplateFileName(selectedType));
    formData.append("shuffleTopics", shuffle ? "true" : "false");
    formData.append("excelFile", excelFile);
    formData.append("commonFields", JSON.stringify(fields));

    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("Требуется авторизация");

      const response = await fetch(`${API_BASE_URL}/templates/generate/from-excel`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      if (response.status === 401) {
        handleLogout();
        alert("Сессия истекла. Пожалуйста, войдите снова.");
        return;
      }

      if (!response.ok) {
        throw new Error("Ошибка при генерации файла");
      }

      const blob = await response.blob();
      const contentDisposition = response.headers.get("content-disposition");
      let filename = `${selectedType}_assignments.docx`;
      if (contentDisposition) {
        const match = contentDisposition.match(/filename="?([^"]+)"?/);
        if (match) filename = match[1];
      }

      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
      setFinished(true);
    } catch (e: any) {
      if (e.message === "Требуется авторизация") {
        handleLogout();
        alert("Сессия истекла. Пожалуйста, войдите снова.");
      } else {
        alert("Ошибка при генерации файла: " + e.message);
      }
    }
  };

  const handleLogin = (token: string, email: string) => {
    localStorage.setItem("token", token);
    localStorage.setItem("userEmail", email);
    setToken(token);
    setUserEmail(email);
    setShowAuth(false);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userEmail");
    localStorage.removeItem("selectedType");
    setToken(null);
    setUserEmail(null);
    setProfileMenu(false);
    setStep("select");
  };

  return (
    <div className="min-h-screen bg-blue-100 flex flex-col relative">
      <div className="absolute top-6 right-8 z-20 flex gap-3">
        {!token ? (
          <>
            <button
              className="px-6 py-2 border border-blue-700 rounded-2xl font-semibold text-blue-700 bg-white hover:bg-blue-50 transition shadow-sm"
              onClick={() => { setShowAuth(true); setAuthMode("login"); }}
            >
              Войти
            </button>
            <button
              className="px-6 py-2 border border-blue-700 rounded-2xl font-semibold text-blue-700 bg-white hover:bg-blue-50 transition shadow-sm"
              onClick={() => { setShowAuth(true); setAuthMode("register"); }}
            >
              Зарегистрироваться
            </button>
          </>
        ) : (
          <ProfileMenu
            userEmail={userEmail || ""}
            profileMenu={profileMenu}
            setProfileMenu={setProfileMenu}
            onLogout={handleLogout}
          />
        )}
      </div>

      {showAuth && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-xl p-8 min-w-[320px] relative">
            <button
              className="absolute top-2 right-3 text-gray-400 hover:text-gray-700 text-2xl"
              onClick={() => setShowAuth(false)}
              aria-label="Закрыть"
            >
              ×
            </button>
            <LoginPage
              mode={authMode}
              onLogin={handleLogin}
              onSwitchMode={mode => setAuthMode(mode)}
            />
          </div>
        </div>
      )}

      <main className="flex-1 flex items-center justify-center p-4">
        {finished ? (
          <div className="bg-white p-8 rounded-2xl shadow-md text-center">
            <h2 className="text-2xl font-bold mb-4 text-green-700">Готово!</h2>
            <p>Задания успешно сгенерированы и скачаны.</p>
            <button
              className="mt-6 px-4 py-2 rounded-2xl bg-blue-600 text-white"
              onClick={() => window.location.reload()}
            >
              Создать ещё
            </button>
          </div>
        ) : (
          <>
            {step === "select" && <TaskTypePage onSelect={handleTypeSelect} />}
            {step === "upload" && selectedType && (
              <UploadExcelPage
                onNext={handleUploadNext}
                onBack={() => setStep("select")}
              />
            )}
            {step === "fields" && selectedType && (
              <CommonFieldsPage
                templateType={selectedType}
                onBack={() => setStep("upload")}
                onGenerate={handleGenerate}
              />
            )}
          </>
        )}
      </main>
    </div>
  );
};

export default App;