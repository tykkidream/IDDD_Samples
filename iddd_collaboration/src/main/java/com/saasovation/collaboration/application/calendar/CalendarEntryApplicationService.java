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

import com.saasovation.collaboration.domain.model.calendar.Alarm;
import com.saasovation.collaboration.domain.model.calendar.AlarmUnitsType;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntry;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryId;
import com.saasovation.collaboration.domain.model.calendar.CalendarEntryRepository;
import com.saasovation.collaboration.domain.model.calendar.RepeatType;
import com.saasovation.collaboration.domain.model.calendar.Repetition;
import com.saasovation.collaboration.domain.model.calendar.TimeSpan;
import com.saasovation.collaboration.domain.model.collaborator.CollaboratorService;
import com.saasovation.collaboration.domain.model.collaborator.Participant;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

public class CalendarEntryApplicationService {

    private CalendarEntryRepository calendarEntryRepository;
    private CollaboratorService collaboratorService;

    public CalendarEntryApplicationService(CalendarEntryRepository aCalendarEntryRepository, CollaboratorService aCollaboratorService) {
        super();

        this.calendarEntryRepository = aCalendarEntryRepository;
        this.collaboratorService = aCollaboratorService;
    }

    /**
     *<h3>修改条目的描述</h3>
     *@param aTenantId
     *@param aCalendarEntryId
     *@param aDescription
     */
    public void changeCalendarEntryDescription(String aTenantId, String aCalendarEntryId, String aDescription) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取条目
        CalendarEntry calendarEntry = this.calendarEntryRepository().calendarEntryOfId(tenant, new CalendarEntryId(aCalendarEntryId));

        // 改：在条目上修改让描述
        calendarEntry.changeDescription(aDescription);

        // 入：向仓库中保存条目
        this.calendarEntryRepository().save(calendarEntry);
    }

    /**
     *<h3>邀请参与者</h3>
     *@param aTenantId
     *@param aCalendarEntryId
     *@param aParticipantsToInvite
     */
    public void inviteCalendarEntryParticipant(String aTenantId, String aCalendarEntryId, Set<String> aParticipantsToInvite) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取条目
        CalendarEntry calendarEntry = this.calendarEntryRepository().calendarEntryOfId(tenant, new CalendarEntryId(aCalendarEntryId));

        for (Participant participant : this.inviteesFrom(tenant, aParticipantsToInvite)) {
        	// 改：在条目上添加参与者
            calendarEntry.invite(participant);
        }

        // 入：向仓库中保存条目
        this.calendarEntryRepository().save(calendarEntry);
    }

    /**
     *<h3>修改待办事项的地址</h3>
     *@param aTenantId
     *@param aCalendarEntryId
     *@param aLocation
     */
    public void relocateCalendarEntry(String aTenantId, String aCalendarEntryId, String aLocation) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取条目
        CalendarEntry calendarEntry = this.calendarEntryRepository().calendarEntryOfId(tenant, new CalendarEntryId(aCalendarEntryId));

        // 改：在条目上修改地址
        calendarEntry.relocate(aLocation);

        // 入：向仓库中保存条目
        this.calendarEntryRepository().save(calendarEntry);
    }

    /**
     *<h3>重新设置日历条目</h3>
     *@param aTenantId
     *@param aCalendarEntryId
     *@param aDescription
     *@param aLocation
     *@param aTimeSpanBegins
     *@param aTimeSpanEnds
     *@param aRepeatType
     *@param aRepeatEndsOnDate
     *@param anAlarmType
     *@param anAlarmUnits
     */
    public void rescheduleCalendarEntry(
            String aTenantId,
            String aCalendarEntryId,
            String aDescription,
            String aLocation,
            Date aTimeSpanBegins,
            Date aTimeSpanEnds,
            String aRepeatType,
            Date aRepeatEndsOnDate,
            String anAlarmType,
            int anAlarmUnits) {

        Tenant tenant = new Tenant(aTenantId);
        
        // 出：从仓库中提取条目
        CalendarEntry calendarEntry = this.calendarEntryRepository().calendarEntryOfId(tenant, new CalendarEntryId(aCalendarEntryId));
        
        // 改：在条目上修改重置
        calendarEntry.reschedule(aDescription, aLocation,new TimeSpan(aTimeSpanBegins, aTimeSpanEnds),
                new Repetition(RepeatType.valueOf(aRepeatType), aRepeatEndsOnDate),
                new Alarm(AlarmUnitsType.valueOf(anAlarmType), anAlarmUnits));

        // 入：向仓库中保存条目
        this.calendarEntryRepository().save(calendarEntry);
    }

    /**
     *<h3>取消参与者</h3>
     *@param aTenantId
     *@param aCalendarEntryId
     *@param aParticipantsToInvite
     */
    public void uninviteCalendarEntryParticipant(String aTenantId, String aCalendarEntryId, Set<String> aParticipantsToInvite) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取条目
        CalendarEntry calendarEntry = this.calendarEntryRepository().calendarEntryOfId(tenant, new CalendarEntryId(aCalendarEntryId));

        for (Participant participant : this.inviteesFrom(tenant, aParticipantsToInvite)) {
        	// 改：在条目上取消参与者
            calendarEntry.uninvite(participant);
        }

        // 入：向仓库中保存条目
        this.calendarEntryRepository().save(calendarEntry);
    }

    private CalendarEntryRepository calendarEntryRepository() {
        return this.calendarEntryRepository;
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
}
