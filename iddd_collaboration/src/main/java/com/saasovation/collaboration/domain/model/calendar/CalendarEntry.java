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
 *<h3>日历条目 - 聚合根</h3>
 *<p>日历条目就是待办事项、活动，或者日历事件等。
 */
public class CalendarEntry extends EventSourcedRootEntity {

	/** 警报 **/
    private Alarm alarm;
    /** ID **/
    private CalendarEntryId calendarEntryId;
    /** 日历ID。遵循“通过唯一标识引用其它聚合”的原则。 **/
    private CalendarId calendarId;
    /** 描述 **/
    private String description;
    /** 参与者 **/
    private Set<Participant> invitees;
    /** 地址 **/
    private String location;
    /** 拥有者 **/
    private Owner owner;
    /** 重复时间 **/
    private Repetition repetition;
    /** 租户（订阅者） **/
    private Tenant tenant;
    /** 条目活动时间范围 **/
    private TimeSpan timeSpan;

    /**
     *<h3>构造CalendarEntry</h3>
     *<p>代表了重建（还原）一个已存在的CalendarEntry对象。
     *<p>由于本聚合使用了事件溯源，所以初始化需要参数的是事件流及事件版本号。
     *重建过程是将事件流中的事件按顺序依次使用聚合内的事件重放方法更新聚合对
     *象的状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     *
     * @param anEventStream
     * @param aStreamVersion
     */
    public CalendarEntry(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public Alarm alarm() {
        return this.alarm;
    }

    public Set<Participant> allInvitees() {
        return Collections.unmodifiableSet(this.invitees());
    }

    public CalendarEntryId calendarEntryId() {
        return this.calendarEntryId;
    }

    public CalendarId calendarId() {
        return this.calendarId;
    }

    public String description() {
        return this.description;
    }

    public String location() {
        return this.location;
    }

    public Owner owner() {
        return this.owner;
    }

    public Repetition repetition() {
        return this.repetition;
    }

    /**
     *<h3>修改描述</h3>
     *<p>这是一个CQS命令方法，用于修改日历条目的描述，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link CalendarEntryDescriptionChanged}事件对象中，并发布它，最
     *后由事件重放方法{@link #when(CalendarEntryDescriptionChanged)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * @param aDescription
     */
    public void changeDescription(String aDescription) {
        if (aDescription != null) {
            aDescription = aDescription.trim();
            if (!aDescription.isEmpty() && !this.description().equals(aDescription)) {
                this.apply(new CalendarEntryDescriptionChanged(
                        this.tenant(), this.calendarId(), this.calendarEntryId(),
                        aDescription));
            }
        }
    }

    /**
     *<h3>邀请参与者到当前条目中</h3>
     *<p>这是一个CQS命令方法，用于添加一个参与者参与当前待办事项，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link CalendarEntryParticipantInvited}事件对象中，并发布它，最
     *后由事件重放方法{@link #when(CalendarEntryParticipantInvited)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     *
     * @param aParticipant
     */
    public void invite(Participant aParticipant) {
        this.assertArgumentNotNull(aParticipant, "The participant must be provided.");

        if (!this.invitees().contains(aParticipant)) {
            this.apply(new CalendarEntryParticipantInvited(
                    this.tenant(), this.calendarId(), this.calendarEntryId(),
                    aParticipant));
        }
    }

    /**
     *<h3>修改待办事项的地址</h3>
     *
     *<p>这是一个CQS命令方法，用于修改待办事项的地址，没有返回值。
     *
     *<p>处理时，将参数封装到一个新的{@link CalendarEntryRelocated}事件对象中，并发布它，最
     *后由事件重放方法{@link #when(CalendarEntryRelocated)}更新当前聚合状态。
     *
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     *
     * @param aLocation
     */
    public void relocate(String aLocation) {
        if (aLocation != null) {
            aLocation = aLocation.trim();
            if (!aLocation.isEmpty() && !this.location().equals(aLocation)) {
                this.apply(new CalendarEntryRelocated(
                        this.tenant(), this.calendarId(), this.calendarEntryId(),
                        aLocation));
            }
        }
    }

    /**
     *<h3>重新设置日历条目</h3>
     *
     *<p>这是一个CQS命令方法，用于重新设置日历，修改描述、地址、时间，没有返回值。
     *
     *<p>处理时，将参数封装到一个新的{@link CalendarEntryRescheduled}事件对象中，并发布它，最
     *后由事件重放方法{@link #when(CalendarEntryRescheduled)}更新当前聚合状态。
     *
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aDescription
     * @param aLocation
     * @param aTimeSpan
     * @param aRepetition
     * @param anAlarm
     */
    public void reschedule(
            String aDescription,
            String aLocation,
            TimeSpan aTimeSpan,
            Repetition aRepetition,
            Alarm anAlarm) {

        this.assertArgumentNotNull(anAlarm, "The alarm must be provided.");
        this.assertArgumentNotNull(aRepetition, "The repetition must be provided.");
        this.assertArgumentNotNull(aTimeSpan, "The time span must be provided.");

        if (aRepetition.repeats().isDoesNotRepeat()) {
            aRepetition = Repetition.doesNotRepeatInstance(aTimeSpan.ends());
        }

        // 时间校验
        this.assertTimeSpans(aRepetition, aTimeSpan);

        // 执行其它命令：修改描述
        this.changeDescription(aDescription);
        // 执行其它命令：修改地址
        this.relocate(aLocation);

        this.apply(new CalendarEntryRescheduled(
                this.tenant(), this.calendarId(), this.calendarEntryId(),
                aTimeSpan, aRepetition, anAlarm));
    }

    public Tenant tenant() {
        return this.tenant;
    }

    public TimeSpan timeSpan() {
        return this.timeSpan;
    }

    /**
     *<h3>从当前条目中取消一个参与者</h3>
     *<p>这是一个CQS命令方法，用于从当前待办事项的参与者中移除一个参与者，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link CalendarEntryParticipantUninvited}事件对象中，并发布它，最
     *后由事件重放方法{@link #when(CalendarEntryParticipantUninvited)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aParticipant
     */
    public void uninvite(Participant aParticipant) {
        this.assertArgumentNotNull(aParticipant, "The participant must be provided.");

        if (this.invitees().contains(aParticipant)) {
            this.apply(new CalendarEntryParticipantUninvited(
                    this.tenant(), this.calendarId(), this.calendarEntryId(),
                    aParticipant));
        }
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            CalendarEntry typedObject = (CalendarEntry) anObject;
            equalObjects =
                this.tenant().equals(typedObject.tenant()) &&
                this.calendarId().equals(typedObject.calendarId()) &&
                this.calendarEntryId().equals(typedObject.calendarEntryId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
                + (5439 * 79)
                + this.tenant().hashCode()
                + this.calendarId().hashCode()
                + this.calendarEntryId().hashCode();

            return hashCodeValue;
    }

    @Override
    public String toString() {
        return "CalendarEntry [alarm=" + alarm + ", calendarEntryId=" + calendarEntryId + ", calendarId=" + calendarId
                + ", description=" + description + ", invitees=" + invitees + ", location=" + location + ", owner=" + owner
                + ", repetition=" + repetition + ", tenant=" + tenant + ", timeSpan=" + timeSpan + "]";
    }

    /**
     *<h3>构造CalendarEntry</h3>
     *
     *<p>代表了一个新生的CalendarEntry对象，同时会发布创建事件{@link CalendarEntryScheduled}。
     *本方法是受保护的protected，所以在当前calendar模块（包）外是无法使用的，需要创建新生的
     *对象时，只能通过Calendar聚合的{@link Calendar#scheduleCalendarEntry scheduleCalendarEntry}
     *命令创建，因为一个日历上可以创建多个条目。
     *
     *<p>初始化需要一些参数，其中某些是必须的，初始化时会对参数作一些断言。由于本聚合使用
     *了事件溯源，所以在断言之后将参数封装到了一个{@link CalendarEntryScheduled}事件对象中，
     *再将事件发布，最后由聚合内的事件重放方法{@link #when(CalendarEntryScheduled)}更新当前聚
     *合状态。
     *
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aTenant
     * @param aCalendarId
     * @param aCalendarEntryId
     * @param aDescription
     * @param aLocation
     * @param anOwner
     * @param aTimeSpan
     * @param aRepetition
     * @param anAlarm
     * @param anInvitees
     */
    protected CalendarEntry(
            Tenant aTenant,
            CalendarId aCalendarId,
            CalendarEntryId aCalendarEntryId,
            String aDescription,
            String aLocation,
            Owner anOwner,
            TimeSpan aTimeSpan,
            Repetition aRepetition,
            Alarm anAlarm,
            Set<Participant> anInvitees) {

        this();

        this.assertArgumentNotNull(anAlarm, "The alarm must be provided.");
        this.assertArgumentNotNull(aCalendarEntryId, "The calendar entry id must be provided.");
        this.assertArgumentNotNull(aCalendarId, "The calendar id must be provided.");
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");
        this.assertArgumentNotEmpty(aLocation, "The location must be provided.");
        this.assertArgumentNotNull(anOwner, "The owner must be provided.");
        this.assertArgumentNotNull(aRepetition, "The repetition must be provided.");
        this.assertArgumentNotNull(aTenant, "The tenant must be provided.");
        this.assertArgumentNotNull(aTimeSpan, "The time span must be provided.");

        if (aRepetition.repeats().isDoesNotRepeat()) {
            aRepetition = Repetition.doesNotRepeatInstance(aTimeSpan.ends());
        }

        // 时间校验
        this.assertTimeSpans(aRepetition, aTimeSpan);

        if (anInvitees == null) {
            anInvitees = new HashSet<Participant>(0);
        }

        this.apply(new CalendarEntryScheduled(aTenant, aCalendarId, aCalendarEntryId, aDescription,
                aLocation, anOwner, aTimeSpan, aRepetition, anAlarm, anInvitees));
    }

    /**
     *<h3>构造CalendarEntry</h3>
     */
    protected CalendarEntry() {
        super();
    }

    /**
     *<h3>处理CalendarEntryDescriptionChanged事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(CalendarEntryDescriptionChanged anEvent) {
        this.setDescription(anEvent.description());
    }

    /**
     *<h3>处理CalendarEntryParticipantInvited事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(CalendarEntryParticipantInvited anEvent) {
        this.invitees().add(anEvent.participant());
    }

    /**
     *<h3>处理CalendarEntryRelocated事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(CalendarEntryRelocated anEvent) {
        this.setLocation(anEvent.location());
    }

    /**
     *<h3>处理CalendarEntryRescheduled事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(CalendarEntryRescheduled anEvent) {
        this.setAlarm(anEvent.alarm());
        this.setRepetition(anEvent.repetition());
        this.setTimeSpan(anEvent.timeSpan());
    }

    /**
     *<h3>处理CalendarEntryScheduled事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(CalendarEntryScheduled anEvent) {
        this.setAlarm(anEvent.alarm());
        this.setCalendarEntryId(anEvent.calendarEntryId());
        this.setCalendarId(anEvent.calendarId());
        this.setDescription(anEvent.description());
        this.setInvitees(anEvent.invitees());
        this.setLocation(anEvent.location());
        this.setOwner(anEvent.owner());
        this.setRepetition(anEvent.repetition());
        this.setTenant(anEvent.tenant());
        this.setTimeSpan(anEvent.timeSpan());
    }

    /**
     *<h3>处理CalendarEntryParticipantUninvited事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(CalendarEntryParticipantUninvited anEvent) {
        this.invitees().remove(anEvent.participant());
    }

    private void setAlarm(Alarm anAlarm) {
        this.alarm = anAlarm;
    }

    /**
     * 断言时间
     * @param aRepetition
     * @param aTimeSpan
     */
    private void assertTimeSpans(Repetition aRepetition, TimeSpan aTimeSpan) {
        if (aRepetition.repeats().isDoesNotRepeat()) {
            this.assertArgumentEquals(
                    aTimeSpan.ends(),
                    aRepetition.ends(),
                    "Non-repeating entry must end with time span end.");
        } else {
            this.assertArgumentFalse(
                    aTimeSpan.ends().after(aRepetition.ends()),
                    "Time span must end when or before repetition ends.");
        }
    }

    private void setCalendarEntryId(CalendarEntryId aCalendarEntryId) {
        this.calendarEntryId = aCalendarEntryId;
    }

    private void setCalendarId(CalendarId aCalendarId) {
        this.calendarId = aCalendarId;
    }

    private void setDescription(String aDescription) {
        this.description = aDescription;
    }

    /**
     * 
     * @return
     */
    private Set<Participant> invitees() {
        return this.invitees;
    }

    private void setInvitees(Set<Participant> anInvitees) {
        this.invitees = anInvitees;
    }

    private void setLocation(String aLocation) {
        this.location = aLocation;
    }

    private void setOwner(Owner anOwner) {
        this.owner = anOwner;
    }

    private void setRepetition(Repetition aRepetition) {
        this.repetition = aRepetition;
    }

    private void setTenant(Tenant aTenant) {
        this.tenant = aTenant;
    }

    private void setTimeSpan(TimeSpan aTimeSpan) {
        this.timeSpan = aTimeSpan;
    }
}
