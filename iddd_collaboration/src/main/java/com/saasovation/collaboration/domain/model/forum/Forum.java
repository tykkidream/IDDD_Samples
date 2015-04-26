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

package com.saasovation.collaboration.domain.model.forum;

import java.util.List;

import com.saasovation.collaboration.domain.model.collaborator.Author;
import com.saasovation.collaboration.domain.model.collaborator.Creator;
import com.saasovation.collaboration.domain.model.collaborator.Moderator;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.EventSourcedRootEntity;

/**
 * <h3>论坛 - 聚合根</h3>
 *
 */
public class Forum extends EventSourcedRootEntity {

    private boolean closed;
    /** 创建人 **/
    private Creator creator;
    /** 描述 **/
    private String description;
    /** 独家拥有者 **/
    private String exclusiveOwner;
    /** ID **/
    private ForumId forumId;
    /** 论坛版主 **/
    private Moderator moderator;
    /** 主题 **/
    private String subject;
    /** 租户 **/
    private Tenant tenant;

    /**
     *<h3>构造Forum</h3>
     *
     *<p>代表了一个新生的Forum对象，同时会发布创建事件{@link ForumStarted}。
     *
     *<p>初始化需要一些参数，都是必须的，初始化时会对参数做校验。由于本聚合使用
     *了事件溯源，所以在断言之后将参数封装到了一个{@link ForumStarted}事件对象中，
     *再将事件发布，最后由聚合内的事件重放方法{@link #when(ForumStarted)}更新当前
     *聚合状态。
     *
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aTenant
     * @param aForumId
     * @param aCreator
     * @param aModerator
     * @param aSubject
     * @param aDescription
     * @param anExclusiveOwner
     */
    public Forum(
            Tenant aTenant,
            ForumId aForumId,
            Creator aCreator,
            Moderator aModerator,
            String aSubject,
            String aDescription,
            String anExclusiveOwner) {

        this();

        // 对参数进行校验
        this.assertArgumentNotNull(aCreator, "The creator must be provided.");
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");
        this.assertArgumentNotNull(aForumId, "The forum id must be provided.");
        this.assertArgumentNotNull(aModerator, "The moderator must be provided.");
        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");
        this.assertArgumentNotNull(aTenant, "The creator must be provided.");

        this.apply(new ForumStarted(aTenant, aForumId, aCreator,
                aModerator, aSubject, aDescription, anExclusiveOwner));
    }

