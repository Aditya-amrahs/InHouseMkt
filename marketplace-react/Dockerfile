FROM node:22 AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html
# For CRA, replace /app/dist with /app/build

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]