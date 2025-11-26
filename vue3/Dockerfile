# 阶段1：构建Vue应用
FROM node:16-alpine as builder

# 设置工作目录
WORKDIR /app

# 复制package.json和package-lock.json
COPY package*.json ./

# 安装依赖
RUN npm install

# 复制项目文件
COPY . .

# 构建生产版本
RUN npm run build

# 阶段2：使用Nginx部署
FROM nginx:alpine

# 复制构建好的文件到nginx目录
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制自定义的nginx配置（如果需要）
COPY nginx.conf /etc/nginx/conf.d/default.conf 2>/dev/null || echo "使用默认nginx配置"

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]