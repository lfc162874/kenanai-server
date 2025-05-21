-- 商品分类表
CREATE TABLE IF NOT EXISTS `shop_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(64) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父级分类ID',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `shop_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(128) NOT NULL COMMENT '商品名称',
  `description` varchar(512) DEFAULT NULL COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '商品原价',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `image_url` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `detail` text DEFAULT NULL COMMENT '商品详情',
  `stock` int(11) NOT NULL DEFAULT 0 COMMENT '库存数量',
  `sales` int(11) NOT NULL DEFAULT 0 COMMENT '销售数量',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `shop_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `freight_amount` decimal(10,2) DEFAULT 0.00 COMMENT '运费',
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付方式：1-支付宝，2-微信，3-余额',
  `source_type` tinyint(4) DEFAULT NULL COMMENT '订单来源：1-PC，2-APP，3-小程序',
  `status` tinyint(4) DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-已发货，3-已完成，4-已关闭，5-已取消',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) NOT NULL COMMENT '收货人地址',
  `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单商品表
CREATE TABLE IF NOT EXISTS `shop_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(128) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `product_price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `total_price` decimal(10,2) NOT NULL COMMENT '商品总价',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 购物车表
CREATE TABLE IF NOT EXISTS `shop_cart_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(128) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `quantity` int(11) NOT NULL DEFAULT 1 COMMENT '购买数量',
  `specifications` varchar(512) DEFAULT NULL COMMENT '规格，JSON格式',
  `checked` tinyint(1) DEFAULT 1 COMMENT '是否选中：0-未选中，1-已选中',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 地址表
CREATE TABLE IF NOT EXISTS `shop_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL COMMENT '收货人电话',
  `province` varchar(64) NOT NULL COMMENT '省份',
  `city` varchar(64) NOT NULL COMMENT '城市',
  `district` varchar(64) NOT NULL COMMENT '区/县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `default_status` tinyint(1) DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- 插入示例分类数据
INSERT INTO `shop_category` (`name`, `description`, `parent_id`, `icon`, `sort`, `status`) VALUES
('电子产品', '各类电子产品', 0, 'electronics.png', 1, 1),
('服装鞋帽', '时尚服装鞋帽', 0, 'clothing.png', 2, 1),
('家居用品', '家居生活用品', 0, 'home.png', 3, 1),
('手机', '智能手机与配件', 1, 'phone.png', 1, 1),
('电脑', '笔记本与台式电脑', 1, 'computer.png', 2, 1),
('男装', '男士服装', 2, 'men.png', 1, 1),
('女装', '女士服装', 2, 'women.png', 2, 1),
('厨房用品', '厨房烹饪与餐具', 3, 'kitchen.png', 1, 1);

-- 插入示例商品数据
INSERT INTO `shop_product` (`name`, `description`, `price`, `original_price`, `category_id`, `image_url`, `detail`, `stock`, `sales`, `status`) VALUES
('智能手机Pro', '最新款智能手机，高性能处理器，超长续航', 5999.00, 6999.00, 4, 'phone_pro.jpg', '<p>详细规格描述...</p>', 100, 20, 1),
('轻薄笔记本', '超轻薄笔记本电脑，适合商务办公与学习', 4999.00, 5599.00, 5, 'laptop.jpg', '<p>详细规格描述...</p>', 50, 10, 1),
('男士休闲夹克', '时尚休闲夹克，适合春秋季节穿着', 299.00, 399.00, 6, 'jacket.jpg', '<p>详细规格描述...</p>', 200, 50, 1),
('女士连衣裙', '优雅修身连衣裙，适合多种场合', 399.00, 499.00, 7, 'dress.jpg', '<p>详细规格描述...</p>', 150, 30, 1),
('不锈钢炒锅', '优质不锈钢炒锅，不粘底易清洁', 199.00, 249.00, 8, 'pan.jpg', '<p>详细规格描述...</p>', 80, 15, 1);


