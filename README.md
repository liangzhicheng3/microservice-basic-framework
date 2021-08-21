# microservice-basic-framework

微服务基础框架搭建

技术结构

    SpringCloudAlibaba
    SpringCloud Stream RabbitMQ
    SpringBoot Data Elasticsearch
    Redis
    Shiro
    Mybatis Plus

项目结构

    basic-core(基础核心模块)
    entity-core(实体类核心模块)
    user-server(用户服务)
    area-server(地区服务)
    product-server(商品服务)
    order-server(订单服务)
    elastic-server(elasticsearch搜索服务)
    pay-server(支付服务)
    gateway-server(网关服务)
    provider-server(供应者服务)
    consumer-server(消费者服务)

启动

    修改各服务中的appliction.yml配置，包括数据库连接,RabbitMQ连接,Elasticsearch连接,Redis连接等
    需启动redis客户端(集成一些技术需要用到redis)
    需启动Nacos服务端(将服务注册到Nacos服务中心中;用到Nacos配置中心)
    需启动RabbitMQ服务端(provider-server供应者服务与consumer-server消费者服务集成用到)
    需启动Elasticsearch PC端与服务端(PC端:Elasticsearch Head;服务端:Elasticsearch7.13.3)
    启动类:各服务中Application应用启动类

swagger接口文档

    集成SpringBoot Knife4j + Gateway网关
    访问路径：http://localhost:9000/doc.html

    根据自己项目所需的端口和命名方式配置appliction.yml配置中的值


代码生成

    在basic-core基础核心模块中,找到devtool->generator->CodeGenerator类,修改数据库连接\路径等,直接运行generate()函数(详细注释说明类中有写)
    resources下提供了一个用于测试的表test_user.sql
