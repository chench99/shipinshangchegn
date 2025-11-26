@echo off

:: 食品商城项目部署脚本（Windows版本）
cls
echo ===========================================
echo           食品商城项目部署脚本
 echo ===========================================

:: 检查Docker和Docker Compose是否安装
docker --version >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: Docker 未安装，请先安装Docker
    pause
    exit /b 1
)

docker-compose --version >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: Docker Compose 未安装，请先安装Docker Compose
    pause
    exit /b 1
)

:: 检查.env文件是否存在
if not exist .env (
    echo 警告: .env文件不存在，正在从.env.example创建
    if exist .env.example (
        copy .env.example .env
        echo 已创建.env文件，请根据需要修改配置
    ) else (
        echo 错误: .env.example文件也不存在，无法继续
        pause
        exit /b 1
    )
)

:: 提示用户确认开始部署
echo. 
echo 注意：此操作将停止并重新构建当前运行的容器
set /p confirm=是否继续部署？(y/n): 
if /i not "%confirm%"=="y" (
    echo 部署已取消
    pause
    exit /b 0
)

:: 停止并移除现有容器
echo.
echo 正在停止并移除现有容器...
docker-compose down

:: 构建并启动容器
echo.
echo 正在构建镜像并启动容器...
docker-compose up -d --build

:: 检查启动状态
echo.
echo 正在检查服务状态...
ping -n 6 127.0.0.1 >nul 2>nul
docker ps | findstr "snack_mall_"

:: 提示部署完成
echo.
echo ===========================================
echo           食品商城项目部署完成
 echo ===========================================
echo 前端访问地址: http://localhost:80
echo 后端API地址: http://localhost:1234/api
echo 请确保防火墙已开放80和1234端口
echo.
echo 查看日志命令:
echo   前端日志: docker logs snack_mall_frontend
echo   后端日志: docker logs snack_mall_backend
echo   数据库日志: docker logs snack_mall_mysql
echo.
echo 部署脚本执行完毕！
pause