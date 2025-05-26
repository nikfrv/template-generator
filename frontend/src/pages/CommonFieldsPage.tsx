import React, { useState } from "react";
import { TemplateType } from "../types/TemplateType";

interface CommonFields {
  day: string;
  month: string;
  year: string;
  order?: string;
  deadline: string;
  content?: string;
  graphic?: string;
  consultants: string;
  assignmentDate: string; 
  projectStage: string;
}

interface Props {
  templateType: keyof typeof TemplateType;
  onBack: () => void;
  onGenerate: (fields: CommonFields) => void;
}

const monthsGenitive = [
  "января", "февраля", "марта", "апреля", "мая", "июня",
  "июля", "августа", "сентября", "октября", "ноября", "декабря"
];

const getTodayGenitive = () => {
  const now = new Date();
  return {
    day: String(now.getDate()).padStart(2, "0"),
    month: monthsGenitive[now.getMonth()],
    year: String(now.getFullYear() + ' г.'),
  };
};

const CommonFieldsPage: React.FC<Props> = ({ templateType, onBack, onGenerate }) => {
  const todayGenitive = getTodayGenitive();

  const [date, setDate] = useState({
    day: todayGenitive.day,
    month: todayGenitive.month,
    year: todayGenitive.year,
  });
  const [assignmentDate, setAssignmentDate] = useState({
    day: todayGenitive.day,
    month: todayGenitive.month,
    year: todayGenitive.year,
  });
  const [fields, setFields] = useState<Omit<CommonFields, "day" | "month" | "year" | "assignmentDate">>({
    order: "",
    deadline: "",
    content: "",
    graphic: "",
    consultants: "",
    projectStage: "",
  });

  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>, setDateFn: any) => {
    const { name, value } = e.target;
    setDateFn((prev: any) => ({ ...prev, [name]: value }));
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFields(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = () => {
    const assignmentDateStr = `${assignmentDate.day} ${assignmentDate.month} ${assignmentDate.year} `;

    onGenerate({
      ...fields,
      day: date.day,
      month: date.month,
      year: date.year,
      assignmentDate: assignmentDateStr,
    });
  };

  
  const templateTypeLabel = TemplateType[templateType];

  return (
    <div className="flex flex-col items-center justify-center bg-white p-8 rounded-2xl shadow-md w-full max-w-2xl">
      <h2 className="text-2xl font-bold mb-6 text-blue-700">Общие поля</h2>
      <div className="w-full space-y-4">

        <div>
          <label className="block text-blue-700 mb-1">Дата</label>
          <div className="flex space-x-2">
            <input
              type="text"
              name="day"
              value={date.day}
              onChange={e => handleDateChange(e, setDate)}
              className="w-16 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
              placeholder={todayGenitive.day}
              style={{ opacity: 0.6 }}
            />
            <span className="self-center">-</span>
            <input
              type="text"
              name="month"
              value={date.month}
              onChange={e => handleDateChange(e, setDate)}
              className="w-24 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
              placeholder={todayGenitive.month}
              style={{ opacity: 0.6 }}
            />
            <span className="self-center">-</span>
            <input
              type="text"
              name="year"
              value={date.year}
              onChange={e => handleDateChange(e, setDate)}
              className="w-20 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
              placeholder={todayGenitive.year}
              style={{ opacity: 0.6 }}
            />
            <span className="self-center"></span>
          </div>
        </div>

        {templateType === "DIPLOMA_PROJECT" && (
          <>
            <div>
              <label className="block text-blue-700 mb-1">Приказ по вузу от</label>
              <input
                name="order"
                value={fields.order}
                onChange={handleChange}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Введите приказ"
              />
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Сроки сдачи студентом законченного проекта</label>
              <input
                name="deadline"
                value={fields.deadline}
                onChange={handleChange}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Например: 21 мая 2025 г."
              />
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Содержание расчетно-пояснительной записки</label>
              <textarea
                name="content"
                value={fields.content}
                onChange={handleChange}
                rows={2}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Введите содержание"
              />
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Перечень графического материала</label>
              <textarea
                name="graphic"
                value={fields.graphic}
                onChange={handleChange}
                rows={2}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Введите перечень"
              />
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Консультанты по проекту</label>
              <textarea
                name="consultants"
                value={fields.consultants}
                onChange={handleChange}
                rows={2}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Введите консультантов"
              />
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Дата выдачи задания</label>
              <div className="flex space-x-2">
                <input
                  type="text"
                  name="day"
                  value={assignmentDate.day}
                  onChange={e => handleDateChange(e, setAssignmentDate)}
                  className="w-16 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
                  placeholder={todayGenitive.day}
                  style={{ opacity: 0.6 }}
                />
                <span className="self-center">-</span>
                <input
                  type="text"
                  name="month"
                  value={assignmentDate.month}
                  onChange={e => handleDateChange(e, setAssignmentDate)}
                  className="w-24 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
                  placeholder={todayGenitive.month}
                  style={{ opacity: 0.6 }}
                />
                <span className="self-center">-</span>
                <input
                  type="text"
                  name="year"
                  value={assignmentDate.year}
                  onChange={e => handleDateChange(e, setAssignmentDate)}
                  className="w-20 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
                  placeholder={todayGenitive.year}
                  style={{ opacity: 0.6 }}
                />
                <span className="self-center"></span>
              </div>
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Календарный график работы над проектом</label>
              <textarea
                name="projectStage"
                value={fields.projectStage}
                onChange={handleChange}
                rows={2}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Введите календарный график"
              />
            </div>
          </>
        )}

        {(templateType === "COURSE_PROJECT" || templateType === "COURSE_WORK") && (
          <>
            <div>
              <label className="block text-blue-700 mb-1">Сроки сдачи студентом законченного проекта</label>
              <input
                name="deadline"
                value={fields.deadline}
                onChange={handleChange}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Например: 21 марта 2025 г."
              />
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Содержание расчетно-пояснительной записки</label>
              <textarea
                name="content"
                value={fields.content}
                onChange={handleChange}
                rows={2}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Введите содержание"
              />
            </div>
            {templateType === "COURSE_PROJECT" && (
              <div>
                <label className="block text-blue-700 mb-1">Перечень графического материала</label>
                <textarea
                  name="graphic"
                  value={fields.graphic}
                  onChange={handleChange}
                  rows={2}
                  className="w-full border border-blue-300 rounded-lg p-2"
                  placeholder="Введите перечень"
                />
              </div>
            )}
            <div>
              <label className="block text-blue-700 mb-1">Консультанты по проекту</label>
              <textarea
                name="consultants"
                value={fields.consultants}
                onChange={handleChange}
                rows={2}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Введите консультантов"
              />
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Дата выдачи задания</label>
              <div className="flex space-x-2">
                <input
                  type="text"
                  name="day"
                  value={assignmentDate.day}
                  onChange={e => handleDateChange(e, setAssignmentDate)}
                  className="w-16 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
                  placeholder={todayGenitive.day}
                  style={{ opacity: 0.6 }}
                />
                <span className="self-center">-</span>
                <input
                  type="text"
                  name="month"
                  value={assignmentDate.month}
                  onChange={e => handleDateChange(e, setAssignmentDate)}
                  className="w-24 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
                  placeholder={todayGenitive.month}
                  style={{ opacity: 0.6 }}
                />
                <span className="self-center">-</span>
                <input
                  type="text"
                  name="year"
                  value={assignmentDate.year}
                  onChange={e => handleDateChange(e, setAssignmentDate)}
                  className="w-20 border border-blue-300 rounded-lg p-2 placeholder-blue-300"
                  placeholder={todayGenitive.year}
                  style={{ opacity: 0.6 }}
                />
                <span className="self-center"></span>
              </div>
            </div>
            <div>
              <label className="block text-blue-700 mb-1">Календарный график работы над проектом</label>
              <textarea
                name="projectStage"
                value={fields.projectStage}
                onChange={handleChange}
                rows={2}
                className="w-full border border-blue-300 rounded-lg p-2"
                placeholder="Введите календарный график"
              />
            </div>
          </>
        )}
      </div>

      <div className="flex space-x-4 mt-6">
        <button
          onClick={onBack}
          className="px-4 py-2 rounded-lg bg-blue-50 text-blue-700 border border-blue-300 hover:bg-blue-100 transition"
        >
          Назад
        </button>
        <button
          onClick={handleSubmit}
          className="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 transition"
        >
          Сгенерировать задания
        </button>
      </div>
    </div>
  );
};

export default CommonFieldsPage;
