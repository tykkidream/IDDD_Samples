//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.saasovation.collaboration.domain.model.calendar;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.saasovation.collaboration.domain.model.collaborator.Owner;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.EventSourcedRootEntity;

/**
 * <h3>日历 - 聚合根</h3>
 * <p>
 */
public class Calendar extends EventSourcedRootEntity {

	/** ID **/
    private CalendarId calendarId;
    /** 描述 **/
    private String description;
    /** 名称 **/
    private String name;
    /** 拥有者 **/
    private Owner owner;
    /** ID **/
    private Set<CalendarSharer> sharedWith;
    /** 房客、住户 **/
    private Tenant tenant;

    /**
     *<h3>初始化Calendar</h3>
     *<p>代表了一个新生的Calendar对象，同时会发布创建事件{@link CalendarCreated}。
     *<p>初始化需要一些参数，其中某些是必须的，初始化时会对参数作一些断言。由
     *于本聚合使用了事件溯源，所以在断言之后将参数封装到了一个事件对象中，再将
     *事件发布，最后由聚合内的事件处理方法也就是事件重放方法，处理事件对象并获
     *得参数更新到当前聚合对象的状态上。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     *
     * @param aTenant
     * @param aCalendarId
     * @param aName
     * @param aDescription
     * @param anOwner
     * @param aSharedWith
     */
    public Calendar(
            Tenant aTenant,
            CalendarId aCalendarId,
            String aName,
            String aDescription,
            Owner anOwner,
            Set<CalendarSharer> aSharedWith) {

        this();

        /* 除了aSharedWith，其它必须不能为null。 */
        this.assertArgumentNotNull(aTenant, "The tenant must be provided.");
        this.assertArgumentNotNull(aCalendarId, "The calendar id must be provided.");
        this.assertArgumentNotEmpty(aName, "The name must be provided.");
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");
        this.assertArgumentNotNull(anOwner, "The owner must be provided.");

        if (aSharedWith == null) {
            aSharedWith = new HashSet<CalendarSharer>(0);
        }

        // 发生了一个CalendarCreated事件，并对其处理。
        this.apply(new CalendarCreated(aTenant, aCalendarId, aName,
                aDescription, anOwner, aSharedWith));
    }

