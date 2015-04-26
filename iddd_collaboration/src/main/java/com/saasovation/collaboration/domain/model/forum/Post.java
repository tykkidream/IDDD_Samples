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

import java.util.Date;
import java.util.List;

import com.saasovation.collaboration.domain.model.collaborator.Author;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.EventSourcedRootEntity;

/**
 *<h3>论坛帖子 -  聚合<>
 *
 */
public class Post extends EventSourcedRootEntity {

	/** 作者 **/
    private Author author;
    /** 正文 **/
    private String bodyText;
    /** 修改时间 **/
    private Date changedOn;
    /** 创建时间 **/
    private Date createdOn;
    /** 讨论ID **/
    private DiscussionId discussionId;
    /** 论坛ID **/
    private ForumId forumId;
    /** 帖子ID **/
    private PostId postId;
    /** 回复帖子的ID **/
    private PostId replyToPostId;
    /** 主题 **/
    private String subject;
    /** 租户 **/
    private Tenant tenant;

    /**
     *<h3>构造Post</h3>
     *<p>代表了重建（还原）一个已存在的Post对象。
     *<p>由于本聚合使用了事件溯源，所以初始化需要参数的是事件流及事件版本号。
     *重建过程是将事件流中的事件按顺序依次使用聚合内的事件重放方法更新聚合对
     *象的状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param anEventStream
     * @param aStreamVersion
     */
    public Post(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public Author author() {
        return this.author;
    }

    public String bodyText() {
        return this.bodyText;
    }

    public Date changedOn() {
        return this.changedOn;
    }

    public Date createdOn() {
        return this.createdOn;
    }

    public DiscussionId discussionId() {
        return this.discussionId;
    }

    public ForumId forumId() {
        return this.forumId;
    }

    public PostId postId() {
        return this.postId;
    }

    public PostId replyToPostId() {
        return this.replyToPostId;
    }

    public String subject() {
        return this.subject;
    }

    public Tenant tenant() {
        return this.tenant;
    }

    /**
     *<h3>构造Post</h3>
     *
     *<p>逻辑委托给了{@link #Post(Tenant, ForumId, DiscussionId, PostId, PostId, Author, String, String)}方法
     *处理，其中传递aReplyToPost参数为null。
     * 
     * @param aTenant
     * @param aForumId
     * @param aDiscussionId
     * @param aPostId
     * @param anAuthor
     * @param aSubject
     * @param aBodyText
     */
    protected Post(
            Tenant aTenant,
            ForumId aForumId,
            DiscussionId aDiscussionId,
            PostId aPostId,
            Author anAuthor,
            String aSubject,
            String aBodyText) {

        this(aTenant, aForumId, aDiscussionId, null, aPostId, anAuthor, aSubject, aBodyText);
    }

    /**
     *<h3>构造Post</h3>
     *
     *<p>代表了一个新生的Post对象，同时会发布创建事件{@link PostedToDiscussion}。
     *
     *<p>初始化需要一些参数，都是必须的，初始化时会对参数做校验。由于本聚合使用
     *了事件溯源，所以在断言之后将参数封装到了一个{@link PostedToDiscussion}事件对象中，
     *再将事件发布，最后由聚合内的事件重放方法{@link #when(PostedToDiscussion)}更新当前
     *聚合状态。
     *
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aTenant
     * @param aForumId
     * @param aDiscussionId
     * @param aReplyToPost
     * @param aPostId
     * @param anAuthor
     * @param aSubject
     * @param aBodyText
     */
    protected Post(
            Tenant aTenant,
            ForumId aForumId,
            DiscussionId aDiscussionId,
            PostId aReplyToPost,
            PostId aPostId,
            Author anAuthor,
            String aSubject,
            String aBodyText) {

        this.assertArgumentNotNull(anAuthor, "The author must be provided.");
        this.assertArgumentNotEmpty(aBodyText, "The body text must be provided.");
        this.assertArgumentNotNull(aDiscussionId, "The discussion id must be provided.");
        this.assertArgumentNotNull(aForumId, "The forum id must be provided.");
        this.assertArgumentNotNull(aPostId, "The post id must be provided.");
        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");
        this.assertArgumentNotNull(aTenant, "The tenant must be provided.");

        this.apply(new PostedToDiscussion(aTenant, aForumId, aDiscussionId,
                aReplyToPost, aPostId, anAuthor, aSubject, aBodyText));
    }

    /**
     *<h3>构造Post</h3>
     * 
     */
    protected Post() {
        super();
    }

    /**
     *<h3>修改帖子内容</h3>
     *<p>这是一个CQS命令方法，用于修改帖子内容，没有返回值。
     *<p>处理时，将参数封装到一个新的{@link PostContentAltered}事件对象中，并发
     *布它，最后由事件重放方法{@link #when(PostContentAltered)}更新当前聚合状态。
     *<p>具体的事件溯源机制参考{@link #apply(DomainEvent)}。
     * 
     * @param aSubject
     * @param aBodyText
     */
    protected void alterPostContent(String aSubject, String aBodyText) {

        this.assertArgumentNotEmpty(aSubject, "The subject must be provided.");
        this.assertArgumentNotEmpty(aBodyText, "The body text must be provided.");

        this.apply(new PostContentAltered(this.tenant(), this.forumId(), this.discussionId(),
                this.postId(), aSubject, aBodyText));
    }

    /**
     *<h3>处理{@link PostContentAltered}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(PostContentAltered anEvent) {
        this.setBodyText(anEvent.bodyText());
        this.setChangedOn(anEvent.occurredOn());
        this.setSubject(anEvent.subject());
    }

    /**
     *<h3>处理{@link PostedToDiscussion}事件。</h3>
     *<p>事件重放的方法。
     * 
     * @param anEvent
     */
    protected void when(PostedToDiscussion anEvent) {
        this.setAuthor(anEvent.author());
        this.setBodyText(anEvent.bodyText());
        this.setChangedOn(anEvent.occurredOn());
        this.setCreatedOn(anEvent.occurredOn());
        this.setDiscussionId(anEvent.discussionId());
        this.setForumId(anEvent.forumId());
        this.setPostId(anEvent.postId());
        this.setReplyToPostId(anEvent.replyToPost());
        this.setSubject(anEvent.subject());
        this.setTenant(anEvent.tenant());
    }

    private void setAuthor(Author anAuthor) {
        this.author = anAuthor;
    }

    private void setBodyText(String aBodyText) {
        this.bodyText = aBodyText;
    }

    private void setChangedOn(Date aChangedOnDate) {
        this.changedOn = aChangedOnDate;
    }

    private void setCreatedOn(Date aCreatedOnDate) {
        this.createdOn = aCreatedOnDate;
    }

    private void setDiscussionId(DiscussionId aDiscussionId) {
        this.discussionId = aDiscussionId;
    }

    private void setForumId(ForumId aForumId) {
        this.forumId = aForumId;
    }

    private void setPostId(PostId aPostId) {
        this.postId = aPostId;
    }

    private void setReplyToPostId(PostId aReplyToPostId) {
        this.replyToPostId = aReplyToPostId;
    }

    private void setSubject(String aSubject) {
        this.subject = aSubject;
    }

    private void setTenant(Tenant aTenant) {
        this.tenant = aTenant;
    }
}
