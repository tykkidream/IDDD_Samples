此项目演示了《实施领域驱动设计》书中示例的限界上下文——by Vaughn Vernon:

http://vaughnvernon.co/?page_id=168

The models and surrounding architectural mechanisms
may be in various states of flux as the are refined
over time. Some tests may be incomplete. The code is
not meant to be a reflection of a production quality
work, but rather as a set of reference projects for
the book.

管道是消息通道
端口连接过滤器和管道 过滤器通过端口连接到输入和输出管道
过滤器即是处理器 过滤器可以对消息进行处理，而不见得一定对消息进行过滤
松耦合
可换性

说明
==================

iddd_agilepm 项目使用了一个key-value存储作为底层的持久层机制，主要是LevelDB。
实际使用的LevelDB是一个纯Java实现：https://github.com/dain/leveldb

目前 iddd_agilepm 没使用任何一种容器（例如Spring）。

iddd_collaboration 项目使用了事件溯源和CQRS。
It purposely avoids the use of an object-relational
mapper, showing that a simple JDBC-based query engine
and DTO matter can be used instead. This technique does
have its limitations, but it is meant to be small and fast
and require no configuration or annotations. It is not
meant to be perfect.

It may be helpful to make one additional mental note on
the iddd_collaboration CQRS implementation. To keep the
example simple it persists the Event Sourced write model
and the CQRS read model in one thread. Since two different
stores are used--LevelDB for the Event Journal and MySQL
for the read model--there may be very slight opportunities
for inconsistency, but not much. The idea was to keep the
two models as close to consistent as possible without
using the same data storage (and transaction) for both.
Two different storage mechanisms were used purposely to
demonstrate that they can be separate.

The iddd_identityaccess project uses uses object-relational
mapping (Hibernate), but so as not to leave it "boring" it
provides a RESTful client interface and even publishes
Domain-Event notifications via REST (logs) and RabbitMQ.

Finally the iddd_common project provides a number of reusable
components. This is not an attempt to be a framework, but
just leverages reuse to the degree that code copying doesn't
liter each project. This is not a recommendation, but it
did work well and save a considerable amount of work while
producing the samples.

用法
=====

要求
--------

- Java 7 (8+ does not work)
- MySQL Client + Server
- RabbitMQ

安装 (with Docker)
-------------------

To make it easy to run the tests and it requirements,
the `startContainers.sh` script is provided. Which
will start a:
- MySQL Server container
- RabbitMQ Server container
- RabbitMQ Management container

If the `mysql` command is available, which is the mysql client,
also the required SQL scripts will be imported into the MySQL
Server.

If you use the `startContainers.sh` script, you don't need
MySQL Server and RabbitMQ installed locally. Instead,
Docker needs to be installed as the script will start
MySQL and RabbitMQ in Docker containers.

构建
------

You can build the project by running:

```
./gradlew build
```

This automatically downloads Gradle and builds the project, including running the tests.

The Gradle build using Maven repositories was provided by
Michael Andrews (Github michaelajr and Twitter @MichaelAJr).
Thanks much!


I hope you benefit from the samples.

Vaughn Vernon
Author: Implementing Domain-Driven Design
Twitter: @VaughnVernon
http://vaughnvernon.co/
