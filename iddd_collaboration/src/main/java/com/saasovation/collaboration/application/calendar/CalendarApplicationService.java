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

package com.saasovation.collaboration.application.calendar;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.saasovation.collaboration.application.calendar.data.CalendarCommandResult;
import com.saasovation.collaboration.domain.model.calendar.Alarm;
import com.saasovation.collaboration.domain.model.calendar.AlarmUnitsType;
import com.saasovation.collaboration.domain.model.calendar.Calendar;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntry;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryRepository;
import com.saasovation.collaboration.domain.model.calendar.CalendarId;
import com.saasovation.collaboration.domain.model.calendar.CalendarIdentityService;
import com.saasovation.collaboration.domain.model.calendar.CalendarRepository;
import com.saasovation.collaboration.domain.model.calendar.CalendarSharer;
import com.saasovation.collaboration.domain.model.calendar.RepeatType;
import com.saasovation.collaboration.domain.model.calendar.Repetition;
import com.saasovation.collaboration.domain.model.calendar.TimeSpan;
import com.saasovation.collaboration.domain.model.collaborator.CollaboratorService;
import com.saasovation.collaboration.domain.model.collaborator.Owner;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

/**
 *<h3>日历聚合的应用服务</h3>
 *<p>本应用服务主要有以下5个命令：
 *<ul>
 *<li>{@link #changeCalendarDescription}
 *<li>{@link #renameCalendar}
 *<li>{@link #scheduleCalendarEntry}
 *<li>{@link #shareCalendarWith}
 *<li>{@link #inviteesFrom}
 *</ul>
 *<p>
 */
public class CalendarApplicationService {

	/** 日历仓库 **/
    private CalendarRepository calendarRepository;
    /** 日历条目仓库 **/
    private CalendarEntryRepository calendarEntryRepository;
    private CalendarIdentityService calendarIdentityService;
    private CollaboratorService collaboratorService;

    public CalendarApplicationService(
            CalendarRepository aCalendarRepository,
            CalendarEntryRepository aCalendarEntryRepository,
            CalendarIdentityService aCalendarIdentityService,
            CollaboratorService aCollaboratorService) {

        super();

        // 由于不存在相应的setter，所以这里直接赋值。
        this.calendarRepository = aCalendarRepository;
        this.calendarEntryRepository = aCalendarEntryRepository;
        this.calendarIdentityService = aCalendarIdentityService;
        this.collaboratorService = aCollaboratorService;
    }

    /**
     *<h3>修改日历的描述</h3>
     *
     * @param aTenantId
     * @param aCalendarId
     * @param aDescription
     */
    public void changeCalendarDescription(String aTenantId, String aCalendarId, String aDescription) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取日历
        Calendar calendar = this.calendarRepository().calendarOfId(tenant, new CalendarId(aCalendarId));

        // 改：在日历上修改描述
        calendar.changeDescription(aDescription);

