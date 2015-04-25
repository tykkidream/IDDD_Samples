需求说明
================

这是一套企业协作软件，功能包括：

- 论坛（forum）
- 共享日历（shared calendar）
- 博客（blog）
- 即时消息（instant message）
- Wiki
- 留言板（message board）
- 文档管理（document management）
- 通知（announcement）
- 提醒（alert）
- 活动跟踪（activity tracking）
- RSS订阅

虽然功能很多，但是第一个协作工具可以单独使用。

领域分析
========

当前产品形成了自己的限界上下文，称为协作上下文（Collaboration Context）。

协作上下文作为一个显示边界，它的领域模型在于这个边界之内，这些模型具有独特的非歧义的含义。

协作者（collaborator），这是协作活动中的概念和语言。

日历子域（Calendar Subdomain）
-------------------------------------


协作者子域（Collaborator Subdomain）
--------------------------------------------

在参与协作活动的协作者中，包含这样一些概念：

- 协作者（Collaborator）
- 作者（Author）
- 创作者（Creator）
- 主持人（Moderator）
- 拥有者（Owner）
- 参加者（Participant）


论坛子域（Forum Subdomain）
-----------------------------------


房客子域（Tenant Subdomain）
-----------------------------------

开发设计
========

[开发设计](开发设计.md)

开发实现
========

通过领域分析，开发实现时将采取以下方式：

|  领域内容   |       实现     |
|---------------|---------------|
|日历子域    |日历模块     |
|协作者子域 |协作者模块 |
|论坛子域    |论坛模块     |
|房客子域    |房客模块     |

日历模块（Calendar Model）
-------------------------------------

此模块的包为：`com.saasovation.collaboration.domain.model.calendar`

日历内容：

|      类型      |                           类名                 |                 名称            |       说明      
|---------------|--------------------------------------|---------------------------|----------------
|聚合根        |Calendar                                          |日历                          |
|值对象        |CalendarId                                      |日历ID                            |
|                      |CalendarSharer                                |日历共享（集合）      |
|事件           |CalendarCreated                             |日历创建事件             |
|                       |CalendarDescriptionChanged       |日历描述修改事件      |
|                       |CalendarRenamed                          |日历创建事件             |
|                       |CalendarShared                              |日历共享事件             |
|                       |CalendarUnshared                          |日历取消共享事件      |
|仓库            |CalendarRepository                        |日历仓库                   |

日历条目内容：

|       类型      |                             类名                    |                      名称               |       说明       |
|---------------|------------------------------------------|---------------------------------|----------------|
|聚合根        |CalendarEntry                                      |日历条目                           |日历模块      |
|值对象        |CalendarEntryId                                   |日历条目ID                             |日历模块      |
|                       |Repetition                                             |警报时间类型                     |日历模块      |
|                       |TimeSpan                                             |警报时间类型                     |日历模块      |
|事件           |CalendarEntryDescriptionChanged     |日历条目描述修改事件       |日历模块      |
|                      |CalendarEntryParticipantInvited          |日历条目参与者邀请          |日历模块      |
|                      |CalendarEntryParticipantUninvited      |日历条目参与者取消邀请   |日历模块      |
|                      |CalendarEntryRelocated                        |日历条目迁移事件             |日历模块      |
|                      |CalendarEntryRescheduled                    |日历条目改期事件             |日历模块      |
|                      |CalendarEntryScheduled                        |日历条目计划事件             |日历模块      |
|仓库           |CalendarEntryRepository                       |日历条目仓库                    |日历模块      |
|外部值对象 |Participant                                               |参与者（集合）                 |日历模块      |
|聚合引用    |CalendarId                                              |引用了日历聚合的ID             |日历模块      |

日历、日历条目共有内容：

|       类型      |                           类名                 |                 名称             |       说明       |
|---------------|--------------------------------------|----------------------------|----------------|
|值对象        |Alarm                                              |警报                           |日历模块      |
|                       |AlarmUnitsType                             |警报时间类型             |日历模块      |
|                       |RepeatType                                     |警报时间类型             |日历模块      |
|领域服务     |CalendarIdentityService                 |服务                          |日历模块      |
|外部值对象  |Owner                                             |拥有者                       |日历模块      |
|                       |Tenant                                             |房客                           |日历模块      |

日历模块（Calendar Model）
-------------------------------------



协作者模块（Collaborator Model）
-------------------------------------



论坛模块（Forum Model）
-------------------------------------



房客模块（Tenant Model）
-------------------------------------