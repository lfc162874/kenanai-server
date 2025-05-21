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
