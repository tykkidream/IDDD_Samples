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

package com.saasovation.agilepm.domain.model.product;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItem;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemId;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemStatus;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemType;
import com.saasovation.agilepm.domain.model.product.backlogitem.StoryPoints;
import com.saasovation.agilepm.domain.model.product.release.Release;
import com.saasovation.agilepm.domain.model.product.release.ReleaseId;
import com.saasovation.agilepm.domain.model.product.sprint.Sprint;
import com.saasovation.agilepm.domain.model.product.sprint.SprintId;
import com.saasovation.agilepm.domain.model.team.ProductOwner;
import com.saasovation.agilepm.domain.model.team.ProductOwnerId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.DomainEventPublisher;

/**
 * <h3>产品 - 聚合根<h3>
 *
 */
public class Product extends Entity {

	/** 产品待办事项列表 **/
    private Set<ProductBacklogItem> backlogItems;
    /** 描述 **/
    private String description;
    /** 产品的讨论 **/
    private ProductDiscussion discussion;
    /** 讨论起始ID **/
    private String discussionInitiationId;
    /** 名称 **/
    private String name;
    /** ID **/
    private ProductId productId;
    /** 拥有者ID **/
    private ProductOwnerId productOwnerId;
    /** 租户ID **/
    private TenantId tenantId;

    /**
     *<h3></h3>
     *
     * @param aTenantId
     * @param aProductId
     * @param aProductOwnerId
     * @param aName
     * @param aDescription
     * @param aDiscussionAvailability
     */
    public Product(
            TenantId aTenantId,
            ProductId aProductId,
            ProductOwnerId aProductOwnerId,
            String aName,
            String aDescription,
            DiscussionAvailability aDiscussionAvailability) {

        this();

        this.setTenantId(aTenantId); // must precede productOwnerId for compare
        this.setDescription(aDescription);
        this.setDiscussion(ProductDiscussion.fromAvailability(aDiscussionAvailability));
        this.setDiscussionInitiationId(null);
        this.setName(aName);
        this.setProductId(aProductId);
        this.setProductOwnerId(aProductOwnerId);

        DomainEventPublisher.instance().publish(new ProductCreated(
                    this.tenantId(),
                    this.productId(),
                    this.productOwnerId(),
                    this.name(),
                    this.description(),
                    this.discussion().availability().isRequested()));
    }

    public Set<ProductBacklogItem> allBacklogItems() {
        return Collections.unmodifiableSet(this.backlogItems());
    }

    public void changeProductOwner(ProductOwner aProductOwner) {
        if (!this.productOwnerId().equals(aProductOwner.productOwnerId())) {
            this.setProductOwnerId(aProductOwner.productOwnerId());

            // TODO: publish event
        }
    }

    public String description() {
        return this.description;
    }

    public ProductDiscussion discussion() {
        return this.discussion;
    }

    public String discussionInitiationId() {
        return this.discussionInitiationId;
    }

    /**
     *<h3></h3>
     */
    public void failDiscussionInitiation() {
        if (!this.discussion().availability().isReady()) {
            this.setDiscussionInitiationId(null);
            this.setDiscussion(ProductDiscussion.fromAvailability(DiscussionAvailability.FAILED));
        }
    }

    /**
     *<h3>发起讨论</h3>
     *
     *<p>这是一个 CQS 命令方法，用于发起讨论，没有返回值。
     *
     *<p>当前聚合中有一个 {@link ProductDiscussion} 属性，其中有一个 {@link DiscussionDescriptor} 属性
     *，处理过程仅仅将参数改变此属性，最后产生一个新的 ProductDiscussion 做为当前聚合的新属性。
     *
     *<p>当处理完成时，将一些数据封装到一个新的 {@link ProductDiscussionInitiated} 事件对象中，并发
     *布它。可参考事件发布 {@link DomainEventPublisher#publish(DomainEvent)} 。
     *
     *@param aDescriptor
     */
    public void initiateDiscussion(DiscussionDescriptor aDescriptor) {
        if (aDescriptor == null) {
            throw new IllegalArgumentException("The descriptor must not be null.");
        }

        if (this.discussion().availability().isRequested()) {
            this.setDiscussion(this.discussion().nowReady(aDescriptor));

            // 发布事件
            // 当有事件监听器订阅此事件时，将开始长时处理过程。
            DomainEventPublisher.instance().publish(new ProductDiscussionInitiated(this.tenantId(), this.productId(), this.discussion()));
        }
    }

