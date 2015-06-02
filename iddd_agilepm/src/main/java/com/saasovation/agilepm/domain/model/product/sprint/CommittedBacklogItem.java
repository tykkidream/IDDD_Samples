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

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

/**
 *<h3>冲刺待定项 - 实体</h3>
 *
 *<p>这个类的访问级别是public的，可以外界使用，但只能被当前子域元素如{@link Sprint}（也当前包内的类）创建。因为这个类的3个构造函数没
 *有public的，分别是：
 *<ul>
 *<li>{@link #CommittedBacklogItem(TenantId, SprintId, BacklogItemId, int)}为protected的；
 *<li>{@link #CommittedBacklogItem(TenantId, SprintId, BacklogItemId)}为protected的；
 *<li>{@link #CommittedBacklogItem()}为private的。
 *</ul>
 *</p>
 *
 *<p>刚看到这个类时还以为是值对象，但经分析之后可以从两点上判断为实体。一是{@link #backlogItemId()}可以作为实例唯一标识，这个标识
 *是来自{@link com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItem#backlogItemId() BacklogItem#backlogItemId()}，二是其状态
 *可以被{@link #reorderFrom(BacklogItemId, int)}修改。所以这个类是一个实体，不是值对象。</p>
 *
 */
public class CommittedBacklogItem extends Entity {

    /**
     * 待定项ID
     */
    private BacklogItemId backlogItemId;
    /**
     * 排序
     */
    private int ordering;
    /**
     * 冲刺ID
     */
    private SprintId sprintId;
    /**
     * 承租者ID
     */
    private TenantId tenantId;

    public BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    public int ordering() {
        return this.ordering;
    }

    public SprintId sprintId() {
        return this.sprintId;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            CommittedBacklogItem typedObject = (CommittedBacklogItem) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.sprintId().equals(typedObject.sprintId()) &&
                this.backlogItemId().equals(typedObject.backlogItemId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (282891 * 53)
            + this.tenantId().hashCode()
            + this.sprintId().hashCode()
            + this.backlogItemId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "CommittedBacklogItem [sprintId=" + sprintId + ", ordering=" + ordering + "]";
    }

    /**
     *<h3>构造TenantId</h3>
     *
     *<p>这个方法的访问级别是protected的，只有当前包内的类才能使用此构造函数创建实例。
     *
     * @param aTenantId
     * @param aSprintId
     * @param aBacklogItemId
     * @param anOrdering
     */
    protected CommittedBacklogItem(
            TenantId aTenantId,
            SprintId aSprintId,
            BacklogItemId aBacklogItemId,
            int anOrdering) {

        this();

        this.setBacklogItemId(aBacklogItemId);
        this.setOrdering(anOrdering);
        this.setSprintId(aSprintId);
        this.setTenantId(aTenantId);
    }

    /**
     *<h3>构造TenantId</h3>
     *
     *<p>内部委托给了{@link #CommittedBacklogItem(TenantId, SprintId, BacklogItemId, int)}
     *初始化实例，并且默认最后一个参数anOrdering为0。
     *
     *<p>这个方法的访问级别是protected的，只有当前包内的类才能使用此构造函数创建实例。
     *
     * @param aTenantId
     * @param aSprintId
     * @param aBacklogItemId
     */
    protected CommittedBacklogItem(
            TenantId aTenantId,
            SprintId aSprintId,
            BacklogItemId aBacklogItemId) {

        this(aTenantId, aSprintId, aBacklogItemId, 0);
    }

    /**
     *<h3>冲刺待定项</h3>
     *
     *<p>这个方法的访问级别是private的，禁止其它类使用此构造函数创建实例。
     */
    private CommittedBacklogItem() {
        super();
    }

    /**
     *<h3>从重新排序</h3>
     *<p>重新排序时同其它待定项比较位置，如果同自己比较，则修改自己的顺序号为anOrderOfPriority指定
     *的号码，否则同anOrderOfPriority比较，如果比它大则递增自己一号。
     *
     * @param anId 待定项ID
     * @param anOrderOfPriority 顺序号
     */
    protected void reorderFrom(BacklogItemId anId, int anOrderOfPriority) {
        if (this.backlogItemId().equals(anId)) {
            this.setOrdering(anOrderOfPriority);
        } else if (this.ordering() >= anOrderOfPriority) {
            this.setOrdering(this.ordering() + 1);
        }
    }

    /**
     *<h3>设置顺序号</h3>
     *<p>注意：这个setter方法是protected的，而其它都是private的。至于原因，由于不了解领域知识而无法知晓，
     *不过可以观察一个现象，就是这个方法只被{@link #reorderFrom}方法调用，这个方法也是protected的。
     *
     *
     * @param anOrdering 顺序号
     */
    protected void setOrdering(int anOrdering) {
        this.ordering = anOrdering;
    }

    private void setBacklogItemId(BacklogItemId aBacklogItemId) {
        this.assertArgumentNotNull(aBacklogItemId, "The backlog item id must be provided.");

        this.backlogItemId = aBacklogItemId;
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
