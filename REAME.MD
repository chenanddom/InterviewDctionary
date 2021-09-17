# 面试题汇总
## 分布式唯一ID的生成方案有哪些？
| 方案 | 描述 | 优点 | 缺点 |
| :------| :------: | :------ | :------ |
| UUID | UUID是通用的唯一标识的缩写,它的目的是让分布式系统中的所<br>有元素都唯一的辨识信息，而不是需要中央控制器来指定唯一标<br>识| 1.降低了全军节点的压力，是的主键生成的速度更加的快<br>2.生成的主键全局唯一<br>3.跨服务合并方便 |1. UUID占用16个字符，空间占用比较大<br>2.不是递增性的，数据写入IO随机性很大，且索引效率下降 |
| 数据库主键自增 | MySQL数据库设置主键且主键自动增长 | 1. INT和BIGINT类型占用空间较小<br>2.主键自动增长，IO写入连续性好<br>3.数字类型查询速度优于字符串 |1. 并发性能不高，受限于数据库性能，<br>2.分库，分表，需要改造，复杂<br>3.自增，数据和数据量泄露 |
|  Redis自增 | Redis计数器，原子性自增  | 使用内存，并发性好  |1. 数据丢失，2.自增：数据量泄露  |
|雪花算法| 雪花算法是分布式ID的经典解决方案| 1. 不依赖外部组件，<br>2.性能好 | 时钟回拨|

```text
此处主要推荐使用雪花算法来作为全局唯一ID的方案，雪花算法的全局唯一ID主要有一下几个部分组成：
1. 符号位占1位
2. 时间戳占41位
3. 随机ID占10位
4. 序列号占12位，一毫秒可以生产4095个ID
```

## 使用分布式锁的场景
```text
1. 系统是一个分布式系统，集群，java自带的锁在这种情况下无法处理这个问题
2. 操作共享资源，比如库里唯一的用户数据
3. 同步访问，即多个线程操作共享资源
```

### 分布式锁的解决方案
```text
1. Redis分布式锁，是基于Redis做扩展开发，setnx key vakue expire 
 如果自己写需要考虑锁的时效性(程序突然挂了，但是锁没释放)，锁的设置和过期时间要是原子性的，watchdog(保证不会因为程序执行太长)

2. 基于zookeeper，临时节点，顺序节点

3. 基于数据库，比如MySql主键唯一索引的唯一性
```

### Redis做分布式锁死锁的情况，如何解决
```text
情况1. 加了锁，但是没有加释放锁的操作，如delete key

情况2. 加锁后，程序还没有执行释放锁，程序挂了，需要使用key过期时间
```

### 计数器算法
```text
技术器算法，是指在指定的时间周期内累加访问的次数，达到设定的阈值时，触发的限流策略。下一个时间周期 进行访问，
访问的次数清零，这个算法无论在三级还是分布式环境下实现都非常地简单，使用redis的incr原子自增性，再结合key的过期
时间，即可轻松的实现。
```

### 滑动窗口算法
```text
为了解决计数器算法临界值的问题，发明了滑动窗口算法。在TCP网络通信协议中，就采用滑动时间窗口算法来解决网络拥堵的问题。、
滑动时间窗口将计数器算法中的实际周期切分成多个小的时间窗口，分贝在每个小的时间窗口中记录访问次数，然后根据时间将窗口往前滑动并删除国企的小
时间窗口。最终只需要统计滑动窗口范围内的小时间串口的总的请求的即可。
阿里的sentinel就是使用滑动时间窗口的例子

```
[sentinel](./files/sentinel-dashboard-1.8.2.jar)

启动脚本
```text
java -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard-1.8.2.jar
```


## 设计微服务时遵守的原则
```text
1. 单一职责原则:让每个服务独立，有界限的工作，每个服务只关注自己的业务。做到高内聚。

2. 服务自治原则:服务自治原则:每个服务都要做到独立的开发，独立测试，独立构建，独立部署，独立运行。与其他服务进行解耦

3. 轻量级的通信原则:让每个服务之间的调用轻量级，并且能够跨平台，跨语言。比如RESTful风格。利用消息队列进行通信等。

4. 粒度进化原则:对每个服务的粒度把控，其实没有统一的标准，这个的结合我们解决的具体业务问题，不要过度的设计
```