-- 插入AI模型和服务类商品
INSERT INTO `shop_product`
(`name`, `description`, `price`, `original_price`, `category_id`, `image_url`, `detail`, `stock`, `sales`, `status`, `create_time`, `update_time`)
VALUES
-- AI大语言模型
('GPT-5 智能助手模型', 'KenanAI最新一代大语言模型，支持多轮对话，理解能力超强，可处理复杂任务。', 9999.00, 12999.00, 1, 'ai_gpt5.jpg', '<p>GPT-5是KenanAI旗下最先进的大语言模型，具有以下特点：</p><ul><li>支持200K上下文窗口</li><li>超强推理和规划能力</li><li>理解和生成多种语言</li><li>精确执行复杂指令</li><li>自然流畅的多轮对话</li></ul>', 50, 12, 1, NOW(), NOW()),

('多模态AI助手', '支持图像、语音、文本多模态输入和输出，适用于内容创作、数据分析等领域。', 7999.00, 9999.00, 1, 'ai_multimodal.jpg', '<p>多模态AI助手可以处理多种信息形式，具有以下优势：</p><ul><li>视觉理解能力强</li><li>语音识别准确率高</li><li>支持图像生成与编辑</li><li>音频转文本、文本转音频</li><li>跨模态内容理解与创作</li></ul>', 100, 25, 1, NOW(), NOW()),

-- AI API服务
('KenanAI API调用包 (100万Tokens)', '100万Tokens的API调用额度，支持所有AI模型接口，可用于应用开发与集成。', 1999.00, 2499.00, 2, 'ai_api_package.jpg', '<p>API调用包特性：</p><ul><li>支持所有KenanAI模型</li><li>灵活的接口调用方式</li><li>详尽的开发文档</li><li>完善的技术支持</li><li>无使用期限限制</li></ul>', 1000, 120, 1, NOW(), NOW()),

('企业级API服务月卡', '面向企业用户的无限调用月卡，适用于高频次、大批量AI应用场景。', 19999.00, 29999.00, 2, 'ai_api_enterprise.jpg', '<p>企业级服务优势：</p><ul><li>无调用次数限制</li><li>独立服务器资源</li><li>优先响应队列</li><li>7×24小时技术支持</li><li>每月技术顾问服务</li></ul>', 30, 8, 1, NOW(), NOW()),

-- AI应用工具
('智能文档助手', '自动处理文档总结、翻译、格式转换等任务，提高文档处理效率。', 499.00, 699.00, 3, 'ai_doc_assistant.jpg', '<p>文档助手功能：</p><ul><li>一键文档摘要</li><li>多语言翻译</li><li>智能格式转换</li><li>文档内容分类</li><li>关键信息提取</li></ul>', 200, 45, 1, NOW(), NOW()),

('AI写作工具套件', '集成多种AI写作辅助功能，适合内容创作者、作家、学生和专业人士使用。', 699.00, 899.00, 3, 'ai_writing_tools.jpg', '<p>写作工具包含：</p><ul><li>标题生成器</li><li>内容拓展工具</li><li>风格转换器</li><li>语法检查与优化</li><li>多种写作模板</li></ul>', 150, 32, 1, NOW(), NOW()),

-- AI教育课程
('AI基础入门课程', '从零开始学习AI基础知识，包含视频课程、练习和项目实践。', 299.00, 399.00, 4, 'ai_course_basic.jpg', '<p>课程内容：</p><ul><li>AI发展历史与现状</li><li>机器学习基础概念</li><li>神经网络入门</li><li>AI工具使用实践</li><li>项目案例分析</li></ul>', 500, 89, 1, NOW(), NOW()),

('AI高级开发者课程', '针对开发者的高级AI应用开发课程，教授如何构建和部署实用AI系统。', 1299.00, 1599.00, 4, 'ai_course_advanced.jpg', '<p>高级课程亮点：</p><ul><li>深度学习模型训练</li><li>AI系统架构设计</li><li>大规模模型优化技术</li><li>AI产品开发全流程</li><li>实际项目指导</li></ul>', 200, 42, 1, NOW(), NOW()),

-- AI会员服务
('KenanAI会员月卡', '每月享受所有AI服务特权，包括优先使用最新模型和功能。', 99.00, 129.00, 5, 'ai_membership_monthly.jpg', '<p>会员福利：</p><ul><li>优先使用新功能</li><li>每月500万Tokens额度</li><li>专属客服支持</li><li>会员专属活动</li><li>AI课程折扣</li></ul>', 9999, 256, 1, NOW(), NOW()),

