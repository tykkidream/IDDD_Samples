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

package com.saasovation.agilepm.domain.model.product.backlogitem;

import java.util.Date;

import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEvent;

/**
 *<h3>任务被定义 - 领域事件</h3>
 *
 */
public class TaskDefined implements DomainEvent {

    /**
     * 待定项ID
     */
    private BacklogItemId backlogItemId;
    /**
     * 描述
     */
    private String description;
    /**
     * 事件版本
     */
    private int eventVersion;
    /**
     * 剩余时间
     */
    private int hoursRemaining;
    /**
     * 名称
     */
    private String name;
    /**
     * 事件发生时间
     */
    private Date occurredOn;
    /**
     * 任务ID
     */
    private TaskId taskId;
    /**
     * 承租者ID
     */
    private TenantId tenantId;
    /**
     * 志愿者ID
     */
    private String volunteerMemberId;

    public TaskDefined(
            TenantId aTenantId,
            BacklogItemId aBacklogItemId,
            TaskId aTaskId,
            String aVolunteerMemberId,
            String aName,
            String aDescription,
            int aHoursRemaining) {

        super();

        this.backlogItemId = aBacklogItemId;
        this.description = aDescription;
        this.eventVersion = 1;
        this.hoursRemaining = aHoursRemaining;
        this.name = aName;
        this.occurredOn = new Date();
        this.taskId = aTaskId;
        this.tenantId = aTenantId;
        this.volunteerMemberId = aVolunteerMemberId;
    }

    public BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    public String description() {
        return this.description;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    public int hoursRemaining() {
        return this.hoursRemaining;
    }

    public String name() {
        return this.name;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    public TaskId taskId() {
        return this.taskId;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public String volunteerMemberId() {
        return this.volunteerMemberId;
    }
}
