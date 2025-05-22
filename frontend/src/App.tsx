import React, { useState } from "react";
import TaskTypePage from "./pages/TaskTypePage";
import UploadExcelPage from "./pages/UploadExcelPage";
import CommonFieldsPage from "./pages/CommonFieldsPage";
import { TemplateType } from "./types/TemplateType";
import { API_BASE_URL } from "./api/config";

type Step = "select" | "upload" | "fields";

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
  const [step, setStep] = useState<Step>("select");
  const [selectedType, setSelectedType] = useState<keyof typeof TemplateType | null>(null); // КЛЮЧ!
  const [excelFile, setExcelFile] = useState<File | null>(null);
  const [shuffle, setShuffle] = useState(false);

  const handleTypeSelect = (type: keyof typeof TemplateType) => { 
    setSelectedType(type);
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
      const response = await fetch(`${API_BASE_URL}/templates/generate/from-excel`, {
        method: "POST",
        body: formData,
      });

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
    } catch (e) {
      alert("Ошибка при генерации файла: " + (e as Error).message);
    }
  };

  return (
    <div className="min-h-screen bg-blue-100 flex items-center justify-center p-4">
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
    </div>
  );
};

export default App;