('KenanAI会员年卡', '年度会员服务，比月卡更经济实惠，享有更多专属特权和礼品。', 999.00, 1399.00, 5, 'ai_membership_yearly.jpg', '<p>年卡专属权益：</p><ul><li>所有月卡福利</li><li>年度AI使用报告</li><li>专属定制AI模型</li><li>高级技术研讨会邀请</li><li>新产品测试资格</li></ul>', 5000, 120, 1, NOW(), NOW()),

-- AI定制服务
('AI定制模型服务', '根据企业需求定制专属AI模型，包含需求分析、模型训练和部署全流程。', 49999.00, 59999.00, 6, 'ai_custom_model.jpg', '<p>定制服务包括：</p><ul><li>专业需求分析</li><li>数据采集与处理</li><li>模型训练与调优</li><li>系统集成与部署</li><li>后续维护支持</li></ul>', 10, 3, 1, NOW(), NOW()),

('AI数字人定制', '打造专属企业数字人形象，可用于客服、讲解、培训等多种场景。', 29999.00, 39999.00, 6, 'ai_digital_human.jpg', '<p>数字人特性：</p><ul><li>形象高度定制</li><li>语音与表情自然</li><li>知识库定制导入</li><li>多平台兼容部署</li><li>持续更新优化</li></ul>', 20, 5, 1, NOW(), NOW()),

-- AI语音服务
('AI语音合成服务包', '超自然AI语音合成技术，支持多语言、多音色，适用于配音、有声书等场景。', 1299.00, 1599.00, 7, 'ai_voice_synthesis.jpg', '<p>语音合成特点：</p><ul><li>50+专业音色</li><li>20+语言支持</li><li>情感语调调整</li><li>批量文本处理</li><li>API接口支持</li></ul>', 300, 68, 1, NOW(), NOW()),

('AI语音识别系统', '专业级语音识别系统，支持实时转录、多人对话区分，准确率高达98%。', 2999.00, 3599.00, 7, 'ai_voice_recognition.jpg', '<p>系统优势：</p><ul><li>高准确率识别</li><li>多场景噪声过滤</li><li>专业术语库支持</li><li>方言口音识别</li><li>实时字幕生成</li></ul>', 150, 35, 1, NOW(), NOW()),

-- AI工具集合
('AI创意设计套件', '集成多种AI设计工具，包括logo生成、海报设计、UI原型等功能。', 899.00, 1199.00, 8, 'ai_design_toolkit.jpg', '<p>设计套件内容：</p><ul><li>AI Logo生成器</li><li>海报智能设计</li><li>UI界面生成</li><li>配色方案推荐</li><li>图像风格转换</li></ul>', 200, 47, 1, NOW(), NOW()),

('AI数据分析平台', '智能数据分析平台，自动处理数据清洗、分析、可视化和报告生成。', 3999.00, 4999.00, 8, 'ai_data_analysis.jpg', '<p>平台功能：</p><ul><li>自动数据清洗</li><li>智能异常检测</li><li>预测分析模型</li><li>可视化报表生成</li><li>洞察自动提取</li></ul>', 100, 22, 1, NOW(), NOW());





-- 首先添加一个"AI产品与服务"顶级分类
INSERT INTO `shop_category`
(`name`, `description`, `parent_id`, `icon`, `sort`, `status`, `create_time`, `update_time`)
VALUES
    ('AI产品与服务', 'KenanAI提供的各类人工智能产品和服务', 0, 'ai_main.png', 1, 1, NOW(), NOW());

-- 获取刚插入的AI产品与服务分类的ID（假设为9，实际使用时可能需要调整）
SET @ai_parent_id = 9;

-- 添加AI子分类
INSERT INTO `shop_category`
(`name`, `description`, `parent_id`, `icon`, `sort`, `status`, `create_time`, `update_time`)
VALUES
-- AI模型类产品
('AI模型', '各类AI大模型与多模态模型', @ai_parent_id, 'ai_model.png', 1, 1, NOW(), NOW()),

