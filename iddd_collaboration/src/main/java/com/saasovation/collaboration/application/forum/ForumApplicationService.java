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

package com.saasovation.collaboration.application.forum;

import com.saasovation.collaboration.application.forum.data.ForumCommandResult;
import com.saasovation.collaboration.domain.model.collaborator.Author;
import com.saasovation.collaboration.domain.model.collaborator.CollaboratorService;
import com.saasovation.collaboration.domain.model.collaborator.Creator;
import com.saasovation.collaboration.domain.model.collaborator.Moderator;
import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.DiscussionId;
import com.saasovation.collaboration.domain.model.forum.DiscussionRepository;
import com.saasovation.collaboration.domain.model.forum.Forum;
import com.saasovation.collaboration.domain.model.forum.ForumId;
import com.saasovation.collaboration.domain.model.forum.ForumIdentityService;
import com.saasovation.collaboration.domain.model.forum.ForumRepository;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

/**
 *<h3>论坛的应用服务</h3>
 *
 */
public class ForumApplicationService {

    private CollaboratorService collaboratorService;
    private DiscussionQueryService discussionQueryService;
    private DiscussionRepository discussionRepository;
    private ForumIdentityService forumIdentityService;
    private ForumQueryService forumQueryService;
    private ForumRepository forumRepository;

    public ForumApplicationService(
            ForumQueryService aForumQueryService,
            ForumRepository aForumRepository,
            ForumIdentityService aForumIdentityService,
            DiscussionQueryService aDiscussionQueryService,
            DiscussionRepository aDiscussionRepository,
            CollaboratorService aCollaboratorService) {

        super();

        this.collaboratorService = aCollaboratorService;
        this.discussionQueryService = aDiscussionQueryService;
        this.discussionRepository = aDiscussionRepository;
        this.forumIdentityService = aForumIdentityService;
        this.forumQueryService = aForumQueryService;
        this.forumRepository = aForumRepository;
    }

    /**
     *<h3>改变论坛版主</h3>
     *@param aTenantId
     *@param aForumId 论坛ID
     *@param aModeratorId 版主ID
     */
    public void assignModeratorToForum(String aTenantId, String aForumId, String aModeratorId) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取论坛
        Forum forum = this.forumRepository().forumOfId(tenant, new ForumId(aForumId));

        // 出：从服务中查找版主
        Moderator moderator = this.collaboratorService().moderatorFrom(tenant, aModeratorId);

        // 改：在论坛上改变版主
        forum.assignModerator(moderator);

