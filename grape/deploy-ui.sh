#!/bin/bash

# ===================================================================
# Grape UI 前端发布脚本
# 路径: /home/grape/grapeui
# 启动命令: npm run serve:prod
# 端口: 8309
# ===================================================================

set -e

# 配置变量
PROJECT_PATH="/home/grape/grapeui"
GIT_REPO="https://git-intra.123u.com/wb-ginshi/grapeui.git"
SERVER_PORT="8309"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 停止前端进程
kill_process() {
    log_info "检查并停止前端进程..."
    
    # 方式1: 根据端口8309杀进程
    if command -v lsof >/dev/null 2>&1; then
        PID=$(lsof -ti:$SERVER_PORT 2>/dev/null || true)
        if [[ -n "$PID" ]]; then
            log_warning "找到端口 $SERVER_PORT 上的进程: $PID"
            kill -9 $PID
            log_success "端口进程已停止"
        fi
    fi
    
    # 方式2: 根据进程名杀进程 (查找grapeui相关的node进程)
    PIDS=$(ps aux | grep node | grep -v grep | grep grapeui | awk '{print $2}' || true)
    if [[ -n "$PIDS" ]]; then
        log_warning "找到grapeui相关进程: $PIDS"
        echo "$PIDS" | xargs kill -9 2>/dev/null || true
        log_success "相关进程已停止"
    fi
    
    # 方式3: 查找npm run serve:prod进程
    SERVE_PIDS=$(ps aux | grep "npm.*run.*serve:prod\|vue-cli-service.*serve" | grep -v grep | awk '{print $2}' || true)
    if [[ -n "$SERVE_PIDS" ]]; then
        log_warning "找到serve:prod进程: $SERVE_PIDS"
        echo "$SERVE_PIDS" | xargs kill -9 2>/dev/null || true
        log_success "serve:prod进程已停止"
    fi
    
    if [[ -z "$PID" && -z "$PIDS" && -z "$SERVE_PIDS" ]]; then
        log_info "没有找到运行中的前端进程"
    fi
    
    # 等待端口释放
    sleep 2
}

# 拉取代码
git_pull() {
    log_info "更新前端代码..."
    
    # 如果项目目录不存在，先克隆
    if [[ ! -d "$PROJECT_PATH" ]]; then
        log_info "项目目录不存在，克隆仓库..."
        PARENT_DIR=$(dirname "$PROJECT_PATH")
        mkdir -p "$PARENT_DIR"
        cd "$PARENT_DIR"
        git clone "$GIT_REPO" "$(basename "$PROJECT_PATH")"
        log_success "仓库克隆成功"
    fi
    
    cd "$PROJECT_PATH"
    
    # 检查是否是git仓库
    if [[ ! -d ".git" ]]; then
        log_error "项目目录不是Git仓库"
        exit 1
    fi
    
    # 检查并修复权限
    if [[ ! -w ".git/FETCH_HEAD" ]] && [[ -f ".git/FETCH_HEAD" ]]; then
        log_warning "Git权限不足，尝试修复..."
        chmod u+w .git/FETCH_HEAD 2>/dev/null || true
        chmod -R u+w .git/ 2>/dev/null || true
    fi
    
    # 拉取最新代码
    if git pull origin main 2>/dev/null || git pull origin master 2>/dev/null; then
        log_success "代码更新完成"
    else
        log_error "Git拉取失败，请检查权限或网络"
        exit 1
    fi
}

# NPM发布
npm_deploy() {
    log_info "开始NPM发布..."
    
    cd "$PROJECT_PATH"
    
    # 检查package.json
    if [[ ! -f "package.json" ]]; then
        log_error "package.json文件不存在"
        exit 1
    fi
    
    # 检查serve:prod脚本是否存在
    if ! grep -q '"serve:prod"' package.json; then
        log_warning "package.json中未找到serve:prod脚本，请检查脚本配置"
    fi
    
    # 安装依赖
    log_info "安装依赖..."
    npm install
    
    # 启动服务
    log_info "启动前端服务 (端口: $SERVER_PORT)..."
    nohup npm run serve:prod -- --port $SERVER_PORT > ui.log 2>&1 &
    
    # 获取进程ID
    APP_PID=$!
    log_success "前端服务已启动，进程ID: $APP_PID"
    
    # 等待几秒检查是否启动成功
    log_info "等待服务启动..."
    sleep 8
    
    if kill -0 $APP_PID 2>/dev/null; then
        log_success "前端服务启动成功!"
        log_info "访问地址: http://localhost:$SERVER_PORT"
        log_info "日志文件: $PROJECT_PATH/ui.log"
        
        # 检查端口是否被监听
        if command -v lsof >/dev/null 2>&1; then
            sleep 2
            PID=$(lsof -ti:$SERVER_PORT 2>/dev/null || true)
            if [[ -n "$PID" ]]; then
                log_success "端口 $SERVER_PORT 已成功绑定"
            else
                log_warning "端口 $SERVER_PORT 似乎未被绑定，请检查日志"
            fi
        fi
    else
        log_error "前端服务启动失败，请检查日志"
        if [[ -f "ui.log" ]]; then
            echo "=== 错误日志 ==="
            tail -20 ui.log
        fi
        exit 1
    fi
}

