# Step 1: Build your Svelte app
FROM node:24 AS build
WORKDIR /app

# The API endpoints are baked into the bundle at build time. They default to a
# local development setup and are overridden per environment, e.g.
#   docker build --build-arg VITE_API_BASE_URL=https://api.example.com ...
ARG VITE_API_BASE_URL=http://localhost:8000
ARG VITE_LEETCODE_BASE_URL=http://localhost:3000
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}
ENV VITE_LEETCODE_BASE_URL=${VITE_LEETCODE_BASE_URL}

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
