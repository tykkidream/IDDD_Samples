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

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

/**
 *<h3>Scrum团队成员 - 实体</h3>
 *
 */
public abstract class Member extends Entity {

    /**
     * 变更跟踪
     */
    private MemberChangeTracker changeTracker;
    /**
     * 电子邮件地址
     */
    private String emailAddress;
    /**
     * 是否启用
     */
    private boolean enabled = true;
    /**
     * 名字
     */
    private String firstName;
    /**
     * 姓名
     */
    private String lastName;
    /**
     * 承租者ID
     */
    private TenantId tenantId;
    /**
     * 用户名
     */
    private String username;

    /**
     *<h3></h3>
     *
     * @param aTenantId
     * @param aUsername
     * @param aFirstName
     * @param aLastName
     * @param anEmailAddress
     * @param anInitializedOn
     */
    public Member(
            TenantId aTenantId,
            String aUsername,
            String aFirstName,
            String aLastName,
            String anEmailAddress,
            Date anInitializedOn) {

        this(aTenantId, aUsername, aFirstName, aLastName, anEmailAddress);

        this.setChangeTracker(
                new MemberChangeTracker(
                        anInitializedOn,
                        anInitializedOn,
                        anInitializedOn));
    }

    /**
     *<h3>改变电子邮件地址</h3>
     *
     * @param anEmailAddress
     * @param asOfDate
     */
    public void changeEmailAddress(String anEmailAddress, Date asOfDate) {
        if (this.changeTracker().canChangeEmailAddress(asOfDate) &&
            !this.emailAddress().equals(anEmailAddress)) {
            this.setEmailAddress(anEmailAddress);
            this.setChangeTracker(this.changeTracker().emailAddressChangedOn(asOfDate));
        }
    }

    /**
     *<h3>改变名称</h3>
     *
     * @param aFirstName
     * @param aLastName
     * @param asOfDate
     */
    public void changeName(String aFirstName, String aLastName, Date asOfDate) {
        if (this.changeTracker().canChangeName(asOfDate)) {
            this.setFirstName(aFirstName);
            this.setLastName(aLastName);
            this.setChangeTracker(this.changeTracker().nameChangedOn(asOfDate));
        }
    }

    /**
     *<h3>禁用成员</h3>
     *
     * @param asOfDate 禁用时间
     */
    public void disable(Date asOfDate) {
        if (this.changeTracker().canToggleEnabling(asOfDate)) {
            this.setEnabled(false);
            this.setChangeTracker(this.changeTracker().enablingOn(asOfDate));
        }
    }

    /**
     *<h3>启用成员</h3>
     *
     *<p>这是一个CQS命令方法，用于启用成员，没有返回值。
     *
     * @param asOfDate 启用时间
     */
    public void enable(Date asOfDate) {
        if (this.changeTracker().canToggleEnabling(asOfDate)) {
            this.setEnabled(true);
            this.setChangeTracker(this.changeTracker().enablingOn(asOfDate));
        }
    }

    public String emailAddress() {
        return this.emailAddress;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String firstName() {
        return this.firstName;
    }

    public String lastName() {
        return this.lastName;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public String username() {
        return this.username;
    }

    /**
     *<h3></h3>
     *
     * @param aTenantId
     * @param aUsername
     * @param aFirstName
     * @param aLastName
     * @param anEmailAddress
     */
    protected Member(
            TenantId aTenantId,
            String aUsername,
            String aFirstName,
            String aLastName,
            String anEmailAddress) {

        this();

        this.setEmailAddress(anEmailAddress);
        this.setFirstName(aFirstName);
        this.setLastName(aLastName);
        this.setTenantId(aTenantId);
        this.setUsername(aUsername);
    }

    /**
     *<h3></h3>
     *
     */
    protected Member() {
        super();
    }

    /**
     *<h3></h3>
     *
     * @return
     */
    private MemberChangeTracker changeTracker() {
        return this.changeTracker;
    }

    private void setChangeTracker(MemberChangeTracker aChangeTracker) {
        this.changeTracker = aChangeTracker;
    }

    private void setEmailAddress(String anEmailAddress) {
        if (anEmailAddress != null) {
            this.assertArgumentLength(anEmailAddress, 100, "Email address must be 100 characters or less.");
        }

        this.emailAddress = anEmailAddress;
    }

    private void setEnabled(boolean anEnabled) {
        this.enabled = anEnabled;
    }

    private void setFirstName(String aFirstName) {
        if (aFirstName != null) {
            this.assertArgumentLength(aFirstName, 50, "First name must be 50 characters or less.");
        }

        this.firstName = aFirstName;
    }

    private void setLastName(String aLastName) {
        if (aLastName != null) {
            this.assertArgumentLength(aLastName, 50, "Last name must be 50 characters or less.");
        }

        this.lastName = aLastName;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenant id must be provided.");

        this.tenantId = aTenantId;
    }

    private void setUsername(String aUsername) {
        this.assertArgumentNotEmpty(aUsername, "The username must be provided.");
        this.assertArgumentLength(aUsername, 250, "The username must be 250 characters or less.");

        this.username = aUsername;
    }
}