# 仅安装依赖
npm_install() {
    log_info "安装依赖..."
    
    cd "$PROJECT_PATH"
    
    # 检查package.json
    if [[ ! -f "package.json" ]]; then
        log_error "package.json文件不存在"
        exit 1
    fi
    
    npm install
    log_success "依赖安装完成!"
}

# 启动服务 (仅启动)
start_service() {
    log_info "启动前端服务..."
    
    cd "$PROJECT_PATH"
    
    # 检查package.json
    if [[ ! -f "package.json" ]]; then
        log_error "package.json文件不存在"
        exit 1
    fi
    
    # 检查node_modules是否存在
    if [[ ! -d "node_modules" ]]; then
        log_warning "node_modules不存在，先安装依赖..."
        npm_install
    fi
    
    # 启动服务
    log_info "启动前端服务 (端口: $SERVER_PORT)..."
    nohup npm run serve:prod -- --port $SERVER_PORT > ui.log 2>&1 &
    
    APP_PID=$!
    log_success "前端服务已启动，进程ID: $APP_PID"
    
    sleep 5
    if kill -0 $APP_PID 2>/dev/null; then
        log_success "前端服务启动成功!"
        log_info "访问地址: http://localhost:$SERVER_PORT"
    else
        log_error "前端服务启动失败"
        exit 1
    fi
}

# 检查状态
check_status() {
    log_info "检查前端服务状态..."
    
    # 检查端口8309
    if command -v lsof >/dev/null 2>&1; then
        PID=$(lsof -ti:$SERVER_PORT 2>/dev/null || true)
        if [[ -n "$PID" ]]; then
            log_success "前端服务正在运行，端口: $SERVER_PORT, 进程ID: $PID"
            echo "进程详情:"
            ps aux | grep "$PID" | grep -v grep || true
        else
            log_warning "端口 $SERVER_PORT 未被占用"
        fi
    fi
    
    # 检查serve:prod进程
    SERVE_PIDS=$(ps aux | grep "npm.*run.*serve:prod\|vue-cli-service.*serve" | grep -v grep | awk '{print $2}' || true)
    if [[ -n "$SERVE_PIDS" ]]; then
        log_success "找到serve:prod进程: $SERVE_PIDS"
        echo "serve:prod进程详情:"
        ps aux | grep "serve" | grep -v grep || true
    else
        log_warning "没有找到serve:prod进程"
    fi
    
    # 检查项目目录
    if [[ -d "$PROJECT_PATH" ]]; then
        log_info "项目目录: $PROJECT_PATH"
        log_info "当前分支: $(cd "$PROJECT_PATH" && git branch --show-current 2>/dev/null || echo '未知')"
    else
        log_warning "项目目录不存在: $PROJECT_PATH"
    fi
}

# 显示日志
show_logs() {
    LOG_FILE="$PROJECT_PATH/ui.log"
    if [[ -f "$LOG_FILE" ]]; then
        echo "=== 前端日志 (最后50行) ==="
        tail -50 "$LOG_FILE"
    else
        log_warning "日志文件不存在: $LOG_FILE"
    fi
}

# 实时查看日志
tail_logs() {
    LOG_FILE="$PROJECT_PATH/ui.log"
    if [[ -f "$LOG_FILE" ]]; then
        log_info "实时查看日志，按 Ctrl+C 退出"
        tail -f "$LOG_FILE"
    else
        log_warning "日志文件不存在: $LOG_FILE"
    fi
}

# 主函数
main() {
    echo "========================================"
    echo "       Grape UI 前端发布脚本"
    echo "       端口: $SERVER_PORT"
    echo "       命令: npm run serve:prod"
    echo "========================================"
    
    case "${1:-deploy}" in
        "deploy")
            log_info "开始前端完整发布流程..."
            kill_process
            git_pull
            npm_deploy
            log_success "前端发布完成!"
            ;;
        "kill")
            kill_process
            ;;
        "pull")
            git_pull
            ;;
        "install")
            npm_install
            ;;
        "start")
            start_service
            ;;
        "restart")
            kill_process
            sleep 3
            start_service
            ;;
        "status")
            check_status
            ;;
        "logs")
            show_logs
            ;;
        "tail")
            tail_logs
            ;;
        *)
            echo "用法: $0 [命令]"
            echo
            echo "可用命令:"
            echo "  deploy   - 完整发布 (kill → git pull → npm install → npm run serve:prod)"
            echo "  kill     - 停止前端服务"
            echo "  pull     - 更新代码"
            echo "  install  - 安装依赖"
            echo "  start    - 启动服务 (npm run serve:prod)"
            echo "  restart  - 重启服务"
            echo "  status   - 检查状态"
            echo "  logs     - 查看日志 (最后50行)"
            echo "  tail     - 实时查看日志"
            echo
            echo "示例:"
            echo "  $0           # 完整发布"
            echo "  $0 status    # 检查状态"
            echo "  $0 logs      # 查看日志"
            ;;
    esac
}

# 执行主函数
main "$@"

