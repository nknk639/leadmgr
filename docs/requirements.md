# ハローワーク電話リスト活用システム
要件定義書（詳細版）

1. 目的

ハローワークの求人 CSV を取り込み、リード管理・架電履歴蓄積・アポ率分析を Mac ブラウザ（単独ユーザー）で行う。

2. 技術スタック（確定）

| 層 | 技術 |
|---|---|
| テンプレート | Thymeleaf 3 |
| フロント | HTML5 / CSS3 (Bootstrap 5.3 CDN)
JavaScript (ES2020, Chart.js 4) |
| バックエンド | Java 17 + Spring Boot 3 (Web / Data JPA) |
| データベース | MySQL 8.x（ローカル & 本番とも統一、Flywayで管理） |
| ビルド | Maven 3.9 + spring-boot-maven-plugin → 単一 JAR |

3. 画面 (Thymeleaf テンプレート) と URL マッピング

| # | 画面名 | テンプレート | URL / Method | 主な要素 | Controller |
|---|---|---|---|---|---|
| 1 | ダッシュボード | dashboard.html | / (GET) | KPIカード, Chart.js | DashboardController |
| 2 | CSV インポート | import/import.html | /import (GET) | ファイル選択・プレビュー | ImportController |
| 3 | CSV インポート実行 | — (Ajax) | /import (POST) | 進捗バー・結果モーダル | ImportController |
| 4 | リード一覧 | leads/list.html | /leads (GET) | 検索, フィルタ, ソート | LeadController |
| 5 | リード詳細 | leads/detail.html | /leads/{id} (GET) | 会社情報, 履歴 | LeadController |
| 6 | 架電登録 | — (Ajax) | /leads/{id}/calls (POST) | 結果入力フォーム | CallController |
| 7 | 結果コード管理 | codes/list.html | /codes (GET) | コード一覧・編集 | CodeController |
| 8 | 結果コード更新 | — (Ajax) | /codes/{id} (PUT) | ラベル編集・無効化 | CodeController |
| 9 | 再コールリスト | leads/recalls.html | /recalls (GET) | 再コール対象表 | LeadController |
| 10 | CSV エクスポート | — | /export (GET) | DL生成 | ExportController |

4. ファイル／ディレクトリ構成（トップレベル & Docker）

leadmgr/

├─ .gitignore

├─ docker-compose.yml

├─ .env  # MYSQL_* 変数 (MYSQL_DATABASE も含む)

├─ pom.xml

├─ src/main/java/com/example/leads/…

│    ├─ controller/

│    ├─ service/

│    ├─ repository/

│    ├─ model/

│    └─ util/

├─ src/main/resources/

│    ├─ db/migration/  # Flyway V1__init.sql など

│    ├─ templates/

│    └─ static/

└─ README.md

docker-compose.yml 内 services:

db: mysql:8 + .env で DB 作成

adminer: adminer 8081:8080

5. データモデル

テーブル: leads

| 列 | 型 | 制約 / 説明 |
|---|---|---|
| Lead_id | BIGINT | PK, AUTO_INCREMENT |
| company_name | VARCHAR(255) | NOT NULL |
| address | TEXT |  |
| phone_number | VARCHAR(20) | NOT NULL, UNIQUE |
| job_title | VARCHAR(255) |  |
| job_description | TEXT |  |
| posting_date | DATE |  |
| total_employees | INT |  |
| Local_employees | INT |  |
| trial_employment | BOOLEAN |  |
| last_call_date | DATETIME |  |
| last_call_result | BIGINT | FK→ result_codes.result_code_id
デフォルト→1 |
| memo | TEXT |  |
| next_call_date | DATETIME |  |

テーブル: call_history

| 列 | 型 | 制約 / 説明 |
|---|---|---|
| call_history_id | BIGINT | PK, AUTO_INCREMENT |
| lead_id | BIGINT | FK → leads.lead_id |
| call_datetime | DATETIME | NOT NULL |
| result_code | BIGINT | FK→ result_codes.result_code_id |
| prev_result_code | BIGINT | FK→ result_codes.result_code_id |
| note | TEXT |  |

