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

package com.saasovation.agilepm.domain.model.team;

import java.util.Date;

import com.saasovation.agilepm.domain.model.tenant.TenantId;

/**
 *<h3>产品负责人 - 聚合根</h3>
 *
 *<p>产品负责人负责最大化产品以及开发团队工作的价值，是管理产品待办事项列表的唯一责任人。产品待办事项列表的管理包括：
 *<ul>
 *<li>清晰地表达产品代办事项列表条目</li>
 *<li>对产品代办事项列表中的条目进行排序，最好地实现目标和使命</li>
 *<li>确保开发团队所执行工作的价值</li>
 *<li>确保产品代办事项列表对所有人可见、透明、清晰，并且显示 Scrum 团队的下一步工作</li>
 *<li>确保开发团队对产品代办事项列表中的条目达到一定程度的理解</li>
 *</ul>
 *产品负责人可以亲自完成上述工作，也可以让开发团队来完成。
 *</p>
 *<p>产品负责人是一个人，而不是一个委员会。产品负责人可能会在产品代办事项列表中体现一个委员会的需求，但要想改变某条目的
 *优先级必须先说服产品负责人。
 *<p>为保证产品负责人的工作取得成功，组织中的所有人员都必须尊重他的决定。产品负 责人所作的决定在产品待办事项列表的内容和
 *排序中要清晰可见。任何人都不得要求开发 团队按照另一套需求开展工作，开发团队也不允许听从任何其他人的指令。
 *
 */
public class ProductOwner extends Member {

    public ProductOwner(
            TenantId aTenantId,
            String aUsername,
            String aFirstName,
            String aLastName,
            String anEmailAddress,
            Date anInitializedOn) {

        super(aTenantId, aUsername, aFirstName, aLastName, anEmailAddress, anInitializedOn);
    }

    public ProductOwnerId productOwnerId() {
        return new ProductOwnerId(this.tenantId(), this.username());
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            ProductOwner typedObject = (ProductOwner) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.username().equals(typedObject.username());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (71121 * 79)
            + this.tenantId().hashCode()
            + this.username().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "ProductOwner [productOwnerId()=" + productOwnerId() + ", emailAddress()=" + emailAddress() + ", isEnabled()="
                + isEnabled() + ", firstName()=" + firstName() + ", lastName()=" + lastName() + ", tenantId()=" + tenantId()
                + ", username()=" + username() + "]";
    }

    protected ProductOwner() {
        super();
    }
}