## 2PC
### 2PC提交的缺点
```text
1. 同步阻塞问题。执行的 过程中，所有参与者节点都是事务阻塞型的。当参与者占有公共资源和的时候，其他第三方节点访问公共资源不得不处于
阻塞状态。(1pc 准备节点，只是执行下SQL，而不提交并且占用数据库连接资源)

2. 单点问题。由于协调者的作用非常重要。一旦协调者挂掉了，参与者会一直阻塞下。尤其使在第二阶段，协调者发生故障，那么所有的参与者都
处于锁定状态中，而无法继续完成事务操作。(如果协调者挂掉，可以重新选举一个协调者，但是无法解决因为协调者当即导致的参与者处于阻塞状
态的问题)


```






## MySQL


### B tree与B+tree
```text
BTree又叫多路平衡搜索树，一颗m叉的BTree特征如下:
1. 书中每个节点最多包含m个孩子
2. 除根节点之外，每个节点至少又ceil(m/2)个孩子
3. 若根节点不是叶子节点，则至少又两个孩子
4. 所有的叶子节点都再同一层.
5.每个非叶子节点有n个key与n+1个指针组成,其中ceil(m/2)<=n<=m-1
------------------------------------------------------------------

B+tree是Btree的变种，B+tree的区别:
1. n叉B+tree最多含有n个key,而Btree最多含有n-1个key
2. B+tree的叶子节点保存所有的key信心，依key大小顺序排列
3. 所有的非叶子节点都可以看作是key的所以部分


```

### 索引的分类
```text
1. 主键索引
一个表的主键所谓索引
2. 单值索引
即一个索引只包含单个列，一个表可以包含多个单值索引
3. 唯一索引
索引的值必须是唯一的，可以为空值

4. 符合索引
复合索引:即一个索引包含多个列
```

### 索引的语法
```text
1. 创建表结构的时候指定索引
CREATE TABLE person(
id BIGINT PRIMARY KEY,
`name` VARCHAR(128) NOT NULL,
UNIQUE INDEX(`name`)
);


2. 创建好表之后再去创建索引
ALTER TABLE `person` ADD FULLTEXT idx_name(`name`);
```

### 索引设计得原则
```text
1. 对查询频次比较高的，且数据量比较达的表简历索引
2. 索引字段的选择，最佳的列应该是从where子句的条件中提取，如果where子句中的组合比较多，那莪应该提挑选最常用的，过滤效果最好的组合。
3. 使用唯一索引，区分度越高，使用索引的效率越高。
4. 索引可以有效的提升查询数据的效率，但是索引数据列不是多多益善，索引越多，维护索引的待加自然也是越大。对于插入，更新，删除等DML
操作比较频繁的表来说，索引过多，会引入相当高的维护代价，降低DML操作的效率，增加响应操作的时间消耗。另外索引过多的话，MYSQL会犯选择
困难症。
5. 使用短索引，索引创建之后也是使用硬盘来存储的，因此提升索引方位的I/O效率，也可以提升欧洲那个题的访问效率，如果索引的长度比较短，那么
相同的磁盘可以存储更多的索引，相应的可以提升MySQL访问索引的I/O效率。
6. 利用最左前缀

```

### 存储过程
```text
存储过程和函数是实现警告编辑并存储在数据库中的一段SQL语句的集合，调用存储过程和函数可以简化应用开发人员的很多工作，减少数据在数据库
和应用服务器之间的传输，对于提高数据处理的效率是有好处的。
存储函数:是一个有返回值的过程
存储过程:时一个没有返回值的函数

```
```sql
DELIMITER $ #此处需要些存储过程的分隔符
CREATE PROCEDURE p_name()
BEGIN	
SELECT 'hello mysql';#mysql默认的分号结束分隔符
END $ #提示sql执行分隔符
DELIMITER ;

-- 调用存储过程
CALL p_name();
-- 查询存储过程
SELECT NAME FROM mysql.`proc`;

SHOW PROCEDURE STATUS;

-- 查看存储过程的定义
SHOW CREATE PROCEDURE p_name;


-- 删除存储过程
DROP PROCEDURE p_name;
```

### 触发器
```text
触发器市与表有关的数据库对象，指在insert/update/delete之前或者之后，出发并执行定义中定义的SQL语句集合。触发器的这种特征可以协助应用在
数据库端确保数据的完整性，日志记录，数据校验等操作。
```
使用别名OLD和NEW来引用触发器中发生变化的记录内容，这与其他的数据库相似。现在触发器还指支持行级出发，不支持语句触发。

