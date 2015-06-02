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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.team.TeamMember;
import com.saasovation.agilepm.domain.model.team.TeamMemberId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEventPublisher;

/**
 *<h3>任务 - 聚合</h3>
 *
 */
public class Task extends Entity {

    /**
     * 待定项ID
     */
    private BacklogItemId backlogItemId;
    /**
     * 描述
     */
    private String description;
    /**
     * 日志
     */
    private List<EstimationLogEntry> estimationLog;
    /**
     * 剩余时间
     */
    private int hoursRemaining;
    /**
     * 名称
     */
    private String name;
    /**
     * 状态
     */
    private TaskStatus status;
    /**
     * ID
     */
    private TaskId taskId;
    /**
     * 承租者ID
     */
    private TenantId tenantId;
    /**
     * 志愿者ID
     */
    private TeamMemberId volunteer;

    public List<EstimationLogEntry> allEstimationLogEntries() {
        return Collections.unmodifiableList(this.estimationLog());
    }

    public String description() {
        return this.description;
    }

    public String name() {
        return this.name;
    }

    public TaskStatus status() {
        return this.status;
    }

    public TeamMemberId volunteer() {
        return this.volunteer;
    }

    /**
     *<h3>设置志愿者</h3>
     *<p>protected？
     *@param aVolunteer
     */
    protected void setVolunteer(TeamMemberId aVolunteer) {
        this.assertArgumentNotNull(aVolunteer, "The volunteer id must be provided.");
        this.assertArgumentEquals(this.tenantId(), aVolunteer.tenantId(), "The volunteer must be of the same tenant.");

        this.volunteer = aVolunteer;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Task typedObject = (Task) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.backlogItemId().equals(typedObject.backlogItemId()) &&
                this.taskId().equals(typedObject.taskId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (73721 * 23)
            + this.tenantId().hashCode()
            + this.backlogItemId().hashCode()
            + this.taskId().hashCode();

        return hashCodeValue;
    }

    /**
     *<h3></h3>
     *@param aTenantId
     *@param aBacklogItemId
     *@param aTaskId
     *@param aVolunteer
     *@param aName
     *@param aDescription
     *@param aHoursRemaining
     *@param aStatus
     */
    protected Task(
            TenantId aTenantId,
            BacklogItemId aBacklogItemId,
            TaskId aTaskId,
            TeamMember aVolunteer,
            String aName,
            String aDescription,
            int aHoursRemaining,
            TaskStatus aStatus) {

        this();

        this.setBacklogItemId(aBacklogItemId);
        this.setDescription(aDescription);
        this.setHoursRemaining(aHoursRemaining);
        this.setName(aName);
        this.setStatus(aStatus);
        this.setTaskId(aTaskId);
        this.setTenantId(aTenantId);
        this.setVolunteer(aVolunteer.teamMemberId());
    }

    /**
     *<h3></h3>
     */
    private Task() {
        super();

        this.setEstimationLog(new ArrayList<EstimationLogEntry>(0));
    }

    /**
     *<h3>分配志愿者</h3>
     *<p>这是一个CQS命令方法，用于分配志愿者，没有返回值。
     *<p>注意此方法为protected。
     *@param aVolunteer
     */
    protected void assignVolunteer(TeamMember aVolunteer) {
    	// 业务如此简单，仅仅是修改了一个状态。
    	// 但是这个方法富有业务意义。
        this.setVolunteer(aVolunteer.teamMemberId());

        DomainEventPublisher.instance().publish(new TaskVolunteerAssigned(this.tenantId(), this.backlogItemId(), this.taskId(), this.volunteer().id()));
    }

    /**
     *<h3>改变状态</h3>
     *<p>这是一个CQS命令方法，用于改变状态，没有返回值。
     *<p>注意此方法为protected。
     *@param aStatus
     */
    protected void changeStatus(TaskStatus aStatus) {
        this.setStatus(aStatus);

        DomainEventPublisher.instance().publish(new TaskStatusChanged(this.tenantId(), this.backlogItemId(), this.taskId(), this.status()));
    }

    /**
     *<h3>修改描述</h3>
     *<p>这是一个CQS命令方法，用于修改描述，没有返回值。
     *<p>注意此方法为protected。
     *@param aDescription
     */
    protected void describeAs(String aDescription) {
        this.setDescription(aDescription);

        DomainEventPublisher.instance().publish(new TaskDescribed(this.tenantId(), this.backlogItemId(), this.taskId(), this.description()));
    }

    /**
     *<h3>估计剩余时间</h3>
     *<p>这是一个CQS命令方法，用于估计剩余时间，没有返回值。
     *@param anHoursRemaining
     */
    protected void estimateHoursRemaining(int anHoursRemaining) {
        if (anHoursRemaining < 0) {
            throw new IllegalArgumentException("Hours reminaing is illegal: " + anHoursRemaining);
        }

        if (anHoursRemaining != this.hoursRemaining()) {
        	// 改变剩余时间
            this.setHoursRemaining(anHoursRemaining);

            DomainEventPublisher.instance().publish(new TaskHoursRemainingEstimated(this.tenantId(), this.backlogItemId(), this.taskId(), this.hoursRemaining()));

        	// 改变状态
            if (anHoursRemaining == 0 && !this.status().isDone()) {
                this.changeStatus(TaskStatus.DONE);
            } else if (anHoursRemaining > 0 && !this.status().isInProgress()) {
                this.changeStatus(TaskStatus.IN_PROGRESS);
            }

            this.logEstimation(anHoursRemaining);
        }
    }

    /**
     *<h3>重命名</h3>
     *<p>这是一个CQS命令方法，用于重命名，没有返回值。
     *@param aName
     */
    protected void rename(String aName) {
        this.setName(aName);

        DomainEventPublisher.instance().publish(new TaskRenamed(this.tenantId(), this.backlogItemId(), this.taskId(), this.name()));
    }

    /**
     *<h3></h3>
     *@return
     */
    protected BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    protected void setBacklogItemId(BacklogItemId aBacklogItemId) {
        if (aBacklogItemId == null) {
            throw new IllegalArgumentException("The backlogItemId is required.");
        }

        this.backlogItemId = aBacklogItemId;
    }

    protected void setDescription(String aDescription) {
        if (aDescription == null || aDescription.length() == 0) {
            throw new IllegalArgumentException("Description is required.");
        }
        if (aDescription.length() > 65000) {
            throw new IllegalArgumentException("Description must be 65000 characters or less.");
        }

        this.description = aDescription;
    }

    protected List<EstimationLogEntry> estimationLog() {
        return this.estimationLog;
    }

    protected void setEstimationLog(List<EstimationLogEntry> anEstimationLog) {
        this.estimationLog = anEstimationLog;
    }

    protected int hoursRemaining() {
        return this.hoursRemaining;
    }

    protected void setHoursRemaining(int aHoursRemaining) {
        this.hoursRemaining = aHoursRemaining;
    }

    protected void setName(String aName) {
        if (aName == null || aName.length() == 0) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (aName.length() > 100) {
            throw new IllegalArgumentException("Name must be 100 characters or less.");
        }

        this.name = aName;
    }

    protected void setStatus(TaskStatus aStatus) {
        if (aStatus == null) {
            throw new IllegalArgumentException("Status is required.");
        }

        this.status = aStatus;
    }

    protected TaskId taskId() {
        return this.taskId;
    }

    protected void setTaskId(TaskId aTaskId) {
        if (aTaskId == null) {
            throw new IllegalArgumentException("The taskId is required.");
        }
        this.taskId = aTaskId;
    }

    protected TenantId tenantId() {
        return this.tenantId;
    }

    protected void setTenantId(TenantId aTenantId) {
        if (aTenantId == null) {
            throw new IllegalArgumentException("The tenantId is required.");
        }

        this.tenantId = aTenantId;
    }

    /**
     *<h3></h3>
     *@param anHoursRemaining
     */
    private void logEstimation(int anHoursRemaining) {
        Date today = EstimationLogEntry.currentLogDate();

        boolean updatedLogForToday = false;

        Iterator<EstimationLogEntry> iterator = this.estimationLog().iterator();

        while (!updatedLogForToday && iterator.hasNext()) {
            EstimationLogEntry entry = iterator.next();

            updatedLogForToday =
                    entry.updateHoursRemainingWhenDateMatches(
                            anHoursRemaining,
                            today);
        }

        if (!updatedLogForToday) {
            this.estimationLog().add(
                    new EstimationLogEntry(
                            this.tenantId(),
                            this.taskId(),
                            today,
                            anHoursRemaining));
        }
    }
}
