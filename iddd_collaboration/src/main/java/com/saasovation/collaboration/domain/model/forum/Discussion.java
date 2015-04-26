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
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.EventSourcedRootEntity;

/**
 * <h3>论坛讨论 - 聚合根</h3>
 */
public class Discussion extends EventSourcedRootEntity {

	/** 作者 **/
    private Author author;
    /** 关闭状态**/
    private boolean closed;
    /** ID **/
    private DiscussionId discussionId;
    /** 独家拥有者 **/
    private String exclusiveOwner;
    /** 论坛ID。遵循“通过唯一标识引用其它聚合”的原则。 **/
    private ForumId forumId;
    /** 主题 **/
    private String subject;
    /** 租户 **/
    private Tenant tenant;

    /**
     *<h3>构造Discussion</h3>
     *<p>代表了重建（还原）一个已存在的Discussion对象。
     *<p>由于本聚合使用了事件溯源，所以初始化需要参数的是事件流及事件版本号。
     *重建过程是将事件流中的事件按顺序依次使用聚合内的事件重放方法更新聚合对
     *象的状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param anEventStream
     * @param aStreamVersion
     */
    public Discussion(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public Author author() {
        return this.author;
    }

    /**
     *<h3>关闭讨论</h3>
     *<p>这是一个CQS命令方法，用于关闭讨论，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link DiscussionClosed}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(DiscussionClosed)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     */
    public void close() {
        if (this.isClosed()) {
            throw new IllegalStateException("This discussion is already closed.");
        }

        this.apply(new DiscussionClosed(this.tenant(), this.forumId(),
                    this.discussionId(), this.exclusiveOwner()));
    }

    /**
     * <h3>讨论是否已关闭</h3>
     * @return
     */
    public boolean isClosed() {
        return this.closed;
    }

    public DiscussionId discussionId() {
        return this.discussionId;
    }

    public String exclusiveOwner() {
        return this.exclusiveOwner;
    }

    public ForumId forumId() {
        return this.forumId;
    }

    /**
     *<h3>开始讨论</h3>
     *<p>这是一个CQS查询方法，用于开始讨论，逻辑委托给了
     *{@link #post(ForumIdentityService, PostId, Author, String, String)}方法
     *处理，其中传递aReplyToPost参数为null。
     * 
     * @param aForumIdentityService
     * @param anAuthor
     * @param aSubject
     * @param aBodyText
     * @return
     */
    public Post post(
            ForumIdentityService aForumIdentityService,
            Author anAuthor,
            String aSubject,
            String aBodyText) {

        return this.post(aForumIdentityService, null, anAuthor, aSubject, aBodyText);
    }

    /**
     *<h3>开始讨论</h3>
     *<p>这是一个CQS查询方法，创建一个新讨论，返回现一个新{@link Post}
     *对象，所以此方法是Post的一个工厂方法。
     *
     *<p>这个方法简化了创建Post的一些方面，使用了外部模块（其它包）无法使用的
     *{@link Post#Post(Tenant, ForumId, DiscussionId, PostId, PostId, Author, String, String) 构造函数}，
     *其中tenant、discussionId由当前聚合对象的状态提供，postId由aForumIdentityService的
     *{@link ForumIdentityService#nextPostId() nextPostId()}提供。
     * 
     * @param aForumIdentityService
     * @param aReplyToPost
     * @param anAuthor
     * @param aSubject
     * @param aBodyText
     * @return
     */
    public Post post(
            ForumIdentityService aForumIdentityService,
            PostId aReplyToPost,
            Author anAuthor,
            String aSubject,
            String aBodyText) {

        Post post =
            new Post(
                    this.tenant(),
                    this.forumId(),
                    this.discussionId(),
                    aReplyToPost,
                    aForumIdentityService.nextPostId(),
                    anAuthor,
                    aSubject,
                    aBodyText);

        return post;
    }


    /**
     *<h3>重新开始讨论</h3>
     *<p>这是一个CQS命令方法，用于重新开始讨论，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link DiscussionReopened}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(DiscussionReopened)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     */
    public void reopen() {
        if (!this.isClosed()) {
            throw new IllegalStateException("The discussion is not closed.");
        }

        this.apply(new DiscussionReopened(this.tenant(), this.forumId(),
                    this.discussionId(), this.exclusiveOwner()));
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
            Discussion typedObject = (Discussion) anObject;
            equalObjects =
                this.tenant().equals(typedObject.tenant()) &&
                this.forumId().equals(typedObject.forumId()) &&
                this.discussionId().equals(typedObject.discussionId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (87123 * 43)
            + this.tenant().hashCode()
            + this.forumId().hashCode()
            + this.discussionId().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Discussion [author=" + author + ", closed=" + closed + ", discussionId=" + discussionId + ", exclusiveOwner="
                + exclusiveOwner + ", forumId=" + forumId + ", subject=" + subject + ", tenantId=" + tenant + "]";
    }

    /**
     *<h3>构造Discussion</h3>
     *
     *<p>代表了一个新生的Discussion对象，同时会发布创建事件{@link DiscussionStarted}。
     *
     *<p>初始化需要一些参数，其中一些是必须的，初始化时会对参数做校验。由于本聚合使用
     *了事件溯源，所以在断言之后将参数封装到了一个{@link DiscussionStarted}事件对象中，
     *再将事件发布，最后由聚合内的事件重放方法{@link #when(DiscussionStarted)}更新当前
     *聚合状态。
     *
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * 
     * @param aTenantId
     * @param aForumId
     * @param aDiscussionId
     * @param anAuthor
     * @param aSubject
     * @param anExclusiveOwner
     */
    protected Discussion(
            Tenant aTenantId,
            ForumId aForumId,
            DiscussionId aDiscussionId,
            Author anAuthor,
            String aSubject,
            String anExclusiveOwner) {

        this();

        this.assertArgumentNotNull(anAuthor, "The author must be provided.");
        this.assertArgumentNotNull(aDiscussionId, "The discussion id must be provided.");
        this.assertArgumentNotNull(aForumId, "The forum id must be provided.");
        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");
        this.assertArgumentNotNull(aTenantId, "The tenant must be provided.");

        this.apply(new DiscussionStarted(aTenantId, aForumId, aDiscussionId,
                anAuthor, aSubject, anExclusiveOwner));
    }

    /**
     *<h3>构造Discussion</h3>
     */
    protected Discussion() {
        super();
    }

    /**
     *<h3>处理{@link DiscussionClosed}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(DiscussionClosed anEvent) {
        this.setClosed(true);
    }

    /**
     *<h3>处理{@link DiscussionReopened}事件。</h3>
     *<p>事件重放的方法。
     * 
     * 
     * @param anEvent
     */
    protected void when(DiscussionReopened anEvent) {
        this.setClosed(false);
    }

    /**
     *<h3>处理{@link DiscussionStarted}事件。</h3>
     *<p>事件重放的方法。
     * 
     * 
     * @param anEvent
     */
    protected void when(DiscussionStarted anEvent) {
        this.setAuthor(anEvent.author());
        this.setDiscussionId(anEvent.discussionId());
        this.setExclusiveOwner(anEvent.exclusiveOwner());
        this.setForumId(anEvent.forumId());
        this.setSubject(anEvent.subject());
        this.setTenant(anEvent.tenant());
    }

    private void setAuthor(Author author) {
        this.author = author;
    }

    private void setClosed(boolean isClosed) {
        this.closed = isClosed;
    }

    private void setDiscussionId(DiscussionId aDiscussionId) {
        this.discussionId = aDiscussionId;
    }

    private void setExclusiveOwner(String anExclusiveOwner) {
        this.exclusiveOwner = anExclusiveOwner;
    }

    private void setForumId(ForumId aForumId) {
        this.forumId = aForumId;
    }

    private void setSubject(String aSubject) {
        this.subject = aSubject;
    }

    private void setTenant(Tenant aTenant) {
        this.tenant = aTenant;
    }
}
