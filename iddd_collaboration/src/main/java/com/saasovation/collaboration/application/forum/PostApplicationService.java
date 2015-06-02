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

import com.saasovation.collaboration.domain.model.collaborator.CollaboratorService;
import com.saasovation.collaboration.domain.model.collaborator.Moderator;
import com.saasovation.collaboration.domain.model.forum.Forum;
import com.saasovation.collaboration.domain.model.forum.ForumId;
import com.saasovation.collaboration.domain.model.forum.ForumRepository;
import com.saasovation.collaboration.domain.model.forum.Post;
import com.saasovation.collaboration.domain.model.forum.PostId;
import com.saasovation.collaboration.domain.model.forum.PostRepository;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

/**
 *<h3>帖子的应用服务</h3>
 *
 */
public class PostApplicationService {

    private CollaboratorService collaboratorService;
    private ForumRepository forumRepository;
    private PostRepository postRepository;

    public PostApplicationService(
            PostRepository aPostRepository,
            ForumRepository aForumRepository,
            CollaboratorService aCollaboratorService) {

        super();

        this.collaboratorService = aCollaboratorService;
        this.forumRepository = aForumRepository;
        this.postRepository = aPostRepository;
    }

    public void moderatePost(String aTenantId, String aForumId, String aPostId,
            String aModeratorId, String aSubject, String aBodyText) {

        Tenant tenant = new Tenant(aTenantId);

        // 出：从仓库中提取论坛
        Forum forum = this.forumRepository().forumOfId(tenant, new ForumId(aForumId));

        // 出：从服务中查找版主
        Moderator moderator = this.collaboratorService().moderatorFrom(tenant, aModeratorId);

        // 出：从仓库中提取帖子
        Post post = this.postRepository().postOfId(tenant, new PostId(aPostId));

        // 改：在帖子上修改内容
        forum.moderatePost(post, moderator, aSubject, aBodyText);

        // 入：向仓库中保存帖子
        this.postRepository().save(post);
    }

    private CollaboratorService collaboratorService() {
        return this.collaboratorService;
    }

    private ForumRepository forumRepository() {
        return this.forumRepository;
    }

    private PostRepository postRepository() {
        return this.postRepository;
    }
}
