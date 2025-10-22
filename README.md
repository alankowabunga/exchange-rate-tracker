# 匯率追蹤系統 - 部署指南

## 📋 專案簡介

這是一個基於 Spring Boot 開發的匯率追蹤應用程式，可以定時從台灣銀行爬取匯率資料，並提供 Web 介面和 REST API 供使用者查詢。

## 📁 專案結構

```
exchange-rate-project/
├── src/
│   ├── main/
│   │   ├── java/com/example/exchangerateproject/
│   │   │   ├── ExchangeRateProjectApplication.java
│   │   │   ├── config/
│   │   │   │   └── SchedulerConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── ExchangeRateController.java
│   │   │   │   └── WebController.java
│   │   │   ├── dto/
│   │   │   │   └── UpdateRateRequest.java
│   │   │   ├── entity/
│   │   │   │   └── ExchangeRate.java
│   │   │   ├── repository/
│   │   │   │   └── ExchangeRateRepository.java
│   │   │   └── service/
│   │   │       └── ExchangeRateService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── templates/
│   │           └── index.html
├── Dockerfile
├── docker-compose.yml
├── init.sql
├── pom.xml
└── README.md
```

## 🚀 快速開始

### 方法一: 使用 Docker Compose (推薦)

#### 1. 確保已安裝 Docker 和 Docker Compose

```bash
docker --version
docker-compose --version
```

#### 2. 建置並啟動服務

```bash
# 建置 Docker 映像檔並啟動所有服務
docker-compose up -d --build
```

#### 3. 查看服務狀態

```bash
docker-compose ps
```

#### 4. 查看日誌

```bash
# 查看應用程式日誌
docker-compose logs -f app

# 查看資料庫日誌
docker-compose logs -f mariadb
```

#### 5. 初始化資料（手動觸發爬取）

可以透過 REST API 或直接進入容器執行：

```bash
# 透過 API 手動觸發爬取（需要先在 Controller 新增觸發端點）
curl -X POST http://localhost:8080/api/fetch

# 或進入容器手動執行
docker exec -it exchange-rate-app sh
```

### 方法二: 本機開發運行

#### 1. 啟動 MariaDB

```bash
docker run -d \
  --name mariadb \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=exchange_rate_db \
  -e MYSQL_USER=appuser \
  -e MYSQL_PASSWORD=apppassword \
  -p 3306:3306 \
  mariadb:11.2
```

#### 2. 編譯專案

```bash
mvn clean package -DskipTests
```

#### 3. 執行應用程式

```bash
java -jar target/exchange-rate-project.jar
```

或使用 Maven:

```bash
mvn spring-boot:run
```

## 📊 功能說明

### 1. Web 介面

訪問 `http://localhost:8080` 查看匯率追蹤介面

**功能特點:**
- 選擇不同幣別查看匯率
- 選擇查詢天數 (7/14/30/60/90 天)
- 折線圖視覺化顯示匯率變化趨勢
- 表格列表顯示詳細資料

### 2. REST API

#### 查詢指定幣別最近 7 筆匯率

```bash
curl -X GET http://localhost:8080/api/rates/USD
```

**回應範例:**
```json
[
  {
    "id": 1,
    "currency": "USD",
    "rate": 31.5400,
    "date": "2025-10-21",
    "createdAt": "2025-10-21"
  },
  ...
]
```

#### 手動更新匯率

```bash
curl -X POST http://localhost:8080/api/update \
  -H "Content-Type: application/json" \
  -d '{
    "currency": "USD",
    "rate": 32.1,
    "date": "2025-10-01"
  }'
```

**回應範例:**
```json
{
  "success": true,
  "message": "匯率更新成功",
  "data": {
    "id": 123,
    "currency": "USD",
    "rate": 32.1000,
    "date": "2025-10-01",
    "createdAt": "2025-10-22"
  }
}
```

#### 取得所有可用幣別

```bash
curl -X GET http://localhost:8080/api/currencies
```

### 3. 定時爬取

系統預設每天早上 9:00 自動爬取台灣銀行匯率資料。

可在 `SchedulerConfig.java` 中修改排程設定：

```java
@Scheduled(cron = "0 0 9 * * ?")  // 每天 9:00
```

常用 Cron 表達式：
- `0 0 * * * ?` - 每小時執行
- `0 0 9,15 * * ?` - 每天 9:00 和 15:00 執行
- `0 */30 * * * ?` - 每 30 分鐘執行

## 🔧 環境變數配置

可透過環境變數覆蓋預設配置：

| 變數名稱 | 說明 | 預設值 |
|---------|------|--------|
| `DB_HOST` | 資料庫主機 | localhost |
| `DB_PORT` | 資料庫埠號 | 3306 |
| `DB_NAME` | 資料庫名稱 | exchange_rate_db |
| `DB_USER` | 資料庫使用者 | root |
| `DB_PASSWORD` | 資料庫密碼 | password |
| `SERVER_PORT` | 應用程式埠號 | 8080 |

## 🐛 疑難排解

### 問題 1: 容器無法連接資料庫

```bash
# 檢查資料庫容器是否正常運行
docker-compose ps

# 檢查網路連接
docker network inspect exchange-rate-project_exchange-rate-network

# 重啟服務
docker-compose restart
```

### 問題 2: 爬取資料失敗

可能原因：
- 網路連線問題
- 台灣銀行網站格式變更
- SSL 憑證問題

解決方法：
- 檢查應用程式日誌
- 手動測試 API 端點
- 確認可以訪問 https://rate.bot.com.tw

### 問題 3: 資料庫連接被拒絕

```bash
# 檢查 MariaDB 是否已完全啟動
docker-compose logs mariadb

# 等待健康檢查通過
docker-compose ps

# 手動連接測試
docker exec -it exchange-rate-db mysql -u appuser -papppassword exchange_rate_db
```

## 🧹 清理與停止

```bash
# 停止所有服務
docker-compose down

# 停止並刪除所有資料（包含資料庫資料）
docker-compose down -v

# 刪除 Docker 映像檔
docker rmi exchange-rate-project_app
```

## 📝 注意事項
```aiexclude
資料庫密碼: 請在正式環境修改 docker-compose.yml 中的密碼
爬蟲頻率: 可在 SchedulerConfig.java 調整執行時間
埠號衝突: 如 8080 或 3306 已被占用，請修改 docker-compose.yml
測試資料: init.sql 包含測試資料，可視需求刪除
```
