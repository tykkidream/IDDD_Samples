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

import com.saasovation.collaboration.application.forum.data.DiscussionCommandResult;
import com.saasovation.collaboration.domain.model.collaborator.Author;
import com.saasovation.collaboration.domain.model.collaborator.CollaboratorService;
import com.saasovation.collaboration.domain.model.forum.Discussion;
import com.saasovation.collaboration.domain.model.forum.DiscussionId;
import com.saasovation.collaboration.domain.model.forum.DiscussionRepository;
import com.saasovation.collaboration.domain.model.forum.ForumIdentityService;
import com.saasovation.collaboration.domain.model.forum.Post;
import com.saasovation.collaboration.domain.model.forum.PostId;
import com.saasovation.collaboration.domain.model.forum.PostRepository;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

/**
 *<h3>讨论的应用服务</h3>
 *
 */
public class DiscussionApplicationService {

    private CollaboratorService collaboratorService;
    private DiscussionRepository discussionRepository;
    private ForumIdentityService forumIdentityService;
    private PostRepository postRepository;

    public DiscussionApplicationService(
            DiscussionRepository aDiscussionRepository,
            ForumIdentityService aForumIdentityService,
            PostRepository aPostRepository,
            CollaboratorService aCollaboratorService) {

        super();

        this.collaboratorService = aCollaboratorService;
        this.discussionRepository = aDiscussionRepository;
        this.forumIdentityService = aForumIdentityService;
        this.postRepository = aPostRepository;
    }

    public void closeDiscussion(String aTenantId, String aDiscussionId) {
    	// 出：从仓库中提取讨论
        Discussion discussion = this.discussionRepository().discussionOfId(new Tenant(aTenantId), new DiscussionId(aDiscussionId));

        // 改：在讨论上执行关闭
        discussion.close();

        // 入：向仓库中保存讨论
        this.discussionRepository().save(discussion);
    }

    /**
     *<h3>开始一个新讨论帖</h3>
     *@param aTenantId
     *@param aDiscussionId
     *@param anAuthorId
     *@param aSubject
     *@param aBodyText
     *@param aDiscussionCommandResult
     */
    public void postToDiscussion(String aTenantId, String aDiscussionId, String anAuthorId, String aSubject,
            String aBodyText, DiscussionCommandResult aDiscussionCommandResult) {
    	// 出：从仓库中提取讨论
        Discussion discussion = this.discussionRepository().discussionOfId(new Tenant(aTenantId), new DiscussionId(aDiscussionId));

        // 出：从仓库中提取作者
        Author author = this.collaboratorService().authorFrom(new Tenant(aTenantId), anAuthorId);

        // 改：在讨论上开始一个新的讨论帖
        Post post = discussion.post(this.forumIdentityService(), author, aSubject, aBodyText);

        // 入：向仓库中保存讨论
        this.postRepository().save(post);

        // 设置当前命令的执行结果
        aDiscussionCommandResult.resultingDiscussionId(aDiscussionId);
        aDiscussionCommandResult.resultingPostId(post.postId().id());
    }

    /**
     *<h3>回复一个讨论帖</h3>
     *@param aTenantId
     *@param aDiscussionId
     *@param aReplyToPostId
     *@param anAuthorId
     *@param aSubject
     *@param aBodyText
     *@param aDiscussionCommandResult
     */
    public void postToDiscussionInReplyTo(String aTenantId, String aDiscussionId, String aReplyToPostId, String anAuthorId,
            String aSubject, String aBodyText, DiscussionCommandResult aDiscussionCommandResult) {
    	// 出：从仓库中提取讨论
        Discussion discussion = this.discussionRepository().discussionOfId(new Tenant(aTenantId), new DiscussionId(aDiscussionId));

        // 出：从仓库中提取作者
        Author author = this.collaboratorService().authorFrom(new Tenant(aTenantId), anAuthorId);

        // 改：在讨论上开始一个新的回复帖
        Post post = discussion.post(this.forumIdentityService(), new PostId(aReplyToPostId), author, aSubject, aBodyText);

        // 入：向仓库中保存讨论
        this.postRepository().save(post);

        // 设置当前命令的执行结果
        aDiscussionCommandResult.resultingDiscussionId(aDiscussionId);
        aDiscussionCommandResult.resultingPostId(post.postId().id());
        aDiscussionCommandResult.resultingInReplyToPostId(aReplyToPostId);
    }

    /**
     *<h3>重新开始讨论</h3>
     *@param aTenantId
     *@param aDiscussionId
     */
    public void reopenDiscussion(String aTenantId, String aDiscussionId) {
        // 出：从仓库中提取讨论
        Discussion discussion = this.discussionRepository().discussionOfId(new Tenant(aTenantId), new DiscussionId(aDiscussionId));

        // 改：在讨论上重新开始
        discussion.reopen();

        // 入：向仓库中保存讨论
        this.discussionRepository().save(discussion);
    }

    private CollaboratorService collaboratorService() {
        return this.collaboratorService;
    }

    private DiscussionRepository discussionRepository() {
        return this.discussionRepository;
    }

    private ForumIdentityService forumIdentityService() {
        return this.forumIdentityService;
    }

    private PostRepository postRepository() {
        return this.postRepository;
    }
}
