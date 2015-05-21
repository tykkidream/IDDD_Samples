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

package com.saasovation.agilepm.domain.model.product.sprint;

import java.util.*;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.backlogitem.*;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

/**
 *<h3>冲刺 - 聚合</h3>
 *<p>Sprint原意为冲刺，无对应中文翻译，指一次迭代。</p>
 *<p>Scrum是一种迭代和增量式的产品开发方法，Scrum通过冲刺来实现迭代。一个冲刺是指一个
 *1周－4周的迭代，它是一个时间盒。冲刺的长度一旦确定，保持不变。冲刺的产出是“完成”的、可用
 *的、潜在可发布的产品增量。冲刺在整个开发过程中的周期一致。新的冲刺在上一 个冲刺完成之后立即
 *开始。冲刺包含并由冲刺计划会议、每日站会、开发工作、冲刺评审会议和冲刺回顾会议构成。
 *</p>
 *<p>Scrum采用迭代增量的方式，是因为需求是涌现的，我们对产品和需求的理解是渐进式的，冲刺
 *长度越长，我们需要预测的越多，复杂度会提升、风险也会增加，所以冲刺的长度最多不超过4周。越来
 *越多的团队使用2周的冲刺，很多市场变化快、竞争激烈的领域，比如互联网和移动互联网产品开发团队
 *也会使用1周的迭代。
 *</p>
 *<p>在Sprint进行过程中，如下内容不能发生变化：
 *<ul>
 *<li>Sprint的目标</li>
 *<li>Sprint的质量目标和验收标准</li>
 *<li>开发团队的组成</li>
 *</ul>
 *</p>
 *
 */
public class Sprint extends Entity {

    /**
     * 冲刺代办事项列表
     */
    private Set<CommittedBacklogItem> backlogItems;
    /**
     * 开始时间
     */
    private Date begins;
    /**
     * 结束时间
     */
    private Date ends;
    /**
     * 目标
     */
    private String goals;
    /**
     * 名称
     */
    private String name;
    /**
     * 产品ID
     */
    private ProductId productId;
    /**
     * 回顾
     */
    private String retrospective;
    /**
     * ID
     */
    private SprintId sprintId;
    /**
     * 承租者ID
     */
    private TenantId tenantId;

    /**
     *<h3></h3>
     *
     * @param aTenantId
     * @param aProductId
     * @param aSprintId
     * @param aName
     * @param aGoals
     * @param aBegins
     * @param anEnds
     */
    public Sprint(
            TenantId aTenantId,
            ProductId aProductId,
            SprintId aSprintId,
            String aName,
            String aGoals,
            Date aBegins,
            Date anEnds) {

        this();

        if (anEnds.before(aBegins)) {
            throw new IllegalArgumentException("Sprint must not end before it begins.");
        }

        this.setBegins(aBegins);
        this.setEnds(anEnds);
        this.setGoals(aGoals);
        this.setName(aName);
        this.setProductId(aProductId);
        this.setSprintId(aSprintId);
        this.setTenantId(aTenantId);
    }

    /**
     *<h3>调整目标</h3>
     *
     * @param aGoals
     */
    public void adjustGoals(String aGoals) {
        this.setGoals(aGoals);

        // TODO: publish event / student assignment
    }

    public Set<CommittedBacklogItem> allCommittedBacklogItems() {
        return Collections.unmodifiableSet(this.backlogItems());
    }

    public Date begins() {
        return this.begins;
    }

    /**
     *<h3>捕捉回顾会议结果</h3>
     *
     * @param aRetrospective
     */
    public void captureRetrospectiveMeetingResults(String aRetrospective) {
        this.setRetrospective(aRetrospective);

        // TODO: publish event / student assignment
    }

    /**
     *<h3>提交待定项</h3>
     *
     * @param aBacklogItem
     */
    public void commit(BacklogItem aBacklogItem) {
        this.assertArgumentEquals(this.tenantId(), aBacklogItem.tenantId(), "Must have same tenants.");
        this.assertArgumentEquals(this.productId(), aBacklogItem.productId(), "Must have same products.");

        int ordering = this.backlogItems().size() + 1;

        CommittedBacklogItem committedBacklogItem =
                new CommittedBacklogItem(
                        this.tenantId(),
                        this.sprintId(),
                        aBacklogItem.backlogItemId(),
                        ordering);

        this.backlogItems().add(committedBacklogItem);
    }

