import React, { useState, useRef } from "react";
import { API_BASE_URL } from "../api/config";

interface UploadExcelPageProps {
  onNext: (file: File, shuffle: boolean) => void;
  onBack: () => void;
}

const MAX_FILE_SIZE = 5 * 1024 * 1024;

const UploadExcelPage: React.FC<UploadExcelPageProps> = ({ onNext, onBack }) => {
  const [file, setFile] = useState<File | null>(null);
  const [dragActive, setDragActive] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [shuffle, setShuffle] = useState(false);
  const [loading, setLoading] = useState(false);
  const [duplicates, setDuplicates] = useState<string[]>([]);
  const [showDuplicates, setShowDuplicates] = useState(false);
  const [pendingUpload, setPendingUpload] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      validateAndSetFile(e.target.files[0]);
    }
  };

  const validateAndSetFile = (file: File) => {
    if (!file.name.endsWith(".xlsx")) {
      setError("Можно загрузить только .xlsx файл");
      setFile(null);
      return;
    }
    if (file.size > MAX_FILE_SIZE) {
      setError("Максимальный размер файла — 5 МБ");
      setFile(null);
      return;
    }
    setError(null);
    setFile(file);
  };

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(true);
  };

  const handleDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      validateAndSetFile(e.dataTransfer.files[0]);
    }
  };

  const handleClick = () => {
    inputRef.current?.click();
  };

  const handleSubmit = async () => {
    if (!file) return;
    setLoading(true);
    setError(null);
    setDuplicates([]);
    setShowDuplicates(false);
    setPendingUpload(false);

    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("Требуется авторизация");
      const templateType = localStorage.getItem("selectedType");
      if (!templateType) throw new Error("Не выбран тип задания");

      const checkFormData = new FormData();
      checkFormData.append("type", templateType);
      checkFormData.append("excelFile", file);

      const checkResp = await fetch(`${API_BASE_URL}/templates/check-duplicates`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: checkFormData,
      });
      const checkData = await checkResp.json();
      if (checkData.duplicates && checkData.duplicates.length > 0) {
        setDuplicates(checkData.duplicates);
        setShowDuplicates(true);
        setPendingUpload(true); 
        return;
      }

      await uploadExcel();
    } catch (e: any) {
      setError(e.message || "Ошибка загрузки Excel");
    } finally {
      setLoading(false);
    }
  };

  const uploadExcel = async () => {
    if (!file) return;
    setLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem("token");
      const templateType = localStorage.getItem("selectedType");
      const formData = new FormData();
      formData.append("type", templateType!);
      formData.append("excelFile", file);

      const response = await fetch(`${API_BASE_URL}/templates/upload/excel`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      if (response.status === 401) {
        setError("Сессия истекла. Пожалуйста, войдите снова.");
        localStorage.removeItem("token");
        window.location.reload();
        return;
      }

      if (!response.ok) {
        const text = await response.text();
        setError(text || "Ошибка загрузки Excel");
        return;
      }

      onNext(file, shuffle);
    } catch (e: any) {
      setError(e.message || "Ошибка загрузки Excel");
    } finally {
      setLoading(false);
      setShowDuplicates(false);
      setDuplicates([]);
      setPendingUpload(false);
    }
  };

  const DuplicatesModal = () => (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
      <div className="bg-white rounded-xl p-6 max-w-lg w-full shadow-lg max-h-[80vh] overflow-y-auto">
        <h3 className="text-lg font-bold mb-2 text-red-700">Внимание!</h3>
        <div className="mb-2">Следующие темы уже были за последние 5 лет:</div>
        <ul className="mb-4 max-h-60 overflow-y-auto pr-2">
          {duplicates.map(t => <li key={t} className="text-red-700">- {t}</li>)}
        </ul>
        <div className="flex gap-4 justify-end">
          <button
            onClick={() => {
              setShowDuplicates(false);
              setDuplicates([]);
              setPendingUpload(false);
            }}
            className="px-4 py-2 rounded bg-blue-50 text-blue-700 border border-blue-300 hover:bg-blue-100"
          >
            Вернуться к загрузке файла
          </button>
          <button
            onClick={uploadExcel}
            className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
            disabled={loading}
          >
            Продолжить
          </button>
        </div>
      </div>
    </div>
  );

  return (
    <div className="flex flex-col items-center justify-center bg-white p-8 rounded-2xl shadow-md w-full max-w-xl">
      <h2 className="text-2xl font-bold mb-4 text-blue-700">
        Загрузка Excel-файла
      </h2>

      <div
        className={`mb-4 w-full h-48 border-2 border-dashed rounded-lg flex items-center justify-center text-center cursor-pointer transition
          ${dragActive ? "border-blue-500 bg-blue-50" : "border-blue-300 bg-blue-100 hover:bg-blue-200"}`}
        onClick={handleClick}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
      >
        {!dragActive && (
          <input
            type="file"
            accept=".xlsx"
            ref={inputRef}
            onChange={handleFileChange}
            className="hidden"
          />
        )}
        {file ? (
          <span className="text-blue-700 font-semibold">{file.name}</span>
        ) : (
          <span className="text-gray-600">
            Перетащите файл сюда или{" "}
            <span className="text-blue-600 underline">выберите</span>
          </span>
        )
        }
      </div>

      {error && <div className="text-red-600 mb-2">{error}</div>}

      {file && (
        <div className="flex items-center space-x-4 mb-4">
          <label className="flex items-center space-x-2 cursor-pointer">
            <input
              type="checkbox"
              checked={shuffle}
              onChange={e => setShuffle(e.target.checked)}
              className="accent-blue-600"
            />
            <span className="text-blue-700">Перемешать темы</span>
          </label>
        </div>
      )}

      <div className="flex space-x-4">
        <button
          onClick={onBack}
          className="px-4 py-2 rounded-lg bg-blue-50 text-blue-700 border border-blue-300 hover:bg-blue-100 transition"
          disabled={loading}
        >
          Назад
        </button>

        <button
          onClick={handleSubmit}
          disabled={!file || loading}
          className={`px-4 py-2 rounded-lg transition ${
            file && !loading
              ? "bg-blue-600 text-white hover:bg-blue-700"
              : "bg-gray-300 text-gray-600 cursor-not-allowed"
          }`}
        >
          {loading ? "Загрузка..." : "Распределить темы"}
        </button>
      </div>

      {showDuplicates && <DuplicatesModal />}
    </div>
  );
};

export default UploadExcelPage;