        // 入：向仓库中保存论坛
        this.forumRepository().save(forum);
    }

    /**
     *<h3>修改论坛的描述</h3>
     *@param aTenantId
     *@param aForumId
     *@param aDescription
     */
    public void changeForumDescription(String aTenantId, String aForumId, String aDescription) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取论坛
        Forum forum = this.forumRepository().forumOfId(tenant, new ForumId(aForumId));

        // 改：在论坛上修改描述
        forum.changeDescription(aDescription);

        // 入：向仓库中保存论坛
        this.forumRepository().save(forum);
    }

    /**
     *<h3>改变论坛的主题</h3>
     *@param aTenantId
     *@param aForumId 论坛ID
     *@param aSubject 主题
     */
    public void changeForumSubject(String aTenantId, String aForumId, String aSubject) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取论坛
        Forum forum = this.forumRepository().forumOfId(tenant, new ForumId(aForumId));

        // 改：在论坛上改变主题
        forum.changeSubject(aSubject);

        // 入：向仓库中保存论坛
        this.forumRepository().save(forum);
    }

    /**
     *<h3>关闭论坛</h3>
     *@param aTenantId
     *@param aForumId
     */
    public void closeForum(String aTenantId, String aForumId) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取论坛
        Forum forum = this.forumRepository().forumOfId(tenant, new ForumId(aForumId));

        // 改：在论坛上执行关闭
        forum.close();

        // 入：向仓库中保存论坛
        this.forumRepository().save(forum);
    }

    /**
     *<h3>重开论坛</h3>
     *@param aTenantId
     *@param aForumId
     */
    public void reopenForum(String aTenantId, String aForumId) {
        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取论坛
        Forum forum = this.forumRepository().forumOfId(tenant, new ForumId(aForumId));

        // 改：在论坛上执行开放
        forum.reopen();

        // 入：向仓库中保存论坛
        this.forumRepository().save(forum);
    }

    /**
     *<h3>开始一个新论坛</h3>
     *@param aTenantId
     *@param aCreatorId
     *@param aModeratorId
     *@param aSubject
     *@param aDescription
     *@param aResult
     */
    public void startForum(String aTenantId, String aCreatorId, String aModeratorId,
            String aSubject, String aDescription, ForumCommandResult aResult) {

    	// 改：创建一个新论坛
        Forum forum = this.startNewForum(new Tenant(aTenantId), aCreatorId, aModeratorId, aSubject, aDescription, null);

        if (aResult != null) {
            // 设置当前命令的执行结果
            aResult.resultingForumId(forum.forumId().id());
        }
    }

    /**
     *<h3>开始一个新论坛</h3>
     *@param aTenantId
     *@param anExclusiveOwner
     *@param aCreatorId
     *@param aModeratorId
     *@param aSubject
     *@param aDescription
     *@param aResult
     */
    public void startExclusiveForum(String aTenantId, String anExclusiveOwner, String aCreatorId,
            String aModeratorId, String aSubject, String aDescription, ForumCommandResult aResult) {

        Tenant tenant = new Tenant(aTenantId);

        // 查：从Query服务中查询论坛ID
        String forumId = this.forumQueryService().forumIdOfExclusiveOwner(aTenantId, anExclusiveOwner);

        Forum forum = null;

        if (forumId != null) {
            // 出：从仓库中提取论坛
            forum = this.forumRepository().forumOfId(tenant, new ForumId(forumId));
        }

        if (forum == null) {
        	// 改：创建一个新论坛
            forum = this.startNewForum(tenant, aCreatorId, aModeratorId, aSubject, aDescription, anExclusiveOwner);
        }

        if (aResult != null) {
            // 设置当前命令的执行结果
            aResult.resultingForumId(forum.forumId().id());
        }
    }

    /**
     *<h3>在讨论中开始一个新讨论</h3>
     *@param aTenantId
     *@param anExclusiveOwner
     *@param aCreatorId
     *@param aModeratorId
     *@param anAuthorId
     *@param aForumSubject
     *@param aForumDescription
     *@param aDiscussionSubject
     *@param aResult
     */
    public void startExclusiveForumWithDiscussion(String aTenantId, String anExclusiveOwner, 
    		String aCreatorId, String aModeratorId, String anAuthorId, String aForumSubject, 
    		String aForumDescription, String aDiscussionSubject, ForumCommandResult aResult) {

        Tenant tenant = new Tenant(aTenantId);

        // 查：从Query服务中查询论坛ID
        String forumId = this.forumQueryService().forumIdOfExclusiveOwner(aTenantId, anExclusiveOwner);

        Forum forum = null;

        if (forumId != null) {
            // 出：从仓库中提取论坛
            forum = this.forumRepository().forumOfId(tenant, new ForumId(forumId));
        }

        if (forum == null) {
        	// 改：创建一个新论坛
            forum = this.startNewForum(tenant, aCreatorId, aModeratorId, aForumSubject, aForumDescription, anExclusiveOwner);
        }

        // 查：从Query服务中查询讨论ID
        String discussionId = this.discussionQueryService().discussionIdOfExclusiveOwner(aTenantId, anExclusiveOwner);

        Discussion discussion = null;

        if (discussionId != null) {
            // 出：从仓库中提取讨论
            discussion = this.discussionRepository().discussionOfId(tenant, new DiscussionId(discussionId));
        }

        if (discussion == null) {
            // 出：从服务中查找创作者
            Author author = this.collaboratorService().authorFrom(tenant, anAuthorId);

        	// 改：在论坛上开始一个新讨论
            discussion = forum.startDiscussionFor(this.forumIdentityService(), author, aDiscussionSubject, anExclusiveOwner);

            // 入：向仓库中保存讨论
            this.discussionRepository().save(discussion);
        }

        if (aResult != null) {
            // 设置当前命令的执行结果
            aResult.resultingForumId(forum.forumId().id());
            aResult.resultingDiscussionId(discussion.discussionId().id());
        }
    }

    private CollaboratorService collaboratorService() {
        return this.collaboratorService;
    }

    private DiscussionQueryService discussionQueryService() {
        return this.discussionQueryService;
    }

    private DiscussionRepository discussionRepository() {
        return this.discussionRepository;
    }

    private ForumIdentityService forumIdentityService() {
        return this.forumIdentityService;
    }

    private ForumQueryService forumQueryService() {
        return this.forumQueryService;
    }

    private ForumRepository forumRepository() {
        return this.forumRepository;
    }

    /**
     *<h3>开始一个新论坛</h3>
     *@param aTenant
     *@param aCreatorId
     *@param aModeratorId
     *@param aSubject
     *@param aDescription
     *@param anExclusiveOwner
     *@return
     */
    private Forum startNewForum(Tenant aTenant, String aCreatorId, String aModeratorId,
            String aSubject, String aDescription, String anExclusiveOwner) {

        // 出：从服务中查找创作者
        Creator creator = this.collaboratorService().creatorFrom(aTenant, aCreatorId);

        // 出：从服务中查找版主
        Moderator moderator = this.collaboratorService().moderatorFrom(aTenant, aModeratorId);

        // 改：创建论坛
        Forum newForum = new Forum(aTenant, this.forumRepository().nextIdentity(), 
        		creator, moderator, aSubject, aDescription, anExclusiveOwner);

        // 入：向仓库中保存论坛
        this.forumRepository().save(newForum);

        return newForum;
    }
}
