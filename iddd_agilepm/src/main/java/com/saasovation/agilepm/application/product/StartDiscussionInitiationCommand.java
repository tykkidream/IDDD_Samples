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

package com.saasovation.agilepm.application.product;

/**
 *<h3>开始讨论 - CQRS命令</h3>
 *<p>是一个POJO</p>
 *
 */
public class StartDiscussionInitiationCommand {

    private String tenantId;
    private String productId;

    public StartDiscussionInitiationCommand(String tenantId, String productId) {
        super();
        this.tenantId = tenantId;
        this.productId = productId;
    }

    public StartDiscussionInitiationCommand() {
        super();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
