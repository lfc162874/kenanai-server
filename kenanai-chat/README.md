## ChatClient 是什么？如何使用？

### 一、ChatClient 是什么？

**ChatClient 是一个用于与大语言模型（LLM）通信的高级封装工具，它提供了一种更简单、更流畅的编程接口（Fluent API）。**

相比底层的原子 API（如 `ChatModel`、`Message`、`ChatMemory` 等），`ChatClient` 更像是一个“自动管家”，可以帮我们协调和集成多个组件（如提示词模板、记忆模块、模型调用、输出解析器、RAG 检索等），从而减少重复性代码，提升开发效率。

---

### 二、为什么使用 ChatClient？

如果使用底层原子 API，开发者通常需要：

- 手动构建提示词（Prompt）
- 管理对话记忆（ChatMemory）
- 明确调用的模型（ChatModel）
- 自行解析模型输出
- 串联 RAG 所需的嵌入模型和向量数据库

这虽然灵活，但也非常繁琐。而 ChatClient 将这些细节封装起来，使你能更专注于业务逻辑。

---

### 三、类比理解：点外卖 vs 自己做饭

| 类比情景     | 对应 API               | 说明                         |
|--------------|------------------------|------------------------------|
| 自己做饭     | 原子 API（ChatModel 等） | 自由灵活，但步骤复杂          |
| 点外卖       | ChatClient             | 简单快捷，自动协调完成各个部分 |

---

### 四、代码示例对比

#### 使用原子 API（底层方式）

```java
ChatModel model = new OpenAIChatModel("your-key");
ChatMemory memory = new InMemoryChatMemory();
PromptTemplate template = new PromptTemplate("你是谁？");

String userInput = "你好";

Message prompt = template.apply(userInput);
List<Message> messages = memory.getMessages();
messages.add(prompt);

ChatResponse response = model.call(messages);
memory.add(response.getMessage());

System.out.println(response.getMessage().getContent());
```
### 禁用 ChatClient.Builder bean 的自动配置
设置属性 spring.ai.chat.client.enabled=false

## 什么是 Embedding（嵌入向量）？

### 一、定义

**Embedding 是将文本、图片、音频等信息转化为一组数字向量的技术，这组向量可以用于机器理解语义之间的关系。**

通俗讲：

> 原本人类能理解“苹果”和“香蕉”都属于“水果”，但计算机只看得到字符。**Embedding 的作用就是把这些词变成“数字”，而且这些数字之间的距离能体现出它们的语义相似度。**

---

### 二、Embedding 的作用

将文本转成向量后可以用于：

- **语义搜索（Semantic Search）**
- **相似度匹配（文本推荐、问答系统）**
- **分类、聚类**
- **RAG（检索增强生成）中的向量检索部分**

---

### 三、举例说明

假设我们有以下三个句子：

1. “我想买一台笔记本电脑”
2. “哪种笔记本电脑性能最好？”
3. “今天天气真好”

假设将它们分别转成 3 维向量：

| 句子 | 向量表示 |
|------|----------|
| 我想买一台笔记本电脑 | `[0.81, 0.34, 0.65]` |
| 哪种笔记本电脑性能最好？ | `[0.79, 0.36, 0.66]` |
| 今天天气真好 | `[0.12, 0.88, 0.21]` |

通过计算向量之间的**余弦相似度**或**欧几里得距离**，可以发现：

- 句子 1 和 2 的向量距离很近 → 语义相似
- 句子 3 的向量与前两个差距较大 → 语义不相关

---

### 四、在大语言模型（LLM）中的应用

#### 1. 检索增强生成（RAG）

- 将大量文档进行嵌入 → 存入向量数据库（如 FAISS、Milvus）
- 用户提问 → 也生成向量
- 在向量数据库中查找语义最接近的内容
- 将这些内容提供给大模型，生成更精准的回答

#### 2. 向量搜索

- 不再使用关键词匹配
- 而是通过语义向量匹配，找出意思相近的内容
- 适合智能问答、推荐系统等应用场景

---

### 五、类比理解：词转向量 ≈ 地图定位

