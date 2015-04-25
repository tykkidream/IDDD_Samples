
设计聚合
========

在设计聚合时将遵循4原则：

- 1、在一致性边界之内建模真正不变条件
- 2、设计小聚合
- 3、通过唯一标识引用它他聚合
- 4、在边界之外使用最终一致性

设计日历模块聚合
----------------------
根据`设计小聚合`原则，可设计日历（Calendar）和日历条目（CalendarEntry）两个聚合。

- Calendar：
- CalendarEntry：

###设计Calendar聚合

####状态

- private CalendarId calendarId：日历ID，当前聚合的唯一标识，CalendarId是当前模块的值对象
- private String description：描述
- private String name：名称
- private Owner owner：拥有者，Owner是collaborator模块的值对象
- private Set<CalendarSharer> sharedWith：动态订阅者，CalendarSharer是当前模块的值对象
- private Tenant tenant：租户（订阅方），Tenant是collaborator模块的值对象

####构造函数

- public Calendar(Tenant,CalendarId,....)：
- public Calendar(List<DomainEvent>, int)：
- protected Calendar()：

####CQS查询方法

-  public CalendarEntry scheduleCalendarEntry(CalendarIdentityService,String,String,Owner anOwner,...)

####CQS命令方法

- public void changeDescription(String)
- public void rename(String)
- public void shareCalendarWith(CalendarSharer)
- public void unshareCalendarWith(CalendarSharer)

####事件重放方法

- protected void when(CalendarCreated)：从CalendarCreated
- protected void when(CalendarDescriptionChanged)
- protected void when(CalendarRenamed)
- protected void when(CalendarShared)
- protected void when(CalendarUnshared)

####Setter方法

- private void setCalendarId(CalendarId)
- private void setDescription(String)
- private void setName(String)
- private void setOwner(Owner)
- private void setSharedWith(Set<CalendarSharer>)
- private void setTenant(Tenant)

从以上可以知道：

一个事件 ———— 一个命令方法、构造函数 ———— 一个事件重放方法

###设计CalendarEntry聚合

####状态

- private Alarm alarm：警报，值对象
- private CalendarEntryId calendarEntryId：日历条目ID，当前聚合的唯一标识，CalendarEntryId是当前模块的值对象
- private CalendarId calendarId：日历ID，Calendar聚合的唯一标识，CalendarId是当前模块的值对象，遵循`通过唯一标识引用其它聚合`的原则
- private String description：描述
- private Set<Participant> invitees：参与者，Participant是collaborator模块的值对象
- private String location：地址
- private Owner owner： 拥有者，Owner是collaborator模块的值对象
- private Repetition repetition：重复时间，Repetition是当前模块的值对象
- private Tenant tenant：租户（订阅者），Tenant是collaborator模块的值对象
- private TimeSpan timeSpan：条目活动时间范围，当前模块的值对象


####构造函数

- public CalendarEntry(Tenant,CalendarId,....)：
- public CalendarEntry(List<DomainEvent>, int)：
- protected CalendarEntry()：

####CQS查询方法

####CQS命令方法

- public void changeDescription(String)
- public void invite(Participant)
- public void relocate(String)
- public void uninvite(Participant)
- public void reschedule(String,String,TimeSpan,Repetition,Alarm)

####事件重放方法

- protected void when(CalendarEntryScheduled anEvent)
- protected void when(CalendarEntryDescriptionChanged anEvent)
- protected void when(CalendarEntryParticipantInvited anEvent)
- protected void when(CalendarEntryRelocated anEvent)
- protected void when(CalendarEntryRescheduled anEvent)
- protected void when(CalendarEntryParticipantUninvited anEvent)

####Setter方法

- private void setAlarm(Alarm 
- private void setCalendarEntryId(CalendarEntryId
- private void setCalendarId(CalendarId
- private void setDescription(String
- private void setInvitees(Set<Participant>
- private void setLocation(String
- private void setOwner(Owner
- private void setRepetition(Repetition
- private void setTenant(Tenant
- private void setTimeSpan(TimeSpan

###设计聚合总结

####Setter方法有以下特点：

1. 方法都为私有的private
2. 

设计事件类型
-----------------

###设计Calendar聚合内发生的事件类型

- CalendarCreated：当创建了一个新日历时发生。
- CalendarDescriptionChanged：当日历的描述被改变时发生。
- CalendarRenamed：当日历的名称被改变时发生。
- CalendarShared：当给日历添加了一个订阅者CalendarSharer时发生。
- CalendarUnshared：当给日历取消了一个订阅者CalendarSharer时发生。

###设计CalendarEntry聚合内发生的事件类型

- CalendarEntryScheduled：当创建了一个新日历条目时发生。
- CalendarEntryDescriptionChanged：当日历条目的描述被改变时发生。
- CalendarEntryRelocated：当日历的地址被改变时发生。
- CalendarEntryRescheduled：当日历的被重新设置时发生。
- CalendarEntryParticipantInvited：当邀请参与者到当前条目时发生。
- CalendarEntryParticipantUninvited：当从当前条目中取消一个参与者时发生。

设计值对象
--------------
