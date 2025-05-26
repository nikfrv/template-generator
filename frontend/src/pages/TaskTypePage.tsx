import React, { useState } from "react";
import { TemplateType } from "../types/TemplateType";

type Props = {
  onSelect: (type: keyof typeof TemplateType) => void; 
};

const TaskTypePage: React.FC<Props> = ({ onSelect }) => {
  const [selected, setSelected] = useState<TemplateType | null>(null);

  const handleClick = (key: keyof typeof TemplateType) => {
    setSelected(TemplateType[key]); 
    onSelect(key); 
  };

  return (
    <div className="bg-white p-8 rounded-2xl shadow-md w-full max-w-md">
      <h1 className="text-2xl font-bold text-center mb-6 text-blue-700">
        Выберите тип задания
      </h1>
      <div className="flex flex-col gap-4">
        {Object.keys(TemplateType).map((key) => (
          <button
            key={key}
            onClick={() => handleClick(key as keyof typeof TemplateType)}
            className={`px-6 py-3 rounded-xl border-2 transition-colors duration-200 font-semibold text-lg
              ${
                selected === TemplateType[key as keyof typeof TemplateType]
                  ? "bg-blue-500 text-white border-blue-700 shadow"
                  : "bg-blue-50 text-blue-800 border-blue-300 hover:bg-blue-100"
              }`}
          >
            {TemplateType[key as keyof typeof TemplateType]}
          </button>
        ))}
      </div>
    </div>
  );
};

export default TaskTypePage;