    /**
     *<h3>构造Forum</h3>
     *<p>代表了重建（还原）一个已存在的Forum对象。
     *<p>由于本聚合使用了事件溯源，所以初始化需要参数的是事件流及事件版本号。
     *重建过程是将事件流中的事件按顺序依次使用聚合内的事件重放方法更新聚合对
     *象的状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param anEventStream
     * @param aStreamVersion
     */
    public Forum(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    /**
     *<h3>改变论坛版主</h3>
     *<p>这是一个CQS命令方法，用于改变论坛版主，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link ForumModeratorChanged}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(ForumModeratorChanged)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aModerator
     */
    public void assignModerator(Moderator aModerator) {
    	// 当前论坛不能是关闭的
        this.assertStateFalse(this.isClosed(), "Forum is closed.");
        this.assertArgumentNotNull(aModerator, "The moderator must be provided.");

        this.apply(new ForumModeratorChanged(this.tenant(), this.forumId(),
                aModerator, this.exclusiveOwner()));
    }

    /**
     *<h3>修改描述</h3>
     *<p>这是一个CQS命令方法，用于修改描述，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link ForumDescriptionChanged}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(ForumDescriptionChanged)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aDescription
     */
    public void changeDescription(String aDescription) {
    	// 当前论坛不能是关闭的
        this.assertStateFalse(this.isClosed(), "Forum is closed.");
        this.assertArgumentNotEmpty(aDescription, "The description must be provided.");

        this.apply(new ForumDescriptionChanged(this.tenant(), this.forumId(),
                aDescription, this.exclusiveOwner()));
    }

    /**
     *<h3>改变主题</h3>
     *<p>这是一个CQS命令方法，用于改变主题，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link ForumSubjectChanged}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(ForumSubjectChanged)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aSubject
     */
    public void changeSubject(String aSubject) {
    	// 当前论坛不能是关闭的
        this.assertStateFalse(this.isClosed(), "Forum is closed.");
        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");

        this.apply(new ForumSubjectChanged(this.tenant(), this.forumId(),
                aSubject, this.exclusiveOwner()));
    }

    /**
     *<h3>关闭论坛</h3>
     *<p>这是一个CQS命令方法，用于关闭论坛，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link ForumClosed}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(ForumClosed)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     */
    public void close() {
    	// 当前论坛不能是关闭的
        this.assertStateFalse(this.isClosed(), "Forum is already closed.");

        this.apply(new ForumClosed(this.tenant(), this.forumId(), this.exclusiveOwner()));
    }

    /**
     *<h3>论坛是否已关闭</h3>
     * @return
     */
    public boolean isClosed() {
        return this.closed;
    }

    public Creator creator() {
        return this.creator;
    }

    public String description() {
        return this.description;
    }

    public String exclusiveOwner() {
        return this.exclusiveOwner;
    }

    /**
     *<h3>是否存在独家所有者</h3>
     * @return
     */
    public boolean hasExclusiveOwner() {
        return this.exclusiveOwner() != null;
    }

    public ForumId forumId() {
        return this.forumId;
    }

    /**
     *<h3>主持人是否是某人</h3>
     * @param aModerator
     * @return
     */
    public boolean isModeratedBy(Moderator aModerator) {
        return this.moderator().equals(aModerator);
    }

    /**
     *<h3>适度的帖子</h3>
     *<p>这是一个CQS命令方法，用于适度的帖子，没有返回值。
     *<p>处理时，经过一些校验后，再委托给{@link Post#alterPostContent}命令处理。
     * 
     * @param aPost
     * @param aModerator
     * @param aSubject
     * @param aBodyText
     */
    public void moderatePost(
            Post aPost,
            Moderator aModerator,
            String aSubject,
            String aBodyText) {

    	// 当前论坛不能是关闭的
        this.assertStateFalse(this.isClosed(), "Forum is closed.");
        this.assertArgumentNotNull(aPost, "Post may not be null.");
    	// 帖子是以属于当前论坛的
        this.assertArgumentEquals(aPost.forumId(), this.forumId(), "Not a post of this forum.");
    	// 主持人是以当前主持人
        this.assertArgumentTrue(this.isModeratedBy(aModerator), "Not the moderator of this forum.");

        aPost.alterPostContent(aSubject, aBodyText);
    }

    public Moderator moderator() {
        return this.moderator;
    }

    /**
     *<h3>重开论坛</h3>
     *<p>这是一个CQS命令方法，用于重开论坛，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link ForumReopened}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(ForumReopened)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     */
    public void reopen() {
    	// 当前论坛是关闭的
        this.assertStateTrue(this.isClosed(), "Forum is not closed.");

        this.apply(new ForumReopened(this.tenant(), this.forumId(), this.exclusiveOwner()));
    }

    /**
     *<h3>开始讨论</h3>
     *<p>这是一个CQS查询方法，用于开始讨论，逻辑委托给了{@link #startDiscussionFor}方法
     *处理，其中传递anExclusiveOwner参数为null。
     * 
     * @param aForumIdentityService
     * @param anAuthor
     * @param aSubject
     * @return
     */
    public Discussion startDiscussion(
            ForumIdentityService aForumIdentityService,
            Author anAuthor,
            String aSubject) {

        return this.startDiscussionFor(aForumIdentityService, anAuthor, aSubject, null);
    }

    /**
     *<h3>开始讨论</h3>
     *<p>这是一个CQS查询方法，创建一个新讨论，返回现一个新{@link Discussion}
     *对象，所以此方法是Discussion的一个工厂方法。
     *
     *<p>这个方法简化了创建Discussion的一些方面，使用了外部模块（其它包）无法使用的
     *{@link Discussion#Discussion(Tenant, ForumId, DiscussionId, Author, String, String) 构造函数}，
     *其中tenant、forumId由当前聚合对象的状态提供，discussionId由aForumIdentityService的
     *{@link ForumIdentityService#nextDiscussionId() nextDiscussionId()}提供。
     * 
     * @param aForumIdentityService
     * @param anAuthor
     * @param aSubject
     * @param anExclusiveOwner
     * @return
     */
    public Discussion startDiscussionFor(
            ForumIdentityService aForumIdentityService,
            Author anAuthor,
            String aSubject,
            String anExclusiveOwner) {

        if (this.isClosed()) {
            throw new IllegalStateException("Forum is closed.");
        }

        Discussion discussion =
                new Discussion(
                    this.tenant(),
                    this.forumId(),
                    aForumIdentityService.nextDiscussionId(),
                    anAuthor,
                    aSubject,
                    anExclusiveOwner);

        return discussion;
    }

    public String subject() {
        return this.subject;
    }

    public Tenant tenant() {
        return this.tenant;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Forum typedObject = (Forum) anObject;
            equalObjects =
                this.tenant().equals(typedObject.tenant()) &&
                this.forumId().equals(typedObject.forumId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (75219 * 41)
            + this.tenant().hashCode()
            + this.forumId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Forum [closed=" + closed + ", creator=" + creator
                + ", description=" + description + ", exclusiveOwner="+ exclusiveOwner
                + ", forumId=" + forumId + ", moderator=" + moderator
                + ", subject=" + subject + ", tenantId=" + tenant + "]";
    }

    /**
     *<h3>构造Forum</h3>
     */
    protected Forum() {
        super();
    }

    /**
     *<h3>处理{@link ForumClosed}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(ForumClosed anEvent) {
        this.setClosed(true);
    }

    /**
     *<h3>处理{@link ForumDescriptionChanged}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(ForumDescriptionChanged anEvent) {
        this.setDescription(anEvent.description());
    }

    /**
     *<h3>处理{@link ForumModeratorChanged}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(ForumModeratorChanged anEvent) {
        this.setModerator(anEvent.moderator());
    }

    /**
     *<h3>处理{@link ForumReopened}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(ForumReopened anEvent) {
        this.setClosed(false);
    }

    /**
     *<h3>处理{@link ForumStarted}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(ForumStarted anEvent) {
        this.setCreator(anEvent.creator());
        this.setDescription(anEvent.description());
        this.setExclusiveOwner(anEvent.exclusiveOwner());
        this.setForumId(anEvent.forumId());
        this.setModerator(anEvent.moderator());
        this.setSubject(anEvent.subject());
        this.setTenant(anEvent.tenant());
    }

    /**
     *<h3>处理{@link ForumSubjectChanged}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(ForumSubjectChanged anEvent) {
        this.setSubject(anEvent.subject());
    }

    private void setClosed(boolean isClosed) {
        this.closed = isClosed;
    }

    private void setCreator(Creator aCreator) {
        this.creator = aCreator;
    }

    private void setDescription(String aDescription) {
        this.description = aDescription;
    }

    private void setExclusiveOwner(String anExclusiveOwner) {
        this.exclusiveOwner = anExclusiveOwner;
    }

    private void setForumId(ForumId aForumId) {
        this.forumId = aForumId;
    }

    private void setModerator(Moderator aModerator) {
        this.moderator = aModerator;
    }

    private void setSubject(String aSubject) {
        this.subject = aSubject;
    }

    private void setTenant(Tenant aTenant) {
        this.tenant = aTenant;
    }
}
