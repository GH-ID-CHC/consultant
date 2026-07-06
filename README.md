# 项目总结

## 项目概述

本项目是一个 **AI 志愿填报顾问系统**，通过 AI 助手的形式与用户进行对话交互。系统以"传智教育专业 AI 志愿填报顾问"为角色定位，能够为用户提供院校查询、专业推荐、志愿填报指导等服务，并支持通过 AI 对话的方式完成预约服务的增删改查。

> 对接Langchain4J，实现将向量模型将文本转换为向量；多次大模型对话

---

## 一、功能实现

### 1. AI 智能对话

- 基于大语言模型（LLM）的多轮对话能力，支持自然语言交互
- 通过 `system.txt` 定义 AI 角色（专业 AI 志愿填报顾问），包含 11 项核心能力描述与行为规范
- 支持 **流式响应（SSE）**，前端实时展示打字机效果（约 50 字符/秒）
- 支持中断响应（Abort），用户可随时停止 AI 输出

### 2. RAG 知识库检索

- 基于 **RAG（Retrieval-Augmented Generation）** 技术实现知识增强问答
- 将 PDF 文档解析后分块向量化，存入向量数据库
- 用户提问时，系统自动检索最相关的知识片段（Top-3，最低相似度 0.5），注入对话上下文，提升回答准确性

### 3. AI 工具调用（Function Calling）

通过 LangChain4j 的 `@Tool` 注解，将业务接口暴露给 AI 模型调用：

- **预约登记**（`addReservation`）：AI 引导用户提供姓名、性别、电话、沟通时间、省份、预估分数 6 项信息后，自动创建预约记录
- **预约查询**（`findReservation`）：用户提供手机号，AI 查询并反馈预约详情

以上操作不暴露传统 REST CRUD 接口，完全通过 AI 对话驱动，实现"对话即操作"的交互模式。

### 4. 会话管理

- 每个前端会话分配唯一 `memoryId`，实现多轮对话上下文隔离
- 对话历史持久化到 Redis，TTL 为 1 天，单会话最多保留 20 轮对话
- 支持"新建会话"功能，切换 `memoryId` 即可开启全新对话

### 5. 前端交互

- 自包含的单页应用（SPA），Vue 3 + Tailwind CSS 构建
- 支持 **暗色模式** 切换
- 自适应文本框，流式渲染打字动画
- 一键新建会话、中止回复

---

## 二、技术栈

### 后端核心

| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17 | 开发语言 |
| **Spring Boot** | 3.2.5 | 应用框架 |
| **Maven** | — | 项目构建与依赖管理 |
| **MyBatis** | 3.0.3 | ORM 框架，注解式 SQL 映射 |
| **MySQL** | — | 业务数据库（预约表） |
| **Lombok** | — | 简化 POJO 代码 |

### AI / LLM 相关

| 技术 | 说明 |
|------|------|
| **LangChain4j** | 核心 AI 框架（v1.0.1-beta6），提供 AI Service、Tool Calling、RAG、Chat Memory 等能力 |
| **阿里云 DashScope（Qwen 3.7 Max）** | 大语言模型，通过 OpenAI 兼容接口调用 |
| **Ollama + bge-m3** | 向量嵌入模型，自部署于内网服务器（`192.168.0.128:11434`）主要作用是将文本转换为向量 |
| **langchain4j-easy-rag** | RAG 简化封装，自动完成文档加载、分块、向量化流程 |
| **Apache PDFBox** | PDF 文档解析（通过 `langchain4j-document-parser-apache-pdfbox`） |
| **Redis** | 向量存储（RedisEmbeddingStore）+ 对话记忆存储 |
| **Spring WebFlux + Project Reactor** | 流式响应，`Flux<String>` 实现 SSE 推送 |

### 前端

| 技术 | 说明 |
|------|------|
| **Vue 3** | 前端框架（CDN 引入） |
| **Tailwind CSS** | 样式框架（CDN 引入） |
| **Fetch API + ReadableStream** | 流式读取 AI 响应并逐字渲染 |

---

## 三、系统架构

```
┌─────────────────────────────────────────────────────┐
│                   前端 (Vue 3 SPA)                    │
│              index.html (static 资源)                 │
└────────────────────────┬────────────────────────────┘
                         │ HTTP (SSE Stream)
                         ▼
┌─────────────────────────────────────────────────────┐
│              Spring Boot (ChatController)             │
│          /chat (同步)    /chat2 (流式)                │
└────────────────────────┬────────────────────────────┘
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
┌──────────────┐ ┌─────────────┐ ┌──────────────┐
│  Consultant  │ │   Content   │ │ Reservation  │
│  Service     │ │  Retriever  │ │    Tool      │
│ (@AiService) │ │   (RAG)     │ │  (Function   │
│              │ │             │ │   Calling)   │
└──────┬───────┘ └──────┬──────┘ └──────┬───────┘
       │                │               │
       ▼                ▼               ▼
┌──────────────┐ ┌─────────────┐ ┌──────────────┐
│ DashScope    │ │   Redis     │ │    MySQL     │
│ Qwen 3.7 Max │ │ 向量存储    │ │  预约数据    │
│ (LLM)        │ │ 会话记忆    │ │              │
└──────────────┘ └──────┬──────┘ └──────────────┘
                        │
                 ┌──────▼──────┐
                 │   Ollama    │
                 │  bge-m3     │
                 │ (自部署)    │
                 └─────────────┘
```

---

## 四、数据库设计

**数据库：** `volunteer`  
**表：** `reservation`（预约信息）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK, AUTO_INCREMENT) | 主键 |
| name | VARCHAR(50) | 考生姓名 |
| gender | VARCHAR(2) | 考生性别 |
| phone | VARCHAR(20) | 考生手机号 |
| communication_time | DATETIME | 沟通时间 |
| province | VARCHAR(32) | 考生省份 |
| estimated_score | INT | 预估分数 |

---

## 五、配置文件说明

| 文件 | 说明 |
|------|------|
| `application.yml` | Spring Boot 主配置（模型参数、Redis、MySQL、Ollama） |
| `system.txt` | AI 系统提示词（角色定义 + 能力描述 + 行为规范） |
| `init.sql` | 数据库初始化脚本 |
| `content/*.pdf` | RAG 知识库文档（放于 classpath 下自动加载） |

---

## 六、已知问题

### Ollama 向量模型超时

> Error while extracting response for type [java.lang.String] and content type [application/octet-stream]

**原因：** Ollama 自部署的 `bge-m3` 向量模型性能较差，处理请求时容易超时。

**应对措施：**
- 已将 Ollama 超时时间设置为 **600 秒（10 分钟）**（`application.yml` 中 `timeout: 600000`）
- 建议升级硬件或换用性能更强的嵌入模型以从根本上解决此问题