    /**
     *<h3>初始化Calendar</h3>
     *<p>代表了重建（还原）一个已存在的Calendar对象。
     *<p>由于本聚合使用了事件溯源，所以初始化需要参数的是事件流及事件版本号。
     *重建过程是将事件流中的事件按顺序依次使用聚合内的事件重放方法更新聚合对
     *象的姿态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     *
     * @param anEventStream
     * @param aStreamVersion
     */
    public Calendar(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    /**
     *<h3>获取日历（接收）共享者</h3>
     *<p>“日历（接收）共享者”状态的获取方法，作用同getter，但是更具有领域（业务）含义。
     * @return
     */
    public Set<CalendarSharer> allSharedWith() {
        return Collections.unmodifiableSet(this.sharedWith());
    }

    /**
     *<h3>获取日历ID</h3>
     *<p>“日历ID”状态的获取方法，作用同getter，但是更具有领域（业务）含义。
     * @return
     */
    public CalendarId calendarId() {
        return this.calendarId;
    }

    /**
     *<h3></h3>
     * @param aDescription
     */
    public void changeDescription(String aDescription) {
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");

        this.apply(new CalendarDescriptionChanged(
                this.tenant(), this.calendarId(), this.name(), aDescription));
    }

    public String description() {
        return this.description;
    }

    public String name() {
        return this.name;
    }

    public Owner owner() {
        return this.owner;
    }

    /**
     *<h3>重命名日历名称</h3>
     *<p>这是一个CQS命令方法，用于重命名日历名称，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link CalendarRenamed}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(CalendarRenamed)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     *
     * @param aName
     */
    public void rename(String aName) {
        this.assertArgumentNotEmpty(aName, "The name must be provided.");

        this.apply(new CalendarRenamed(
                this.tenant(), this.calendarId(), aName, this.description()));
    }

    /**
     *<h3>计划日程任务</h3>
     *<p>这是一个CQS查询方法，用于创建一个日历条目，返回现一个新{@link CalendarEntry}
     *对象，所以此方法是CalendarEntry的一个工厂方法。
     *<p>这个方法简化了创建CalendarEntry的一些方面，使用了外部模块（其它包）无法使用的
     *{@link CalendarEntry#CalendarEntry(Tenant, CalendarId, CalendarEntryId, String, String, Owner, TimeSpan, Repetition, Alarm, Set) 构造函数}，
     *其中tenant、calendarId由当前聚合对象的状态提供，calendarEntryId由aCalendarIdentityService的
     *{@link CalendarIdentityService#nextCalendarEntryId() nextCalendarEntryId()}提供。
     *
     * @param aCalendarIdentityService
     * @param aDescription
     * @param aLocation
     * @param anOwner
     * @param aTimeSpan
     * @param aRepetition
     * @param anAlarm
     * @param anInvitees
     * @return
     */
    public CalendarEntry scheduleCalendarEntry(
            CalendarIdentityService aCalendarIdentityService,
            String aDescription,
            String aLocation,
            Owner anOwner,
            TimeSpan aTimeSpan,
            Repetition aRepetition,
            Alarm anAlarm,
            Set<Participant> anInvitees) {

        CalendarEntry calendarEntry =
                new CalendarEntry(
                        this.tenant(),
                        this.calendarId(),
                        aCalendarIdentityService.nextCalendarEntryId(),
                        aDescription,
                        aLocation,
                        anOwner,
                        aTimeSpan,
                        aRepetition,
                        anAlarm,
                        anInvitees);

        return calendarEntry;
    }

    /**
     *<h3>添加日历的（接收）共享的新用户</h3>
     *<p>这是一个CQS命令方法，用于添加一个接收共享日历的新用户，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link CalendarShared}事件对象中，并发布它，最
     *后由事件重放方法{@link #when(CalendarShared)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     *
     * @param aCalendarSharer
     */
    public void shareCalendarWith(CalendarSharer aCalendarSharer) {
        this.assertArgumentNotNull(aCalendarSharer, "The calendar sharer must be provided.");

        if (!this.sharedWith().contains(aCalendarSharer)) {
            this.apply(new CalendarShared(this.tenant(), this.calendarId(),
                    this.name(), aCalendarSharer));
        }
    }

    /**
     *<h3>删除日历的（接收）共享的旧用户</h3>
     *<p>这是一个CQS命令方法，用于删除一个接收共享日历的旧用户，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link CalendarUnshared}事件对象中，并发布它，最
     *后由事件重放方法{@link #when(CalendarUnshared)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     *
     * @param aCalendarSharer
     */
    public void unshareCalendarWith(CalendarSharer aCalendarSharer) {
        this.assertArgumentNotNull(aCalendarSharer, "The calendar sharer must be provided.");

        if (this.sharedWith().contains(aCalendarSharer)) {
            this.apply(new CalendarUnshared(this.tenant(), this.calendarId(),
                    this.name(), aCalendarSharer));
        }
    }

    public Tenant tenant() {
        return this.tenant;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Calendar typedObject = (Calendar) anObject;
            equalObjects =
                this.tenant().equals(typedObject.tenant()) &&
                this.calendarId().equals(typedObject.calendarId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
                + (87213 * 73)
                + this.tenant().hashCode()
                + this.calendarId().hashCode();

            return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Calendar [calendarId=" + calendarId + ", description=" + description + ", name=" + name + ", owner=" + owner
                + ", sharedWith=" + sharedWith + ", tenant=" + tenant + "]";
    }

    /**
     * 
     */
    protected Calendar() {
        super();
    }

    /**
     *<h3>处理CalendarCreated事件。</h3>
     *<p>事件重放的方法。
     * @param anEvent
     */
    protected void when(CalendarCreated anEvent) {
        this.setCalendarId(anEvent.calendarId());
        this.setDescription(anEvent.description());
        this.setName(anEvent.name());
        this.setOwner(anEvent.owner());
        this.setSharedWith(anEvent.sharedWith());
        this.setTenant(anEvent.tenant());
    }
    
    /**
     *<h3>处理CalendarCreated事件。</h3>
     *<p>事件重放的方法。
     * @param anEvent
     */
    protected void when(CalendarDescriptionChanged anEvent) {
        this.setDescription(anEvent.description());
    }
    
    /**
     *<h3>处理CalendarRenamed事件。</h3>
     *<p>事件重放的方法。
     * @param anEvent
     */
    protected void when(CalendarRenamed anEvent) {
        this.setName(anEvent.name());
    }
    
    /**
     *<h3>处理CalendarShared事件。</h3>
     *<p>事件重放的方法。
     * @param anEvent
     */
    protected void when(CalendarShared anEvent) {
        this.sharedWith().add(anEvent.calendarSharer());
    }
    
    /**
     *<h3>处理CalendarUnshared事件。</h3>
     *<p>事件重放的方法。
     * @param anEvent
     */
    protected void when(CalendarUnshared anEvent) {
        this.sharedWith().remove(anEvent.calendarSharer());
    }

    private void setCalendarId(CalendarId calendarId) {
        this.calendarId = calendarId;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setOwner(Owner owner) {
        this.owner = owner;
    }

    private Set<CalendarSharer> sharedWith() {
        return this.sharedWith;
    }

    private void setSharedWith(Set<CalendarSharer> sharedWith) {
        this.sharedWith = sharedWith;
    }

    private void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