    public Date ends() {
        return this.ends;
    }

    public String goals() {
        return this.goals;
    }

    public String name() {
        return this.name;
    }

    /**
     *<h3>冲刺现在开始于时间</h3>
     *
     * @param aBegins
     */
    public void nowBeginsOn(Date aBegins) {
        this.setBegins(aBegins);

        // TODO: publish event / student assignment
    }

    /**
     *<h3>冲刺现在结束于时间</h3>
     *
     * @param anEnds
     */
    public void nowEndsOn(Date anEnds) {
        this.setEnds(anEnds);

        // TODO: publish event / student assignment
    }

    public ProductId productId() {
        return this.productId;
    }

    /**
     *<h3>重命令</h3>
     *
     * @param aName
     */
    public void rename(String aName) {
        this.setName(aName);

        // TODO: publish event / student assignment
    }

    /**
     *<h3>从重新排序</h3>
     *
     * @param anId
     * @param anOrderOfPriority 
     */
    public void reorderFrom(BacklogItemId anId, int anOrderOfPriority) {
        for (CommittedBacklogItem committedBacklogItem : this.backlogItems()) {
            committedBacklogItem.reorderFrom(anId, anOrderOfPriority);
        }
    }

    public String retrospective() {
        return this.retrospective;
    }

    public SprintId sprintId() {
        return this.sprintId;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    /**
     *<h3>取消提交待定项</h3>
     *
     * @param aBacklogItem
     */
    public void uncommit(BacklogItem aBacklogItem) {
        CommittedBacklogItem cbi =
                new CommittedBacklogItem(
                        this.tenantId(),
                        this.sprintId(),
                        aBacklogItem.backlogItemId());

        this.backlogItems.remove(cbi);
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Sprint typedObject = (Sprint) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.productId().equals(typedObject.productId()) &&
                this.sprintId().equals(typedObject.sprintId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (11873 * 53)
            + this.tenantId().hashCode()
            + this.productId().hashCode()
            + this.sprintId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Sprint [tenantId=" + tenantId + ", productId=" + productId
                + ", sprintId=" + sprintId + ", backlogItems="
                + backlogItems + ", begins=" + begins + ", ends=" + ends
                + ", goals=" + goals + ", name=" + name
                + ", retrospective=" + retrospective + "]";
    }

    /**
     *<h3></h3>
     *
     */
    private Sprint() {
        super();

        this.setBacklogItems(new HashSet<CommittedBacklogItem>(0));
    }

    private Set<CommittedBacklogItem> backlogItems() {
        return this.backlogItems;
    }

    private void setBacklogItems(Set<CommittedBacklogItem> aBacklogItems) {
        this.backlogItems = aBacklogItems;
    }

    private void setBegins(Date aBegins) {
        this.assertArgumentNotNull(aBegins, "The begins must be provided.");

        this.begins = aBegins;
    }

    private void setEnds(Date anEnds) {
        this.assertArgumentNotNull(anEnds, "The ends must be provided.");

        this.ends = anEnds;
    }

    private void setGoals(String aGoals) {
        if (aGoals != null) {
            this.assertArgumentLength(aGoals, 500, "The goals must be 500 characters or less.");
        }

        this.goals = aGoals;
    }

    private void setName(String aName) {
        this.assertArgumentNotEmpty(aName, "The name must be provided.");
        this.assertArgumentLength(aName, 100, "The name must be 100 characters or less.");

        this.name = aName;
    }

    private void setProductId(ProductId aProductId) {
        this.assertArgumentNotNull(aProductId, "The product id must be provided.");

        this.productId = aProductId;
    }

    private void setRetrospective(String aRetrospective) {
        if (aRetrospective != null) {
            this.assertArgumentLength(aRetrospective, 5000, "The goals must be 5000 characters or less.");
        }

        this.retrospective = aRetrospective;
    }

    private void setSprintId(SprintId aSprintId) {
        this.assertArgumentNotNull(aSprintId, "The sprint id must be provided.");

        this.sprintId = aSprintId;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenant id must be provided.");

        this.tenantId = aTenantId;
    }
}