-- API服务
('AI API服务', 'AI接口调用与API服务包', @ai_parent_id, 'ai_api.png', 2, 1, NOW(), NOW()),

-- AI应用工具
('AI应用工具', '各种AI辅助工具与应用软件', @ai_parent_id, 'ai_tool.png', 3, 1, NOW(), NOW()),

-- AI教育课程
('AI教育课程', 'AI相关培训与教育课程', @ai_parent_id, 'ai_course.png', 4, 1, NOW(), NOW()),

-- AI会员服务
('AI会员服务', 'KenanAI会员订阅与特权服务', @ai_parent_id, 'ai_membership.png', 5, 1, NOW(), NOW()),

-- AI定制服务
('AI定制服务', '企业级AI定制开发服务', @ai_parent_id, 'ai_custom.png', 6, 1, NOW(), NOW()),

-- AI语音服务
('AI语音服务', '语音合成与识别服务', @ai_parent_id, 'ai_voice.png', 7, 1, NOW(), NOW()),

-- AI工具集合
('AI工具集', '综合AI工具包与解决方案', @ai_parent_id, 'ai_toolkit.png', 8, 1, NOW(), NOW());

-- 获取各子分类ID（假设为10-17，实际使用时需要调整）
SET @ai_model_id = 10;
SET @ai_api_id = 11;
SET @ai_tool_id = 12;
SET @ai_course_id = 13;
SET @ai_membership_id = 14;
SET @ai_custom_id = 15;
SET @ai_voice_id = 16;
SET @ai_toolkit_id = 17;

-- 添加AI模型的子分类
INSERT INTO `shop_category`
(`name`, `description`, `parent_id`, `icon`, `sort`, `status`, `create_time`, `update_time`)
VALUES
    ('通用大语言模型', '各类通用型大语言模型产品', @ai_model_id, 'ai_llm.png', 1, 1, NOW(), NOW()),
    ('多模态AI模型', '支持图像、语音、文本等多种模态的AI模型', @ai_model_id, 'ai_multimodal.png', 2, 1, NOW(), NOW()),
    ('垂直领域模型', '针对特定领域优化的专业AI模型', @ai_model_id, 'ai_vertical.png', 3, 1, NOW(), NOW());

-- 添加API服务的子分类
INSERT INTO `shop_category`
(`name`, `description`, `parent_id`, `icon`, `sort`, `status`, `create_time`, `update_time`)
VALUES
    ('按量计费API', '按使用量付费的API服务', @ai_api_id, 'ai_pay_as_you_go.png', 1, 1, NOW(), NOW()),
    ('企业级API套餐', '面向企业用户的高性能API服务包', @ai_api_id, 'ai_enterprise_api.png', 2, 1, NOW(), NOW());

-- 添加AI工具的子分类
INSERT INTO `shop_category`
(`name`, `description`, `parent_id`, `icon`, `sort`, `status`, `create_time`, `update_time`)
VALUES
    ('文本处理工具', '文档摘要、翻译、写作辅助等工具', @ai_tool_id, 'ai_text_tool.png', 1, 1, NOW(), NOW()),
    ('图像处理工具', '图像生成、编辑、风格转换等工具', @ai_tool_id, 'ai_image_tool.png', 2, 1, NOW(), NOW()),
    ('开发者工具', '面向开发者的AI辅助工具', @ai_tool_id, 'ai_dev_tool.png', 3, 1, NOW(), NOW());

-- 添加AI教育课程的子分类
INSERT INTO `shop_category`
(`name`, `description`, `parent_id`, `icon`, `sort`, `status`, `create_time`, `update_time`)
VALUES
    ('入门课程', 'AI基础知识与入门课程', @ai_course_id, 'ai_basic_course.png', 1, 1, NOW(), NOW()),
    ('进阶课程', 'AI高级应用与开发课程', @ai_course_id, 'ai_advanced_course.png', 2, 1, NOW(), NOW()),
    ('专业认证', 'AI相关专业技能认证', @ai_course_id, 'ai_certification.png', 3, 1, NOW(), NOW());