| 触发器类型 | NEW和OLD的使用 |
| :------| :------: |
|INSERT型触发器|NEW表示将要或者已经新增的数据|
|UPDATE型触发器|OLD表示修改之前的数据，NEW表示将要或者已经修改后的数据|
|DELETE型触发器|OLD表示将要或者已经删除的数据|

#### 触发器创建
```sql
create trigger trigger_name
before/after insert/update/delete
on table_name
[for each row] -- 触发行级触发器
begin


end $



CREATE TABLE emp_logs(
id INT(11) PRIMARY KEY AUTO_INCREMENT,
operation VARCHAR(32) NULL COMMENT '操作类型，insert/update/delete',
operate_time DATETIME NOT NULL COMMENT '操作时间',
operate_id INT(11) NOT NULL COMMENT '操作表id',
operate_params VARCHAR(500) COMMENT '操作参数'
);



CREATE TABLE emp(id INT(11) PRIMARY KEY AUTO_INCREMENT,
`name` VARCHAR(32),
age INT,
salary DECIMAL
);




DELIMITER $
CREATE TRIGGER emp_insert_trigger
AFTER INSERT
ON emp
FOR EACH ROW -- 申明是行级触发器
BEGIN
INSERT INTO emp_logs(id ,operation,operate_time,operate_id,operate_params)VALUES(NULL,"insert",NOW(),new.id,CONCAT(
'after insert(id:',new.id,',name:',new.name,',age:',new.age,',salary:',new.salary));
END $
DELIMITER ;

INSERT INTO emp(id,`name`,`age`,salary)VALUES(NULL,'张三',22,23000);

SELECT *FROM emp_logs;



DELIMITER $
CREATE TRIGGER emp_update_trigger
AFTER UPDATE
ON emp
FOR EACH ROW
BEGIN
INSERT INTO emp_logs(id ,operation,operate_time,operate_id,operate_params)VALUES(NULL,"update",NOW(),new.id,CONCAT(
'before update(id:',old.id,',name:',old.name,',age:',old.age,',salary:',old.salary,')','after update(id:',new.id,',name:',new.name,',age:',new.age,',salary:',new.salary,')'));
END $
DELIMITER ;


UPDATE emp SET `name`='zhangsan',age=29,salary=30000 WHERE id=1;


DELIMITER $
CREATE TRIGGER emp_delete_trigger
AFTER DELETE
ON emp
FOR EACH ROW
BEGIN
INSERT INTO emp_logs(id ,operation,operate_time,operate_id,operate_params)VALUES(NULL,"delete",NOW(),old.id,CONCAT(
'before delete(id:',old.id,',name:',old.name,',age:',old.age,',salary:',old.salary,')'));
END $
DELIMITER ;

INSERT INTO emp(id,`name`,`age`,salary)VALUES(NULL,'lisi',26,25000);

DELETE FROM emp WHERE id=2;

SELECT *FROM emp;
SELECT *FROM emp_logs;

SELECT *FROM emp;
# 查看触发器
SHOW TRIGGERS;

-- 删除触发器
DROP TRIGGER emp_delete_trigger;

```
### MySQL优化
```text
-- 只是当期这个数据库的
SHOW STATUS LIKE 'Com_______';
-- 查看整个数据库是查询为主还是插入，修改，或者删除为主
SHOW GLOBAL STATUS LIKE 'Com_______';
-- innodb的操作数量
SHOW STATUS LIKE 'Innodb_rows_%';
```
#### 定位低效率的执行SQL
* 慢查询日志:通过查询日志定位那些执行效率抵消的SQL语句，用--log-show-queries[=file_name]选项启动时，mysqld写一个包含所有执行时间超过
long_query_time秒的SQL语句的日志文件。

* show prcesslist:慢查询日志在查询结束后才记录，所以在应用反映执行效率出现问题的时候慢查询日志并不能定位问题，可以使用show processlist
命令查看当前MySQL在进行线程，包括线程状态，是否锁表等，可以实时的查看SQL的执行情况，同时对一些锁表操作进行优化。
![show processlist](./files/show_processlist.PNG)

