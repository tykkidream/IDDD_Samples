package com.saasovation.agilepm.application.sprint;

import com.saasovation.agilepm.domain.model.product.backlogitem.*;
import com.saasovation.agilepm.domain.model.product.sprint.*;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

/**
 *<h3>冲刺业务 - 应用服务</h3>
 *
 */
public class SprintApplicationService {

    private BacklogItemRepository backlogItemRepository;
    private SprintRepository sprintRepository;

    public SprintApplicationService(
            SprintRepository aSprintRepository,
            BacklogItemRepository aBacklogItemRepository) {

        super();

        this.backlogItemRepository = aBacklogItemRepository;
        this.sprintRepository = aSprintRepository;
    }

    /**
     *<h3>提交待定项到冲刺</h3>
     *@param aCommand
     */
    public void commitBacklogItemToSprint(CommitBacklogItemToSprintCommand aCommand) {

        TenantId tenantId = new TenantId(aCommand.getTenantId());

        // 加载：从仓库中加载冲刺
        Sprint sprint =  this.sprintRepository().sprintOfId(tenantId, new SprintId(aCommand.getSprintId()));

        // 加载：从仓库中加载待定项
        BacklogItem backlogItem = this.backlogItemRepository().backlogItemOfId(tenantId, new BacklogItemId(aCommand.getBacklogItemId()));

        // 命令：在冲刺上提交待定项
        sprint.commit(backlogItem);

        // 向仓库中保存冲刺
        this.sprintRepository().save(sprint);
    }

    private BacklogItemRepository backlogItemRepository() {
        return this.backlogItemRepository;
    }

    private SprintRepository sprintRepository() {
        return this.sprintRepository;
    }
}
