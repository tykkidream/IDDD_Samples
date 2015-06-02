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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.release.Release;
import com.saasovation.agilepm.domain.model.product.release.ReleaseId;
import com.saasovation.agilepm.domain.model.product.sprint.Sprint;
import com.saasovation.agilepm.domain.model.product.sprint.SprintId;
import com.saasovation.agilepm.domain.model.team.TeamMember;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEventPublisher;

/**
 *<h3>待定项 - 聚合根</h3>
 *<p>积压的工作, 待解决的问题。
 */
public class BacklogItem extends Entity {

	/** 有关发行ID **/
    private String associatedIssueId;
    /** ID **/
    private BacklogItemId backlogItemId;
    /** 业务优先级 **/
    private BusinessPriority businessPriority;
    /** 类别 **/
    private String category;
    /** 讨论 **/
    private BacklogItemDiscussion discussion;
    /** 讨论起始ID **/
    private String discussionInitiationId;
    /** 产品ID **/
    private ProductId productId;
    /** 版本ID **/
    private ReleaseId releaseId;
    /** 冲刺ID **/
    private SprintId sprintId;
    /** 状态 **/
    private BacklogItemStatus status;
    /** 故事 **/
    private String story;
    /** 故事点 **/
    private StoryPoints storyPoints;
    /** 总结 **/
    private String summary;
    /** 任务 **/
    private Set<Task> tasks;
    /** 承租者ID **/
    private TenantId tenantId;
    /** 类型 **/
    private BacklogItemType type;

    /**
     *<h3>构造BacklogItem</h3>
     *
     *<p>代表了一个新生的BacklogItem对象，由初始化参数构造聚合内的状态。
     * 
     * @param aTenantId
     * @param aProductId
     * @param aBacklogItemId
     * @param aSummary
     * @param aCategory
     * @param aType
     * @param aStatus
     * @param aStoryPoints
     */
    public BacklogItem(
            TenantId aTenantId,
            ProductId aProductId,
            BacklogItemId aBacklogItemId,
            String aSummary,
            String aCategory,
            BacklogItemType aType,
            BacklogItemStatus aStatus,
            StoryPoints aStoryPoints) {

        this();

        this.setBacklogItemId(aBacklogItemId);
        this.setCategory(aCategory);
        this.setDiscussion(
                BacklogItemDiscussion
                    .fromAvailability(DiscussionAvailability.NOT_REQUESTED));
        this.setProductId(aProductId);
        this.setStatus(aStatus);
        this.setStoryPoints(aStoryPoints);
        this.setSummary(aSummary);
        this.setTenantId(aTenantId);
        this.setType(aType);
    }

    public Set<Task> allTasks() {
        return Collections.unmodifiableSet(this.tasks());
    }

    /**
     *<h3>所有任务是否有剩余时间</h3>
     *<p>如果还有剩余时间，返回ture，反之返回false。
     *@return
     */
    public boolean anyTaskHoursRemaining() {
        return this.totalTaskHoursRemaining() > 0;
    }

    public String associatedIssueId() {
        return this.associatedIssueId;
    }

    /**
     *<h3>联想到发行</h3>
     * @param anIssueId
     */
    public void associateWithIssue(String anIssueId) {
        if (this.associatedIssueId == null) {
            this.associatedIssueId = anIssueId;
        }
    }

    /**
     *<h3>分配业务优先级</h3>
     *
     *<p>这是一个CQS命令方法，用于分配业务优先级，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BusinessPriorityAssigned}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     *
     * @param aBusinessPriority
     */
    public void assignBusinessPriority(BusinessPriority aBusinessPriority) {
    	// 业务如此简单，仅仅是修改了一个状态。
    	// 但是这个方法富有业务意义。
        this.setBusinessPriority(aBusinessPriority);

        // 富有业务意义不只是因为方法名，还因为这里有事件。事件还会让人觉得这里将引发在未来的哪个地方继续任务。
        DomainEventPublisher.instance().publish(new BusinessPriorityAssigned(this.tenantId(), this.backlogItemId(), this.businessPriority()));
    }

