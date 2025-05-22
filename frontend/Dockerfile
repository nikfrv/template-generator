# Используем Node.js 22
FROM node:22

# Рабочая директория
WORKDIR /app

# Копируем файлы зависимостей
COPY package*.json ./

# Установка зависимостей
RUN npm install

# Копируем остальные файлы
COPY . .

# Сборка проекта
RUN npm run build

# Стадия с nginx
FROM nginx:stable-alpine
COPY --from=0 /app/build /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]