- 每个词或句子是地图上的一个点
- Embedding 相当于为它赋予一个“坐标”
- 坐标越近，表示它们在“语义空间”中越相近

---

### 六、Java 示例代码（以 OpenAI 为例）

```java
EmbeddingModel model = new OpenAIEmbeddingModel("your-api-key");
List<String> texts = List.of("我想买电脑", "哪款电脑好", "天气晴朗");

List<List<Double>> vectors = model.embedAll(texts);

// vectors 就是文本对应的嵌入向量，可用于相似度匹配或向量搜索
```
当然可以，以下是完整的 Markdown 文档格式内容，适合直接放入你的 `.md` 文件中使用：

````markdown
## 什么是 Function Calling（函数调用）？

### 一、定义

**Function Calling 是指大语言模型可以根据上下文，自动决定调用你提供的函数（API）并生成所需参数，从而实现“说话 + 执行任务”的能力。**

传统模型只能“说”，Function Calling 让模型“能说会做”。

---

### 二、为什么需要 Function Calling？

在实际应用中，我们希望模型不仅能回答问题，还能：

- 查天气、查汇率、查快递（→ 外部 API）
- 查库存、查价格、查订单（→ 后端服务）
- 操作系统、控制设备（→ 智能体 Agent）

Function Calling 就是连接语言理解和实际操作的桥梁。

---

### 三、工作机制（流程）

1. **你定义函数**（名字、参数结构、描述）
2. **用户提问**（自然语言）
3. **模型判断是否需要调用函数**
4. **模型生成函数名 + 参数（JSON 结构）**
5. **后端实际调用函数并获取结果**
6. **返回结果给模型 → 模型继续生成最终响应**

---

### 四、示例

#### 定义一个函数

```json
{
  "name": "get_weather",
  "description": "获取城市天气",
  "parameters": {
    "type": "object",
    "properties": {
      "city": { "type": "string", "description": "城市名称" },
      "date": { "type": "string", "description": "日期，如 2025-05-21" }
    },
    "required": ["city", "date"]
  }
}
````

#### 用户输入：

```
请告诉我北京明天的天气？
```

#### 模型生成调用请求：

```json
{
  "function_call": {
    "name": "get_weather",
    "arguments": {
      "city": "北京",
      "date": "2025-05-22"
    }
  }
}
```

#### 后端返回结果：

```json
{
  "weather": "多云",
  "temperature": "26°C"
}
```

#### 模型最终回复：

```
明天北京多云，最高气温26°C。
```

---

### 五、常见应用场景

| 场景          | 说明              |
| ----------- | --------------- |
| 查询类应用       | 查天气、查汇率、查新闻等    |
| 数据处理        | 查数据库、调用业务系统     |
| 系统控制        | 控制硬件设备、操作系统     |
| 智能 Agent 系统 | 多函数协同处理复杂任务     |
| 插件化工具       | AI 插件/助手、扩展执行能力 |

---

### 六、形象比喻

| 类型               | 类比                |
| ---------------- | ----------------- |
| 普通大模型            | 会说话的客服            |
| Function Calling | 会说话 + 会调系统的 AI 助理 |

---

### 七、优点与挑战

| 优点       | 挑战               |
| -------- | ---------------- |
| 提高模型实用性  | 需要规范函数定义（schema） |
| 支持复杂业务逻辑 | 参数/类型校验复杂        |
| 可与真实系统打通 | 权限管理、安全控制        |

---

### 八、技术关键词

* OpenAI Function Calling
* LangChain（Tools / Functions）
* Semantic Kernel Plugins
* 智能体（Agent）系统
* JSON Schema + 函数描述 + 动态执行

---

### 九、总结

Function Calling 是大语言模型从“语言工具”走向“通用智能体”的关键一步。它通过语言理解 + 参数推理，实现对外部函数的智能调用，从而推动 AI 在搜索、推荐、问答、自动化等领域落地应用。

```

如果你有具体用到某个 SDK（如 OpenAI Java SDK、LangChain4j、Spring Boot 接入方案等），我也可以为你提供代码示例和说明部分。是否需要继续补充？
```