* explain分析执行计划
```text
通过上面的步骤查询到低效的SQL语句之后，可以通过EXPLAIN或者DESC命令获取MySQL如何执行SELECT语句的信息，包括了SELECT语句如何连接和连接的
顺序。
```
![explain各个字段的含义](./files/explain.PNG)

| 字段 | 含义 |
| :------| :------: |
|id|select 查询的序列号，是一组数字，表示的是查询中执行的select子句或者操作表的顺序|
|select_type|表示SELECT的类型，常见的取值有SIMPLE(简单表，即不使用表连接或者子查询),PRIMARY(主查询，即外层的查询),UNION(UNION中的第二个或者后面的查询),SUBQUERY(子查询中的第一个SELECT等)|
|table|输出结果集的表|
|type|表示连接类型，性能由好到差点连接类型未(system--->constant--->eq_ref--->ref--->ref_or_null--->index_merge--->index_subquery--->range--->index--->all)|
|possible_keys|表示查询时,可能使用的索引|
|key|表示实际使用的索引|
|key_len|索引的长度|
|rows|扫描行的数量|
|extra|执行情况的说明和描述|

* id相同的情况下按顺序由上到下的执行
EXPLAIN SELECT *FROM t_role r,t_user u,user_role ur WHERE r.`id`=ur.`role_id` AND u.`id`=ur.`id`;

![id相同的情况下按顺序由上到下的执行](./files/explain_2.PNG)

* id 不同id值越大，优先级越高，越优先被执行
EXPLAIN SELECT *FROM t_role WHERE id=(SELECT role_id FROM user_role WHERE user_id =(SELECT id FROM t_user WHERE username='stu2'));
!id 不同id值越大，优先级越高，越优先被执行](./files/explain_3.PNG)

* id有相同，也有不同，id相同的认为是一组，从上倒下顺序执行；在所有的组中，id越大越先执行。



* explain 之 type
type 现实的是访问类型，是较为重要的一个指标，可以取值为:

| type | 含义 |
| :------| :------ | 
| NULL|MySQL不访问任何表，索引，直接返回结果（例如EXPLAIN SELECT NOW();） |
|system |表只有一行记录(等于系统表)，这是const类型的特例，一般不会出现 (EXPLAIN SELECT a.*FROM (SELECT * FROM t_user WHERE id=1)a)|
| const|表示通过索引一次就找到了，const用于比较primarykey或者unique索引。因为只匹配一行数据，所以很快。如果将主键置于where列表中，MySQL就能将改查询转换为一个常量。const于将"主键"或者"唯一"索引的所有部分于常量值进行比较(EXPLAIN SELECT *FROM t_user WHERE username='admin';) |
|eq_ref |类似于ref，区别在于使用的是唯一索引，使用主键的关联擦汗寻，关联查询的记录只有一条，常见于主键或者唯一索引扫描（EXPLAIN SELECT *FROM t_user u,t_role r WHERE u.`id`=r.`id`;） |
|ref |非唯一索引扫描，返回匹配某个单独值得所有行。本质上也是一种索引访问，但会所有匹配某个单独值的所有行(多个)-- 创建一个非唯一索引         CREATE INDEX idx_name ON t_user(`name`) ;                                        -- 查看执行计划                                                           EXPLAIN SELECT *FROM t_user WHERE `name`='系统管理员'; |
|range|只检索给定返回的行，使用一个索引来选择行，where之后出现的between,<,>,in等操作 |
| index|index于ALL的区别为index类型只是遍历了索引树，通常逼ALL快，ALL是遍历数据文件 (EXPLAIN SELECT id FROM t_user;)|
| ALL|将遍历权变一找到匹配的行 |

结果值从最好到最坏以此是：

```sql
NULL > system > const > eq_ref > ref > fulltext > ref_or_null > index_merge > unique_subquery > index_subquery > range > index > ALL


system > const > eq_ref > ref > range > index > ALL
```

* explain之key
 1. possible_keys:现实可能应用在这张表的索引，一个或者多个
 2. key:实际使用的索引，如果为NULL，则没有使用索引
 3. key_len:表示索引中使用的字节数，改之为索引字段最大可能的长度，也不损失精度的情况下，长度越短越好。
 

* explain之rows
```text
扫描行的记录
```

* explain之extra

其他的额外的执行计划信息，在该列展示。