テーブル: result_codes

初回マイグレーション時に下記データ挿入

Id:1

Code:1

Label:未架電

Active:1

| 列 | 型 | 制約 / 説明 |
|---|---|---|
| result_call_id | BIGINT | PK, AUTO_INCREMENT |
| code | VARCHAR(50) | 並び順指定に利用 |
| label | VARCHAR(255) |  |
| active | BOOLEAN | NOT NULL DEFAULT 1 |
|  |  |  |
|  |  |  |

6. 機能要件（詳細）

| ID | 機能 | 詳細 |
|---|---|---|
| FR-1 | CSV 取込 | プレビュー → 重複スキップ → 結果JSON返却 |
| FR-2 | リード一覧 | 検索・フィルタ・ソート・ページング |
| FR-3 | リード詳細＋履歴 | 企業情報編集・履歴タブAjax追加 |
| FR-4 | 架電登録 | Ajaxで履歴追加、リード更新 |
| FR-5 | 結果コード管理 | CRUD・無効化, 並び順編集機能追加 |
| FR-6 | ダッシュボード | KPI・推移グラフ・TOP5集計 |
| FR-7 | 再コールリスト | next_call_date ≤ 今日のリード抽出 |
| FR-8 | CSV エクスポート | 検索条件反映したCSV DL |
| FR-9 | 連続架電ナビ | 保存後に次リードへ自動遷移 |

7. 非機能要件

| 項目 | 要件 |
|---|---|
| ユーザー数 | 1（ログイン不要） |
| パフォーマンス | リード5万件／履歴30万件で一覧2秒以内 |
| セキュリティ | オフライン完結・外部通信なし・JARコード署名 |
| バックアップ | 設定画面で MySQL ダンプを zip DL |

8. 開発スケジュール（14 ステップ）

| # | ステップ | Tips / ベストプラクティス |
|---|---|---|
| 1 | GitHub リポジトリ作成 | `main`保護、.gitignore整備 |
| 2 | Spring Boot プロジェクト生成 | Initializrで Web/Thymeleaf/JPA/Flyway/Lombok |
| 3 | Docker 環境構築 | MySQL8 + Adminer, .env で DB変数 |
| 4 | ER 図作成 | Workbench→SQL→Flyway, 主キー/UNIQUE確定 |
| 5 | Flyway 初回 SQL | V1__init.sql にDDL&マスタ |
| 6 | 画面モック | リード一覧/詳細/CSVインポート |
| 7 | エンティティ作成 | JPA注釈 & Flyway同期 |
| 8 | リポジトリ作成 | LeadRepository.findByPhoneNumber |
| 9 | サービス層作成 | importCsv, registerCall |
| 10 | コントローラ層作成 | MVCとREST分離、Validated |
| 11 | Thymeleaf 画面設計 | テーブル行クリック、モーダル編集 |
| 12 | Ajax + JS (架電登録) | fetch POST, 連続ナビ |
| 13 | Dashboard (Chart.js) | 集計JSON, 折線+棒 |
| 14 | 本番デプロイ準備 | mvn package→app.jar, prod設定 |

9. ビルド & 実行手順

1. `docker compose up -d db` で MySQL 起動

2. `mvn clean package` でビルド

3. `java -jar target/leadmgr-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`

4. ブラウザで http://localhost:8080/ を開く

## 10. 受入れテスト

| ID | シナリオ | 期待結果 |
|---|---|---|
| T-01 | CSV 1000行(重複100)取込 | 新規900、スキップ100 |
| T-02 | 一覧→保存→次リード遷移 | 連続ナビ動作 |
| T-03 | 5件目保存 | 完了ダイアログ→一覧復帰 |
| T-04 | データ量大で一覧表示 | 2秒以内 |
| T-05 | ダッシュボード週→月切替 | グラフ・数値更新 |

