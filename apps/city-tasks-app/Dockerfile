##########################################################################
######################## Stage 1 : Builder Image #########################
##########################################################################
FROM node:18 AS builder
WORKDIR /workspace

RUN npm install -g pnpm
COPY pnpm-lock.yaml ./
COPY package.json ./
RUN pnpm install

COPY . .
RUN pnpm run build

##########################################################################
######################## Stage 2: Application Image ######################
##########################################################################
FROM nginx:alpine
COPY --from=builder /workspace/dist/city-tasks-app /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
