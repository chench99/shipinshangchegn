#!/bin/bash

# 食品商城项目部署脚本（Linux版本）
echo "====== 食品商城项目部署开始 ======"

# 设置颜色
green="\033[0;32m"
yellow="\033[1;33m"
red="\033[0;31m"
nc="\033[0m" # No Color

# 检查Docker和Docker Compose是否安装
command -v docker >/dev/null 2>&1 || { echo -e "${red}错误: Docker 未安装，请先安装Docker${nc}"; exit 1; }
command -v docker-compose >/dev/null 2>&1 || { echo -e "${red}错误: Docker Compose 未安装，请先安装Docker Compose${nc}"; exit 1; }

# 检查.env文件是否存在
if [ ! -f ".env" ]; then
    echo -e "${yellow}警告: .env文件不存在，正在从.env.example创建${nc}"
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo -e "${green}已创建.env文件，请根据需要修改配置${nc}"
    else
        echo -e "${red}错误: .env.example文件也不存在，无法继续${nc}"
        exit 1
    fi
fi

# 提示用户确认开始部署
echo -e "${yellow}注意：此操作将停止并重新构建当前运行的容器${nc}"
read -p "是否继续部署？(y/n): " confirm
if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
    echo -e "${yellow}部署已取消${nc}"
    exit 0
fi

# 停止并移除现有容器
echo -e "${green}正在停止并移除现有容器...${nc}"
docker-compose down

# 构建并启动容器
echo -e "${green}正在构建镜像并启动容器...${nc}"
docker-compose up -d --build

# 检查启动状态
echo -e "${green}正在检查服务状态...${nc}"
sleep 5

docker ps | grep snack_mall_

# 提示部署完成
echo -e "${green}====== 食品商城项目部署完成 ======${nc}"
echo -e "${green}前端访问地址: http://服务器IP:80${nc}"
echo -e "${green}后端API地址: http://服务器IP:1234/api${nc}"
echo -e "${yellow}请确保服务器防火墙已开放80和1234端口${nc}"

# 显示日志命令提示
echo -e "${yellow}查看日志命令:${nc}"
echo -e "  ${yellow}前端日志: docker logs snack_mall_frontend${nc}"
echo -e "  ${yellow}后端日志: docker logs snack_mall_backend${nc}"
echo -e "  ${yellow}数据库日志: docker logs snack_mall_mysql${nc}"

echo -e "${green}部署脚本执行完毕！${nc}"