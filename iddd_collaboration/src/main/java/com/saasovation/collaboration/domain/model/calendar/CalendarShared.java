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

import java.util.Date;

import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;

/**
 *<h3>添加日历共享的新用户 - 领域事件</h3>
 *
 */
public class CalendarShared implements DomainEvent {

	/**  日历ID **/
    private CalendarId calendarId;
    /**  （接收）共享者 **/
    private CalendarSharer calendarSharer;
    /**  事件版本**/
    private int eventVersion;
    /**  名称 **/
    private String name;
    /**  发生时间 **/
    private Date occurredOn;
    /**  日历订阅方 **/
    private Tenant tenant;

    public CalendarShared(
            Tenant aTenant,
            CalendarId aCalendarId,
            String aName,
            CalendarSharer aCalendarSharer) {

        super();

        this.calendarId = aCalendarId;
        this.calendarSharer = aCalendarSharer;
        this.eventVersion = 1;
        this.name = aName;
        this.occurredOn = new Date();
        this.tenant = aTenant;
    }

    public CalendarId calendarId() {
        return this.calendarId;
    }

    public CalendarSharer calendarSharer() {
        return this.calendarSharer;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    public String name() {
        return this.name;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    public Tenant tenant() {
        return this.tenant;
    }
}