    /**
     *<h3>分配故事点</h3>
     *
     *<p>这是一个CQS命令方法，用于分配故事点，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemStoryPointsAssigned}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     * @param aStoryPoints
     */
    public void assignStoryPoints(StoryPoints aStoryPoints) {
    	// 业务如此简单，仅仅是修改了一个状态。
    	// 但是这个方法富有业务意义。
        this.setStoryPoints(aStoryPoints);

        // 富有业务意义不只是因为方法名，还因为这里有事件。事件还会让人觉得这里将引发在未来的哪个地方继续任务。
        DomainEventPublisher.instance().publish(new BacklogItemStoryPointsAssigned(this.tenantId(), this.backlogItemId(), this.storyPoints()));
    }

    /**
     *<h3>分配任务志愿者</h3>
     *
     *<p>这是一个CQS命令方法，用于分配任务志愿者，没有返回值。
     *
     *<p>处理过程中，会使用{@link #task(TaskId)}方法根据aTaskId找到{@link Task}对象，然后将
     *aVolunteer传递给{@link Task#assignVolunteer(TeamMember)}来处理。
     *
     * @param aTaskId
     * @param aVolunteer
     */
    public void assignTaskVolunteer(TaskId aTaskId, TeamMember aVolunteer) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.assignVolunteer(aVolunteer);
    }

    public BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    public BusinessPriority businessPriority() {
        return this.businessPriority;
    }

    public String category() {
        return this.category;
    }

    /**
     *<h3>改变类别</h3>
     *
     *<p>这是一个CQS命令方法，用于改变类别，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemCategoryChanged}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     *
     *@param aCategory
     */
    public void changeCategory(String aCategory) {
    	// 业务如此简单，仅仅是修改了一个状态。
    	// 但是这个方法富有业务意义。
        this.setCategory(aCategory);

        // 富有业务意义不只是因为方法名，还因为这里有事件。事件还会让人觉得这里将引发在未来的哪个地方继续任务。
        DomainEventPublisher.instance().publish(new BacklogItemCategoryChanged(this.tenantId(),  this.backlogItemId(), this.category()));
    }

    /**
     *<h3>更改任务状态</h3>
     *
     *<p>这是一个CQS命令方法，用于更改任务状态，没有返回值。
     *
     *<p>处理过程中，会使用{@link #task(TaskId)}方法根据aTaskId找到{@link Task}对象，然后将
     *aStatus传递给{@link Task#changeStatus(TaskStatus))}来处理。
     * 
     * @param aTaskId
     * @param aStatus
     */
    public void changeTaskStatus(TaskId aTaskId, TaskStatus aStatus) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.changeStatus(aStatus);
    }

    /**
     *<h3>改变类别</h3>
     *
     *<p>这是一个CQS命令方法，用于改变类别，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemCategoryChanged}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     * @param aType
     */
    public void changeType(BacklogItemType aType) {
    	// 业务如此简单，仅仅是修改了一个状态。
    	// 但是这个方法富有业务意义。
        this.setType(aType);

        // 富有业务意义不只是因为方法名，还因为这里有事件。事件还会让人觉得这里将引发在未来的哪个地方继续任务。
        DomainEventPublisher.instance().publish(new BacklogItemTypeChanged(this.tenantId(), this.backlogItemId(), this.type()));
    }

    /**
     *<h3>承诺</h3>
     *
     *<p>这是一个CQS命令方法，用于承诺，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemCommitted}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     * @param aSprint
     */
    public void commitTo(Sprint aSprint) {
        this.assertArgumentNotNull(aSprint, "Sprint must not be null.");
        this.assertArgumentEquals(aSprint.tenantId(), this.tenantId(), "Sprint must be of same tenant.");
        this.assertArgumentEquals(aSprint.productId(), this.productId(), "Sprint must be of same product.");

        if (!this.isScheduledForRelease()) {
            throw new IllegalStateException("Must be scheduled for release to commit to sprint.");
        }

        if (this.isCommittedToSprint()) {
            if (!aSprint.sprintId().equals(this.sprintId())) {
            	// 业务1
                this.uncommitFromSprint();
            }
        }

    	// 业务2
        this.elevateStatusWith(BacklogItemStatus.COMMITTED);

    	// 业务3
        this.setSprintId(aSprint.sprintId());

        // 富有业务意义不只是因为方法名，还因为这里有事件。事件还会让人觉得这里将引发在未来的哪个地方继续任务。
        DomainEventPublisher.instance().publish(new BacklogItemCommitted(this.tenantId(), this.backlogItemId(), this.sprintId()));
    }

    /**
     *<h3>定义任务</h3>
     *
     *<p>这是一个CQS命令方法，用于定义任务，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link TaskDefined}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     * @param aVolunteer
     * @param aName
     * @param aDescription
     * @param anHoursRemaining
     */
    public void defineTask(TeamMember aVolunteer, String aName, String aDescription, int anHoursRemaining) {
        Task task = new Task(
                this.tenantId(),
                this.backlogItemId(),
                new TaskId(),
                aVolunteer,
                aName,
                aDescription,
                anHoursRemaining,
                TaskStatus.NOT_STARTED);

        this.tasks().add(task);

        // 富有业务意义不只是因为方法名，还因为这里有事件。事件还会让人觉得这里将引发在未来的哪个地方继续任务。
        DomainEventPublisher.instance().publish(new TaskDefined(
                    this.tenantId(),
                    this.backlogItemId(),
                    task.taskId(),
                    aVolunteer.username(),
                    aName,
                    aDescription,
                    anHoursRemaining));
    }

    /**
     *<h3>描述任务</h3>
     *
     *<p>这是一个CQS命令方法，用于描述任务，没有返回值。
     *
     *<p>处理过程中，会使用{@link #task(TaskId)}方法根据aTaskId找到{@link Task}对象，然后将
     *aDescription传递给{@link Task#describeAs(String)))}来处理。
     * 
     * @param aTaskId
     * @param aDescription
     */
    public void describeTask(TaskId aTaskId, String aDescription) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.describeAs(aDescription);
    }

    public BacklogItemDiscussion discussion() {
        return this.discussion;
    }

    public String discussionInitiationId() {
        return this.discussionInitiationId;
    }

    /**
     *<h3>失败引发的讨论</h3>
     *
     *<p>这是一个CQS命令方法，用于描述任务，没有返回值。
     *
     */
    public void failDiscussionInitiation() {
    	// 检查讨论是否是准备好的。
        if (!this.discussion().availability().isReady()) {
        	// 设置讨论起始ID
            this.setDiscussionInitiationId(null);
            // XXX
            this.setDiscussion(
                    BacklogItemDiscussion
                        .fromAvailability(DiscussionAvailability.FAILED));
        }
    }

    /**
     *<h3>发起讨论</h3>
     *
     *<p>这是一个CQS命令方法，基于现在的讨论通过一个讨论描述发起新讨论，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemDiscussionInitiated}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     *
     * @param aDescriptor
     */
    public void initiateDiscussion(DiscussionDescriptor aDescriptor) {
        if (aDescriptor == null) {
            throw new IllegalArgumentException("The descriptor must not be null.");
        }

        // 检查讨论状态是请求的。
        if (this.discussion().availability().isRequested()) {
        	// XXX
            this.setDiscussion(this.discussion().nowReady(aDescriptor));

            DomainEventPublisher.instance().publish(new BacklogItemDiscussionInitiated(this.tenantId(), this.backlogItemId(), this.discussion()));
        }
    }

    /**
     *<h3>估计任务剩余时间</h3>
     *<p>估算任务的剩余时间，并相应地调整状态。
     *
     * @param aTaskId
     * @param anHoursRemaining
     */
    public void estimateTaskHoursRemaining(TaskId aTaskId, int anHoursRemaining) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        // 估计任务的剩余时间
        task.estimateHoursRemaining(anHoursRemaining);

        BacklogItemStatus changedStatus = null;

        if (anHoursRemaining == 0) {
        	// 如果没有了剩余时间
            if (!this.anyTaskHoursRemaining()) {
                changedStatus = BacklogItemStatus.DONE;
            }
        }
        // 如果当前状态是完成的。
        else if (this.isDone()) {
            // 回归到逻辑之前的状态
            // 因为“完成”不再是合适的
        	
        	// 是否已经提交到冲刺中
            if (this.isCommittedToSprint()) {
                changedStatus = BacklogItemStatus.COMMITTED;
            }
            // 是否已经发布
            else if (this.isScheduledForRelease()) {
                changedStatus = BacklogItemStatus.SCHEDULED;
            } else {
                changedStatus = BacklogItemStatus.PLANNED;
            }
        }

        if (changedStatus != null) {
            this.setStatus(changedStatus);

            DomainEventPublisher.instance().publish(new BacklogItemStatusChanged(this.tenantId(), this.backlogItemId(), changedStatus));
        }
    }

    /**
     *<h3>是否有有业务优先级</h3>
     *<p>如果当前的{@link #businessPriority}不为null返回true。
     * @return
     */
    public boolean hasBusinessPriority() {
        return this.businessPriority() != null;
    }

    /**
     *<h3>发起讨论</h3>
     *
     *<p>这是一个CQS命令方法，发起一个新的讨论，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemDiscussionInitiated}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     *
     * @param aDiscussion
     */
    public void initiateDiscussion(BacklogItemDiscussion aDiscussion) {
        this.setDiscussion(aDiscussion);

        DomainEventPublisher.instance().publish(new BacklogItemDiscussionInitiated(this.tenantId(), this.backlogItemId(),  this.discussion()));
    }

    /**
     *<h3>是否已经提交到冲刺中</h3>
     *<p>如果当前对象有冲刺的ID，说明已经被提交到了冲刺中，返回true。
     * @return
     */
    public boolean isCommittedToSprint() {
        return this.sprintId() != null;
    }

    /**
     *<h3>是否已经完成</h3>
     *<p>检查当前状态{@link #status}的状态是否是完成的。
     * @return
     */
    public boolean isDone() {
        return this.status().isDone();
    }

    /**
     *<h3>是否正在计划中</h3>
     *<p>检查当前状态{@link #status}的状态是否正在计划中。
     * 
     * @return
     */
    public boolean isPlanned() {
        return this.status().isPlanned();
    }

    /**
     *<h3>是否已经被删除</h3>
     *<p>检查当前状态{@link #status}的状态是否已被删除。
     * 
     * @return
     */
    public boolean isRemoved() {
        return this.status().isRemoved();
    }

    /**
     *<h3>是否已经发布</h3>
     *<p>检查当前状态{@link #releaseId}的状态是否是完成的。
     *
     * @return
     */
    public boolean isScheduledForRelease() {
        return this.releaseId() != null;
    }

    /**
     *<h3>标记为删除</h3>
     *
     *<p>这是一个CQS命令方法，将当前对象标记为删除，没有返回值。如果当前对象的状态是{@link BacklogItemStatus#DONE}、
     *{@link BacklogItemStatus#REMOVED}将不能继续此操作，如果当前对象已经提交到冲刺中或者已经发布，将撤消这些操作之后
     *继续标记为删除。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemMarkedAsRemoved}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     *
     */
    public void markAsRemoved() {
        if (this.isRemoved()) {
            throw new IllegalStateException("Already removed, not outstanding.");
        }
        if (this.isDone()) {
            throw new IllegalStateException("Already done, not outstanding.");
        }
        if (this.isCommittedToSprint()) {
            this.uncommitFromSprint();
        }
        if (this.isScheduledForRelease()) {
            this.unscheduleFromRelease();
        }

        this.setStatus(BacklogItemStatus.REMOVED);

        DomainEventPublisher.instance().publish(new BacklogItemMarkedAsRemoved(this.tenantId(), this.backlogItemId()));
    }

    public ProductId productId() {
        return this.productId;
    }

    public ReleaseId releaseId() {
        return this.releaseId;
    }

    /**
     *<h3>删除任务</h3>
     *
     *<p>这是一个CQS命令方法，用于删除任务，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link TaskRemoved}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     *
     * @param aTaskId
     */
    public void removeTask(TaskId aTaskId) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        if (!this.tasks().remove(task)) {
            throw new IllegalStateException("Task was not removed.");
        }

        DomainEventPublisher.instance().publish(new TaskRemoved(this.tenantId(), this.backlogItemId(), aTaskId));
    }

    /**
     * 
     * @param aTaskId
     * @param aName
     */
    public void renameTask(TaskId aTaskId, String aName) {
        Task task = this.task(aTaskId);

        if (task == null) {
            throw new IllegalStateException("Task does not exist.");
        }

        task.rename(aName);
    }

    /**
     *<h3>请求讨论</h3>
     *
     *<p>这是一个CQS命令方法，用于请求讨论，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemDiscussionRequested}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     * @param aDiscussionAvailability
     */
    public void requestDiscussion(DiscussionAvailability aDiscussionAvailability) {
        if (!this.discussion().availability().isReady()) {
            this.setDiscussion(BacklogItemDiscussion.fromAvailability(aDiscussionAvailability));

            DomainEventPublisher.instance().publish(new BacklogItemDiscussionRequested(
                        this.tenantId(),
                        this.productId(),
                        this.backlogItemId(),
                        this.discussion().availability().isRequested()));
        }
    }

    /**
     *<h3>时间安排</h3>
     *
     *<p>这是一个CQS命令方法，用于时间安排，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemScheduled}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     * @param aRelease
     */
    public void scheduleFor(Release aRelease) {
        this.assertArgumentNotNull(aRelease, "Release must not be null.");
        this.assertArgumentEquals(aRelease.tenantId(), this.tenantId(), "Release must be of same tenant.");
        this.assertArgumentEquals(aRelease.productId(), this.productId(), "Release must be of same product.");

        if (this.isScheduledForRelease()) {
            if (!aRelease.releaseId().equals(this.releaseId())) {
                this.unscheduleFromRelease();
            }
        }

        if (this.status().isPlanned()) {
            this.setStatus(BacklogItemStatus.SCHEDULED);
        }

        this.setReleaseId(aRelease.releaseId());

        DomainEventPublisher.instance().publish(new BacklogItemScheduled(this.tenantId(), this.backlogItemId(), this.releaseId()));
    }

    public SprintId sprintId() {
        return this.sprintId;
    }

    /**
     *<h3>开始启动的探讨</h3>
     *<p>这是一个CQS命令方法，用于开始启动的探讨，没有返回值。
     *
     * @param aDiscussionInitiationId
     */
    public void startDiscussionInitiation(String aDiscussionInitiationId) {
        if (!this.discussion().availability().isReady()) {
            this.setDiscussionInitiationId(aDiscussionInitiationId);
        }
    }

    public String story() {
        return this.story;
    }

    public StoryPoints storyPoints() {
        return this.storyPoints;
    }

    public String summary() {
        return this.summary;
    }

    /**
     *<h3>总结</h3>
     *
     *<p>这是一个CQS命令方法，用于总结，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemSummarized}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     *
     * @param aSummary
     */
    public void summarize(String aSummary) {
        this.setSummary(aSummary);

        DomainEventPublisher.instance().publish(new BacklogItemSummarized(this.tenantId(), this.backlogItemId(), this.summary()));
    }

    /**
     *<h3><h3>
     *
     *@param aTaskId
     *@return
     */
    public Task task(TaskId aTaskId) {
        for (Task task : this.tasks()) {
            if (task.taskId().equals(aTaskId)) {
                return task;
            }
        }

        return null;
    }

    /**
     *<h3>告诉故事</h3>
     *
     *<p>这是一个CQS命令方法，用于告诉故事，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemStoryTold}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     * @param aStory
     */
    public void tellStory(String aStory) {
        this.setStory(aStory);

        DomainEventPublisher.instance().publish(new BacklogItemStoryTold(this.tenantId(), this.backlogItemId(), this.story()));
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    /**
     *<h3>获取总的任务剩余时间</h3>
     *
     * @return
     */
    public int totalTaskHoursRemaining() {
        int totalHoursRemaining = 0;

        for (Task task : this.tasks()) {
            totalHoursRemaining += task.hoursRemaining();
        }

        return totalHoursRemaining;
    }

    public BacklogItemType type() {
        return this.type;
    }

    /**
     *<h3>取消提交到冲刺</h3>
     *
     *<p>这是一个CQS命令方法，用于取消提交到冲刺，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemUncommitted}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     */
    public void uncommitFromSprint() {
        if (!this.isCommittedToSprint()) {
            throw new IllegalStateException("Not currently committed.");
        }

        this.setStatus(BacklogItemStatus.SCHEDULED);
        SprintId uncommittedSprintId = this.sprintId();
        this.setSprintId(null);

        DomainEventPublisher.instance().publish(new BacklogItemUncommitted(this.tenantId(), this.backlogItemId(), uncommittedSprintId));
    }

    /**
     *<h3>取消调度从发布</h3>
     *
     *<p>这是一个CQS命令方法，用于取消调度从发布，没有返回值。
     *
     *<p>处理完成后，将参数封装到一个新的{@link BacklogItemUnscheduled}事件对象中，并发
     *布它。更多关于事件的信息参考{@link DomainEventPublisher}。
     * 
     */
    public void unscheduleFromRelease() {
        if (this.isCommittedToSprint()) {
            throw new IllegalStateException("Must first uncommit.");
        }
        if (!this.isScheduledForRelease()) {
            throw new IllegalStateException("Not scheduled for release.");
        }

        this.setStatus(BacklogItemStatus.PLANNED);
        ReleaseId unscheduledReleaseId = this.releaseId();
        this.setReleaseId(null);

        DomainEventPublisher.instance().publish(new BacklogItemUnscheduled(this.tenantId(), this.backlogItemId(), unscheduledReleaseId));
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            BacklogItem typedObject = (BacklogItem) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.productId().equals(typedObject.productId()) &&
                this.backlogItemId().equals(typedObject.backlogItemId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (34685 * 7)
            + this.tenantId().hashCode()
            + this.productId().hashCode()
            + this.backlogItemId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "BacklogItem [tenantId=" + tenantId + ", productId=" + productId
                + ", backlogItemId=" + backlogItemId
                + ", businessPriority=" + businessPriority
                + ", category=" + category + ", discussion=" + discussion
                + ", releaseId=" + releaseId + ", sprintId=" + sprintId
                + ", status=" + status + ", story=" + story
                + ", storyPoints=" + storyPoints + ", summary=" + summary
                + ", tasks=" + tasks + ", type=" + type + "]";
    }

    /**
     *<h3>构造BacklogItem</h3>
     *
     *<p>此构造函数没有参数，构造过程中只发生一个对{@link #tasks}的初始化。
     *
     *<p>注意，此函数是private的，外部无法使用，所以在创建实例时只能使用其它带参的构造函数，
     *并且提供必要的参数以满足聚合所需最少的状态。
     * 
     */
    private BacklogItem() {
        super();

        this.setTasks(new HashSet<Task>(0));
    }

    private void setBacklogItemId(BacklogItemId aBacklogItemId) {
        this.assertArgumentNotNull(aBacklogItemId, "The backlogItemId must be provided.");

        this.backlogItemId = aBacklogItemId;
    }

    private void setBusinessPriority(BusinessPriority aBusinessPriority) {
        this.businessPriority = aBusinessPriority;
    }

    private void setCategory(String aCategory) {
        this.assertArgumentNotEmpty(aCategory, "The category must be provided.");
        this.assertArgumentLength(aCategory, 25, "The category must be 25 characters or less.");

        this.category = aCategory;
    }

    private void setDiscussion(BacklogItemDiscussion aDiscussion) {
        this.discussion = aDiscussion;
    }

    private void setDiscussionInitiationId(String aDiscussionInitiationId) {
        if (aDiscussionInitiationId != null) {
            this.assertArgumentLength(aDiscussionInitiationId, 100, "Discussion initiation identity must be 100 characters or less.");
        }

        this.discussionInitiationId = aDiscussionInitiationId;
    }

    private void setProductId(ProductId aProductId) {
        this.assertArgumentNotNull(aProductId, "The product id must be provided.");

        this.productId = aProductId;
    }

    private void setReleaseId(ReleaseId aReleaseId) {
        this.releaseId = aReleaseId;
    }

    private void setSprintId(SprintId aSprintId) {
        this.sprintId = aSprintId;
    }

    /**
     *<h3>获取当前状态</h3>
     *<p>注意此方法是private的。
     * @return
     */
    private BacklogItemStatus status() {
        return this.status;
    }

    /**
     *<h3>提升当前状态</h3>
     *
     * @param aStatus
     */
    private void elevateStatusWith(BacklogItemStatus aStatus) {
    	// 如果当前状态是预定的
        if (this.status().isScheduled()) {
            this.setStatus(BacklogItemStatus.COMMITTED);
        }
    }

    private void setStatus(BacklogItemStatus aStatus) {
        this.status = aStatus;
    }

    private void setStory(String aStory) {
        if (aStory != null) {
            this.assertArgumentLength(aStory, 65000, "The story must be 65000 characters or less.");
        }

        this.story = aStory;
    }

    private void setStoryPoints(StoryPoints aStoryPoints) {
        this.storyPoints = aStoryPoints;
    }

    private void setSummary(String aSummary) {
        this.assertArgumentNotEmpty(aSummary, "The summary must be provided.");
        this.assertArgumentLength(aSummary, 100, "The summary must be 100 characters or less.");

        this.summary = aSummary;
    }

    private Set<Task> tasks() {
        return this.tasks;
    }

    private void setTasks(Set<Task> aTasks) {
        this.tasks = aTasks;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenant id must be provided.");

        this.tenantId = aTenantId;
    }

    private void setType(BacklogItemType aType) {
        this.assertArgumentNotNull(aType, "The backlog item type must be provided.");

        this.type = aType;
    }
}
