## 水果库存管理系统V2.0（Fruit-inventory-system-2.0）

在水果库存管理系统V1.0的基础上，使用 JavaWeb 相关技术编写的一个 BS 架构水果库存管理系统，相较V1.0版本实现了一些更为复杂的功能。

V1.0版本地址：[Fruit-inventory-system-1.0](https://github.com/lixin-jnu/Fruit-inventory-system)；

#### 1 开发环境

1. jdk-8.0；
2. Tomcat-8.0.42;
3. mysql-8.0；
4. thymeleaf-3.0;
5. Html5+Css3+Javascript；
6. IDEA-2021.3.2；
7. git-2.35.1；

#### 2 实现功能

1. 查看水果库存列表：
    - 分页功能；
    - 首尾页；
    - 上下页。
2. 修改已有水果库存信息；
3. 添加新水果库存信息；
4. 根据关键字“模糊”查询特定水果库存信息；
5. 水果下架；

#### 3 数据库设置

**fruitdb**数据库中的**fruit**表；

- fid：水果编号（**主键**、自增），int(0)；
- fname：水果名称，varchar(255)；
- price：水果价格，int(0)；
- fcount：水果库存，int(0)；
- remark：备注，varchar(255)。