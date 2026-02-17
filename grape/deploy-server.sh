#!/bin/bash

# ===================================================================
# Grape Server 简单发布脚本
# ===================================================================

set -e

# 配置变量
PROJECT_ROOT="/home/grape/grapeserver"          # Git仓库根目录
PROJECT_PATH="/home/grape/grapeserver/grape"    # Maven项目目录（包含pom.xml）
GIT_REPO="https://git-intra.123u.com/wb-ginshi/grapeserver.git"
JAR_NAME="grape-0.0.1-SNAPSHOT.jar"
JAR_PATH="$PROJECT_PATH/target/$JAR_NAME"
PROCESS_NAME="grape"

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

# 检查并停止现有进程
kill_process() {
    log_info "检查并停止现有进程..."
    
    # 查找进程ID
    PID=$(ps aux | grep "$PROCESS_NAME" | grep -v grep | grep "jar" | awk '{print $2}')
    
    if [[ -n "$PID" ]]; then
        log_warning "找到运行中的进程: $PID"
        kill -9 $PID
        log_success "进程已停止"
    else
        log_info "没有找到运行中的进程"
    fi
}

# 克隆或拉取代码
git_pull() {
    log_info "更新代码..."
    
    # 如果Git仓库目录不存在，先克隆
    if [[ ! -d "$PROJECT_ROOT" ]]; then
        log_info "项目目录不存在，克隆仓库..."
        PARENT_DIR=$(dirname "$PROJECT_ROOT")
        mkdir -p "$PARENT_DIR"
        cd "$PARENT_DIR"
        git clone "$GIT_REPO" "$(basename "$PROJECT_ROOT")"
        log_success "仓库克隆成功"
    fi
    
    # 切换到Git根目录执行pull
    cd "$PROJECT_ROOT"
    
    # 如果不是git仓库，先初始化
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

# Maven编译打包
mvn_build() {
    log_info "开始编译打包..."
    
    # 检查Maven项目目录是否存在
    if [[ ! -d "$PROJECT_PATH" ]]; then
        log_error "Maven项目目录不存在: $PROJECT_PATH"
        exit 1
    fi
    
    # 检查pom.xml是否存在
    if [[ ! -f "$PROJECT_PATH/pom.xml" ]]; then
        log_error "pom.xml文件不存在: $PROJECT_PATH/pom.xml"
        exit 1
    fi
    
    # 切换到Maven项目目录
    cd "$PROJECT_PATH"
    log_info "当前目录: $(pwd)"
    
    # 清理并编译
    mvn clean package -DskipTests
    
    # 检查JAR文件是否生成
    if [[ -f "target/$JAR_NAME" ]]; then
        log_success "编译完成: $JAR_PATH"
    else
        log_error "编译失败，JAR文件不存在: target/$JAR_NAME"
        # 列出target目录内容
        if [[ -d "target" ]]; then
            log_info "target目录内容:"
            ls -la target/
        fi
        exit 1
    fi
}

# 启动应用
start_app() {
    log_info "启动应用..."
    
    # 切换到Maven项目目录
    cd "$PROJECT_PATH"
    
    # 检查JAR文件
    if [[ ! -f "target/$JAR_NAME" ]]; then
        log_error "JAR文件不存在: target/$JAR_NAME"
        exit 1
    fi
    
    # 后台启动应用
    nohup java -jar "target/$JAR_NAME" > app.log 2>&1 &
    
    # 获取进程ID
    APP_PID=$!
    log_success "应用已启动，进程ID: $APP_PID"
    
    # 等待几秒检查是否启动成功
    sleep 3
    if kill -0 $APP_PID 2>/dev/null; then
        log_success "应用启动成功!"
        log_info "日志文件: $PROJECT_PATH/app.log"
    else
        log_error "应用启动失败，请检查日志"
        if [[ -f "app.log" ]]; then
            echo "=== 错误日志 ==="
            tail -20 app.log
        fi
        exit 1
    fi
}

# 检查应用状态
check_status() {
    log_info "检查应用状态..."
    
    PID=$(ps aux | grep "$PROCESS_NAME" | grep -v grep | grep "jar" | awk '{print $2}')
    
    if [[ -n "$PID" ]]; then
        log_success "应用正在运行，进程ID: $PID"
        
        # 显示进程详情
        echo "进程详情:"
        ps aux | grep "$PID" | grep -v grep
    else
        log_warning "应用未运行"
    fi
}

# 显示日志
show_logs() {
    LOG_FILE="$PROJECT_PATH/app.log"
    if [[ -f "$LOG_FILE" ]]; then
        echo "=== 应用日志 (最后30行) ==="
        tail -30 "$LOG_FILE"
    else
        log_warning "日志文件不存在: $LOG_FILE"
    fi
}

# 显示项目信息
show_info() {
    echo "=== 项目信息 ==="
    echo "Git仓库目录: $PROJECT_ROOT"
    echo "Maven项目目录: $PROJECT_PATH"
    echo "JAR文件路径: $JAR_PATH"
    echo "日志文件: $PROJECT_PATH/app.log"
    echo
    
    echo "=== 目录结构 ==="
    if [[ -d "$PROJECT_ROOT" ]]; then
        echo "Git根目录内容:"
        ls -la "$PROJECT_ROOT" | head -10
        echo
    fi
    
    if [[ -d "$PROJECT_PATH" ]]; then
        echo "Maven项目目录内容:"
        ls -la "$PROJECT_PATH" | head -10
    fi
}

# 主函数
main() {
    echo "========================================"
    echo "    Grape Server 发布脚本"
    echo "========================================"
    
    case "${1:-deploy}" in
        "deploy")
            log_info "开始完整发布流程..."
            kill_process
            git_pull
            mvn_build
            start_app
            log_success "发布完成!"
            ;;
        "kill")
            kill_process
            ;;
        "pull")
            git_pull
            ;;
        "build")
            mvn_build
            ;;
        "start")
            start_app
            ;;
        "status")
            check_status
            ;;
        "logs")
            show_logs
            ;;
        "restart")
            kill_process
            sleep 2
            start_app
            ;;
        "info")
            show_info
            ;;
        *)
            echo "用法: $0 [命令]"
            echo
            echo "可用命令:"
            echo "  deploy  - 完整发布 (默认)"
            echo "  kill    - 停止应用"
            echo "  pull    - 更新代码"
            echo "  build   - 编译打包"
            echo "  start   - 启动应用"
            echo "  restart - 重启应用"
            echo "  status  - 检查状态"
            echo "  logs    - 查看日志"
            echo "  info    - 显示项目信息"
            echo
            echo "示例:"
            echo "  $0          # 完整发布"
            echo "  $0 info     # 查看项目信息"
            echo "  $0 status   # 检查状态"
            ;;
    esac
}

# 执行主函数
main "$@"