| type | 含义 |
| ------| ------ | 
|using filesort |(表示查询出来的结果需要额外的排序，数据量笑得在内存，大得话在磁盘，需要优化)说明mysql会对书使用一个外部的索引排序，而不是按照表内的索引进行读取，称为"文件排序" | 
|using temporary|(查询使用到了临时表，比如去重，分组，排序等)使用临时表保存中间结果，MySQL在对查询结果排序使用临时表，常见的有order by和group by | 
|using index |(表示查询需要使用到索引，索引满足所有得数据需求)表示相应的select操作使用了覆盖索引，避免访问表的数据行 | 
|using where| 表示查询需要通过索引徽标查询数据|

### 索引的使用
索引是数据库优化最常用的手段之一，通过索引通常可以帮助用户解决大多数的MySQL的性能优化的问题。



###应用优化

#### 对应用的优化
使用数据库连接池

#### 减少对MySQL的访问
在编写应用代码时，需要能够理清对数据库的访问逻辑。能够一次连接就获取到结果的，就不用两次连接，这样可以大大减少对数据库无用的重复请求。

比如 ，需要获取书籍的id 和name字段 ， 则查询如下：

```sql
 select id , name from tb_book;
```

之后，在业务逻辑中有需要获取到书籍状态信息， 则查询如下：

```sql
select id , status from tb_book;
```

这样，就需要向数据库提交两次请求，数据库就要做两次查询操作。其实完全可以用一条SQL语句得到想要的结果。

```sql
select id, name , status from tb_book;
```

#### 开启查询缓存

MySQL的查询缓存默认是关闭的，需要手动配置参数 query_cache_type ， 来开启查询缓存。query_cache_type 该参数的可取值有三个 ：

| 值          | 含义                                                         |
| ----------- | ------------------------------------------------------------ |
| OFF 或 0    | 查询缓存功能关闭                                             |
| ON 或 1     | 查询缓存功能打开，SELECT的结果符合缓存条件即会缓存，否则，不予缓存，显式指定 SQL_NO_CACHE，不予缓存 |
| DEMAND 或 2 | 查询缓存功能按需进行，显式指定 SQL_CACHE 的SELECT语句才会缓存；其它均不予缓存 |

在 /usr/my.ini 配置中，增加以下配置 ：
![my.ini ](./files/1a55ac950c8d9f07e479723c71fdee3.png)


#### 增加cache层
在应用中，我们可以在应用中增加 缓存 层来达到减轻数据库负担的目的。缓存层有很多种，也有很多实现方式，只要能达到降低数据库的负担又能满足应用需求就可以。

因此可以部分数据从数据库中抽取出来放到应用端以文本方式存储， 或者使用框架(Mybatis, Hibernate)提供的一级缓存/二级缓存，或者使用redis数据库来缓存数据 。

#### 1.3 负载均衡
负载均衡是应用中使用非常普遍的一种优化方法，它的机制就是利用某种均衡算法，将固定的负载量分布到不同的服务器上， 以此来降低单台服务器的负载，达到优化的效果。
#### 1.3.1 利用MySQL复制分流查询
通过MySQL的主从复制，实现读写分离，使增删改操作走主节点，查询操作走从节点，从而可以降低单台服务器的读写压力。

![利用MySQL复制分流查询](./files/1.jpg)


####查询缓存失效的情况
）` SQL 语句不一致的情况， 要想命中查询缓存，查询的SQL语句必须一致。`

```sql
SQL1 : select count(*) from tb_item;
SQL2 : Select count(*) from tb_item;
```

2） `当查询语句中有一些不确定的时，则不会缓存。如 ： now() , current_date() , curdate() , curtime() , rand() , uuid() , user() , database() 。`

```sql
SQL1 : select * from tb_item where updatetime < now() limit 1;
SQL2 : select user();
SQL3 : select database();
```

3） `不使用任何表查询语句。`

```sql
select 'A';
```

4）` 查询 mysql， information_schema或 performance_schema 数据库中的表时，不会走查询缓存。`(默认的系统数据库)

```sql
select * from information_schema.engines;
```

5） `在存储的函数，触发器或事件的主体内执行的查询。`

6） `如果表更改，则使用该表的所有高速缓存查询都将变为无效并从高速缓存中删除`。这包括使用`MERGE`映射到已更改表的表的查询。一个表可以被许多类型的语句，如被改变 INSERT， UPDATE， DELETE， TRUNCATE TABLE， ALTER TABLE， DROP TABLE，或 DROP DATABASE 。