        // 入：向仓库中保存日历
        this.calendarRepository().save(calendar);
    }

    /**
     *<h3>创建日历</h3>
     * 
     * @param aTenantId
     * @param aName
     * @param aDescription
     * @param anOwnerId
     * @param aParticipantsToSharedWith
     * @param aCalendarCommandResult 命令结果的载体
     */
    public void createCalendar(
            String aTenantId,
            String aName,
            String aDescription,
            String anOwnerId,
            Set<String> aParticipantsToSharedWith,
            CalendarCommandResult aCalendarCommandResult) {

        Tenant tenant = new Tenant(aTenantId);

        // 获取拥有者
        Owner owner = this.collaboratorService().ownerFrom(tenant, anOwnerId);

        Set<CalendarSharer> sharers = this.sharersFrom(tenant, aParticipantsToSharedWith);

        // 创建日历
        Calendar calendar = new Calendar(tenant, this.calendarRepository.nextIdentity(), aName, aDescription, owner, sharers);

        // 入：向仓库中保存日历
        this.calendarRepository().save(calendar);

        aCalendarCommandResult.resultingCalendarId(calendar.calendarId().id());
    }

    /**
     *<h3>重命名日历</h3>
     *
     * @param aTenantId
     * @param aCalendarId
     * @param aName
     */
    public void renameCalendar(String aTenantId, String aCalendarId, String aName) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取日历
        Calendar calendar = this.calendarRepository().calendarOfId(tenant, new CalendarId(aCalendarId));

        // 改：在日历上重新命名
        calendar.rename(aName);

        // 入：向仓库中保存日历
        this.calendarRepository().save(calendar);
    }

    /**
     * 
     * @param aTenantId
     * @param aCalendarId
     * @param aDescription
     * @param aLocation
     * @param anOwnerId
     * @param aTimeSpanBegins
     * @param aTimeSpanEnds
     * @param aRepeatType
     * @param aRepeatEndsOnDate
     * @param anAlarmType
     * @param anAlarmUnits
     * @param aParticipantsToInvite
     * @param aCalendarCommandResult  命令结果的载体
     */
    public void scheduleCalendarEntry(
            String aTenantId,
            String aCalendarId,
            String aDescription,
            String aLocation,
            String anOwnerId,
            Date aTimeSpanBegins,
            Date aTimeSpanEnds,
            String aRepeatType,
            Date aRepeatEndsOnDate,
            String anAlarmType,
            int anAlarmUnits,
            Set<String> aParticipantsToInvite,
            CalendarCommandResult aCalendarCommandResult) {

        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取日历
        Calendar calendar = this.calendarRepository().calendarOfId(tenant, new CalendarId(aCalendarId));

        // 改：在日历上开始新任务，得到日历条目
        CalendarEntry calendarEntry = calendar.scheduleCalendarEntry(
                    this.calendarIdentityService(),
                    aDescription,
                    aLocation,
                    this.collaboratorService().ownerFrom(tenant, anOwnerId),
                    new TimeSpan(aTimeSpanBegins, aTimeSpanEnds),
                    new Repetition(RepeatType.valueOf(aRepeatType), aRepeatEndsOnDate),
                    new Alarm(AlarmUnitsType.valueOf(anAlarmType), anAlarmUnits),
                    this.inviteesFrom(tenant, aParticipantsToInvite));

        // 入：向仓库中保存日历条目
        this.calendarEntryRepository().save(calendarEntry);

        // 设置当前命令的执行结果
        aCalendarCommandResult.resultingCalendarId(aCalendarId);
        aCalendarCommandResult.resultingCalendarEntryId(calendarEntry.calendarEntryId().id());
    }

    /**
     *<h1>将日历分享给一些订阅者</h1>
     *<p>允许订阅者可以关注拥有者的日历上的活动。
     *
     * @param aTenantId
     * @param aCalendarId
     * @param aParticipantsToSharedWith 日历订阅者集合，由拥有者为每个日历指定
     */
    public void shareCalendarWith(String aTenantId, String aCalendarId, Set<String> aParticipantsToSharedWith) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取日历
        Calendar calendar = this.calendarRepository().calendarOfId(tenant, new CalendarId(aCalendarId));

        // 遍历订阅者集合
        for (CalendarSharer sharer : this.sharersFrom(tenant, aParticipantsToSharedWith)) {
        	// 改：在日历上添加订阅者
            calendar.shareCalendarWith(sharer);
        }

        // 入：向仓库中保存日历
        this.calendarRepository().save(calendar);
    }

    /**
     *<h1>取消日历上的订阅者</h1>
     *<p>取消订阅者，使其不能再关注拥有者的日历上的活动。
     * 
     * @param aTenantId
     * @param aCalendarId
     * @param aParticipantsToUnsharedWith 日历订阅者集合，由拥有者为每个日历指定
     */
    public void unshareCalendarWith(String aTenantId, String aCalendarId, Set<String> aParticipantsToUnsharedWith) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取日历
        Calendar calendar = this.calendarRepository().calendarOfId(tenant, new CalendarId(aCalendarId));

        // 遍历订阅者集合
        for (CalendarSharer sharer : this.sharersFrom(tenant, aParticipantsToUnsharedWith)) {
        	// 改：在日历上取消订阅者
            calendar.unshareCalendarWith(sharer);
        }
        
        // 入：向仓库中保存日历
        this.calendarRepository().save(calendar);
    }

    private CalendarRepository calendarRepository() {
        return this.calendarRepository;
    }

    private CalendarEntryRepository calendarEntryRepository() {
        return this.calendarEntryRepository;
    }

    private CalendarIdentityService calendarIdentityService() {
        return this.calendarIdentityService;
    }

    private CollaboratorService collaboratorService() {
        return this.collaboratorService;
    }

    private Set<Participant> inviteesFrom(Tenant aTenant, Set<String> aParticipantsToInvite) {
        Set<Participant> invitees = new HashSet<Participant>();

        for (String participatnId : aParticipantsToInvite) {
            Participant participant = this.collaboratorService().participantFrom(aTenant, participatnId);

            invitees.add(participant);
        }

        return invitees;
    }

    /**
     *<h1>获取订阅者</h1>
     * @param aTenant
     * @param aParticipantsToSharedWith
     * @return
     */
    private Set<CalendarSharer> sharersFrom(Tenant aTenant, Set<String> aParticipantsToSharedWith) {
    	// 创建订阅者集合
        Set<CalendarSharer> sharers = new HashSet<CalendarSharer>(aParticipantsToSharedWith.size());

        // 遍历获取订阅者对象
        for (String participatnId : aParticipantsToSharedWith) {
            Participant participant = this.collaboratorService().participantFrom(aTenant, participatnId);

            sharers.add(new CalendarSharer(participant));
        }

        return sharers;
    }
}