    public String name() {
        return this.name;
    }

    /**
     *<h3></h3>
     *@param aNewBacklogItemId
     *@param aSummary
     *@param aCategory
     *@param aType
     *@param aStoryPoints
     *@return
     */
    public BacklogItem planBacklogItem(
            BacklogItemId aNewBacklogItemId,
            String aSummary,
            String aCategory,
            BacklogItemType aType,
            StoryPoints aStoryPoints) {

        BacklogItem backlogItem = new BacklogItem(
                    this.tenantId(),
                    this.productId(),
                    aNewBacklogItemId,
                    aSummary,
                    aCategory,
                    aType,
                    BacklogItemStatus.PLANNED,
                    aStoryPoints);

        DomainEventPublisher.instance().publish(new ProductBacklogItemPlanned(
                    backlogItem.tenantId(),
                    backlogItem.productId(),
                    backlogItem.backlogItemId(),
                    backlogItem.summary(),
                    backlogItem.category(),
                    backlogItem.type(),
                    backlogItem.storyPoints()));

        return backlogItem;
    }

    public void plannedProductBacklogItem(BacklogItem aBacklogItem) {
        this.assertArgumentEquals(this.tenantId(), aBacklogItem.tenantId(), "The product and backlog item must have same tenant.");
        this.assertArgumentEquals(this.productId(), aBacklogItem.productId(), "The backlog item must belong to product.");

        int ordering = this.backlogItems().size() + 1;

        ProductBacklogItem productBacklogItem =
                new ProductBacklogItem(
                        this.tenantId(),
                        this.productId(),
                        aBacklogItem.backlogItemId(),
                        ordering);

        this.backlogItems().add(productBacklogItem);
    }

    public ProductId productId() {
        return this.productId;
    }

    public ProductOwnerId productOwnerId() {
        return this.productOwnerId;
    }

    public void reorderFrom(BacklogItemId anId, int anOrdering) {
        for (ProductBacklogItem productBacklogItem : this.backlogItems()) {
            productBacklogItem.reorderFrom(anId, anOrdering);
        }
    }

    /**
     *<h3>申请讨论</h3>
     *
     *<p>这是一个 CQS 命令方法，用于发证讨论状态，没有返回值。
     *
     *<p>当时聚合中有一个 {@link ProductDiscussion} 属性，它有一个 {@link DiscussionAvailability} 属性，
     *处理过程仅仅将参数赋与此属性，最后产生一个新的 ProductDiscussion 做为当前聚合的新属性。
     *
     *<p>处理最后，将参数封装到一个新的 {@link ProductDiscussionRequested} 事件对象中，并发布它。
     *
     *<p>具体的事件发布机制参考 {@link DomainEventPublisher#publish(DomainEvent)} 。
     *
     *@param aDiscussionAvailability
     */
    public void requestDiscussion(DiscussionAvailability aDiscussionAvailability) {
        if (!this.discussion().availability().isReady()) {
            this.setDiscussion(ProductDiscussion.fromAvailability(aDiscussionAvailability));

            DomainEventPublisher.instance().publish(new ProductDiscussionRequested(
                        this.tenantId(),
                        this.productId(),
                        this.productOwnerId(),
                        this.name(),
                        this.description(),
                        this.discussion().availability().isRequested()));
        }
    }

