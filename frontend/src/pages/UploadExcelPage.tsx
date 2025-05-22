import React, { useState, useRef } from "react";

interface UploadExcelPageProps {
  onNext: (file: File, shuffle: boolean) => void;
  onBack: () => void;
}

const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

const UploadExcelPage: React.FC<UploadExcelPageProps> = ({ onNext, onBack }) => {
  const [file, setFile] = useState<File | null>(null);
  const [dragActive, setDragActive] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [shuffle, setShuffle] = useState(false);
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

  const handleSubmit = () => {
    if (file) {
      onNext(file, shuffle);
    }
  };

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
        )}
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
        >
          Назад
        </button>

        <button
          onClick={handleSubmit}
          disabled={!file}
          className={`px-4 py-2 rounded-lg transition ${
            file
              ? "bg-blue-600 text-white hover:bg-blue-700"
              : "bg-gray-300 text-gray-600 cursor-not-allowed"
          }`}
        >
          Распределить темы
        </button>
      </div>
    </div>
  );
};

export default UploadExcelPage;