    public Release scheduleRelease(
            ReleaseId aNewReleaseId,
            String aName,
            String aDescription,
            Date aBegins,
            Date anEnds) {

        Release release =
            new Release(
                    this.tenantId(),
                    this.productId(),
                    aNewReleaseId,
                    aName,
                    aDescription,
                    aBegins,
                    anEnds);

        DomainEventPublisher
            .instance()
            .publish(new ProductReleaseScheduled(
                    release.tenantId(),
                    release.productId(),
                    release.releaseId(),
                    release.name(),
                    release.description(),
                    release.begins(),
                    release.ends()));

        return release;
    }

    public Sprint scheduleSprint(
            SprintId aNewSprintId,
            String aName,
            String aGoals,
            Date aBegins,
            Date anEnds) {

        Sprint sprint =
            new Sprint(
                    this.tenantId(),
                    this.productId(),
                    aNewSprintId,
                    aName,
                    aGoals,
                    aBegins,
                    anEnds);

        DomainEventPublisher
            .instance()
            .publish(new ProductSprintScheduled(
                    sprint.tenantId(),
                    sprint.productId(),
                    sprint.sprintId(),
                    sprint.name(),
                    sprint.goals(),
                    sprint.begins(),
                    sprint.ends()));

        return sprint;
    }

    /**
     *<h3></h3>
     *@param aDiscussionInitiationId
     */
    public void startDiscussionInitiation(String aDiscussionInitiationId) {
        if (!this.discussion().availability().isReady()) {
            this.setDiscussionInitiationId(aDiscussionInitiationId);
        }
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Product typedObject = (Product) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.productId().equals(typedObject.productId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (2335 * 3)
            + this.tenantId().hashCode()
            + this.productId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Product [tenantId=" + tenantId + ", productId=" + productId
                + ", backlogItems=" + backlogItems + ", description="
                + description + ", discussion=" + discussion
                + ", discussionInitiationId=" + discussionInitiationId
                + ", name=" + name + ", productOwnerId=" + productOwnerId + "]";
    }

    private Product() {
        super();

        this.setBacklogItems(new HashSet<ProductBacklogItem>(0));
    }

    private Set<ProductBacklogItem> backlogItems() {
        return this.backlogItems;
    }

    private void setBacklogItems(Set<ProductBacklogItem> backlogItems) {
        this.backlogItems = backlogItems;
    }

    private void setDescription(String aDescription) {
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");
        this.assertArgumentLength(aDescription, 500, "Description must be 500 characters or less.");

        this.description = aDescription;
    }

    private void setDiscussion(ProductDiscussion aDiscussion) {
        this.assertArgumentNotNull(aDiscussion, "The discussion is required even if it is unused.");

        this.discussion = aDiscussion;
    }

    private void setDiscussionInitiationId(String aDiscussionInitiationId) {
        if (aDiscussionInitiationId != null) {
            this.assertArgumentLength(
                    aDiscussionInitiationId,
                    100,
                    "Discussion initiation identity must be 100 characters or less.");
        }

        this.discussionInitiationId = aDiscussionInitiationId;
    }

    private void setName(String aName) {
        this.assertArgumentNotEmpty(aName, "The name must be provided.");
        this.assertArgumentLength(aName, 100, "The name must be 100 characters or less.");

        this.name = aName;
    }

    private void setProductId(ProductId aProductId) {
        this.assertArgumentNotNull(aProductId, "The productId must be provided.");

        this.productId = aProductId;
    }

    private void setProductOwnerId(ProductOwnerId aProductOwnerId) {
        this.assertArgumentNotNull(aProductOwnerId, "The productOwnerId must be provided.");
        this.assertArgumentEquals(this.tenantId(), aProductOwnerId.tenantId(), "The productOwner must have the same tenant.");

        this.productOwnerId = aProductOwnerId;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenantId must be provided.");

        this.tenantId = aTenantId;
    }
}
