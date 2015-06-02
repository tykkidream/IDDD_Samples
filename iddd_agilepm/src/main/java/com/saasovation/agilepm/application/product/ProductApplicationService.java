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

import java.util.Date;

import com.saasovation.agilepm.application.ApplicationServiceLifeCycle;
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor;
import com.saasovation.agilepm.domain.model.product.Product;
import com.saasovation.agilepm.domain.model.product.ProductDiscussionRequestTimedOut;
import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.ProductRepository;
import com.saasovation.agilepm.domain.model.team.ProductOwner;
import com.saasovation.agilepm.domain.model.team.ProductOwnerRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.process.ProcessId;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTracker;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTrackerRepository;

/**
 *<h3>产品业务 - 应用服务</h3>
 *
 */
public class ProductApplicationService {

    private TimeConstrainedProcessTrackerRepository processTrackerRepository;
    private ProductOwnerRepository productOwnerRepository;
    private ProductRepository productRepository;

    public ProductApplicationService(
            ProductRepository aProductRepository,
            ProductOwnerRepository aProductOwnerRepository,
            TimeConstrainedProcessTrackerRepository aProcessTrackerRepository) {

        super();

        this.processTrackerRepository = aProcessTrackerRepository;
        this.productOwnerRepository = aProductOwnerRepository;
        this.productRepository = aProductRepository;
    }

    // TODO: additional APIs / student assignment

    /**
     *<h3>发起讨论</h3>
     *<p>是一个长时处理过程的执行器。
     *@param aCommand
     */
    public void initiateDiscussion(InitiateDiscussionCommand aCommand) {
    	// 服务周期：开始。当前服务开始
        ApplicationServiceLifeCycle.begin();

        try {
        	// 出：从仓库中提取产品
            Product product = this.productRepository().productOfId(new TenantId(aCommand.getTenantId()), new ProductId(aCommand.getProductId()));

            if (product == null) {
                throw new IllegalStateException("Unknown product of tenant id: " + aCommand.getTenantId() + " and product id: " + aCommand.getProductId());
            }

            // 改：在产品上发起讨论
            product.initiateDiscussion(new DiscussionDescriptor(aCommand.getDiscussionId()));

            // 入：向仓库中保存产品
            this.productRepository().save(product);

            ProcessId processId = ProcessId.existingProcessId(product.discussionInitiationId());

            // 出：从仓库中提取过程状态。获取长时处理过程的状态对象。
            TimeConstrainedProcessTracker tracker = this.processTrackerRepository().trackerOfProcessId(aCommand.getTenantId(), processId);

            // 改：在过程状态上执行完成
            tracker.completed();

            // 入：向仓库中保存过程状态
            this.processTrackerRepository().save(tracker);

            // 服务周期：成功
            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
        	// 服务周期：失败
            ApplicationServiceLifeCycle.fail(e);
        }
    }

    /**
     *<h3>新的产品</h3>
     *@param aCommand
     *@return
     */
    public String newProduct(NewProductCommand aCommand) {

        return this.newProductWith(
                aCommand.getTenantId(),
                aCommand.getProductOwnerId(),
                aCommand.getName(),
                aCommand.getDescription(),
                DiscussionAvailability.NOT_REQUESTED);
    }

    /**
     *<h3>新的产品及讨论</h3>
     *@param aCommand
     *@return
     */
    public String newProductWithDiscussion(NewProductCommand aCommand) {

        return this.newProductWith(
                aCommand.getTenantId(),
                aCommand.getProductOwnerId(),
                aCommand.getName(),
                aCommand.getDescription(),
                this.requestDiscussionIfAvailable());
    }

    /**
     *<h3>申请产品讨论</h3>
     *@param aCommand
     */
    public void requestProductDiscussion(RequestProductDiscussionCommand aCommand) {
        // 出：从仓库中提取产品
        Product product = this.productRepository().productOfId(
                            new TenantId(aCommand.getTenantId()),
                            new ProductId(aCommand.getProductId()));

        if (product == null) {
            throw new IllegalStateException("Unknown product of tenant id: " + aCommand.getTenantId() + " and product id: " + aCommand.getProductId());
        }

        this.requestProductDiscussionFor(product);
    }

    /**
     *<h3>重新配置产品要求</h3>
     *@param aCommand
     */
    public void retryProductDiscussionRequest(RetryProductDiscussionRequestCommand aCommand) {

        ProcessId processId = ProcessId.existingProcessId(aCommand.getProcessId());

        TenantId tenantId = new TenantId(aCommand.getTenantId());

        // 出：从仓库中提取产品
        Product product = this.productRepository().productOfDiscussionInitiationId(tenantId, processId.id());

        if (product == null) {
            throw new IllegalStateException("Unknown product of tenant id: " + aCommand.getTenantId() + " and discussion initiation id: " + processId.id());
        }

        this.requestProductDiscussionFor(product);
    }

    /**
     *<h3>开始讨论</h3>
     *@param aCommand
     */
    public void startDiscussionInitiation(StartDiscussionInitiationCommand aCommand) {
    	// 服务周期：开始
        ApplicationServiceLifeCycle.begin();

        try {
        	// 出：从仓库中提取产品
            Product product = this.productRepository().productOfId(
                                new TenantId(aCommand.getTenantId()),
                                new ProductId(aCommand.getProductId()));

            if (product == null) {
                throw new IllegalStateException("Unknown product of tenant id: " + aCommand.getTenantId() + " and product id: " + aCommand.getProductId());
            }

            String timedOutEventName = ProductDiscussionRequestTimedOut.class.getName();

            //  改：创建过程状态
            // 长时处理过程开始时，将创建一个新的状态对象来跟踪事件的完成情况，它将
            // 与所有的领域事件共享一个唯一标识，同时将当前时间戳保存在状态对象。
            TimeConstrainedProcessTracker tracker = new TimeConstrainedProcessTracker(
                            product.tenantId().id(),
                            ProcessId.newProcessId(), // 生成新的过程ID
                            "Create discussion for product: " + product.name(),
                            new Date(), // 过程开始时间
                            5L * 60L * 1000L, // retries every 5 minutes
                            3, // 3 total retries
                            timedOutEventName);

            // 入：向仓库中保存过程状态
            this.processTrackerRepository().save(tracker);

            // 改：在产品上开始讨论
            product.startDiscussionInitiation(tracker.processId().id());
           
            // 入：向仓库中保存产品
            this.productRepository().save(product);

            // 服务周期：成功
            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
        	// 服务周期：失败
            ApplicationServiceLifeCycle.fail(e);
        }
    }

    public void timeOutProductDiscussionRequest(TimeOutProductDiscussionRequestCommand aCommand) {

        ApplicationServiceLifeCycle.begin();

        try {
            ProcessId processId = ProcessId.existingProcessId(aCommand.getProcessId());

            TenantId tenantId = new TenantId(aCommand.getTenantId());

            Product product =
                    this.productRepository()
                        .productOfDiscussionInitiationId(
                                tenantId,
                                processId.id());

            this.sendEmailForTimedOutProcess(product);

            product.failDiscussionInitiation();

            this.productRepository().save(product);

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }
    }

    private void sendEmailForTimedOutProcess(Product aProduct) {

        // TODO: Implement

    }

    /**
     *<h3></h3>
     *@param aTenantId
     *@param aProductOwnerId
     *@param aName
     *@param aDescription
     *@param aDiscussionAvailability
     *@return
     */
    private String newProductWith(
            String aTenantId,
            String aProductOwnerId,
            String aName,
            String aDescription,
            DiscussionAvailability aDiscussionAvailability) {

        TenantId tenantId = new TenantId(aTenantId);
        ProductId productId = null;

        ApplicationServiceLifeCycle.begin();

        try {
            productId = this.productRepository().nextIdentity();

            ProductOwner productOwner =
                    this.productOwnerRepository()
                        .productOwnerOfIdentity(
                                tenantId,
                                aProductOwnerId);

            Product product =
                    new Product(
                            tenantId,
                            productId,
                            productOwner.productOwnerId(),
                            aName,
                            aDescription,
                            aDiscussionAvailability);

            this.productRepository().save(product);

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }

        return productId.id();
    }

    private DiscussionAvailability requestDiscussionIfAvailable() {
        DiscussionAvailability availability = DiscussionAvailability.ADD_ON_NOT_ENABLED;

        boolean enabled = true; // TODO: determine add-on enabled

        if (enabled) {
            availability = DiscussionAvailability.REQUESTED;
        }

        return availability;
    }

    private TimeConstrainedProcessTrackerRepository processTrackerRepository() {
        return this.processTrackerRepository;
    }

    private ProductOwnerRepository productOwnerRepository() {
        return this.productOwnerRepository;
    }

    private ProductRepository productRepository() {
        return this.productRepository;
    }

    /**
     *<h3></h3>
     *@param aProduct
     */
    private void requestProductDiscussionFor(Product aProduct) {

        ApplicationServiceLifeCycle.begin();

        try {
            aProduct.requestDiscussion(this.requestDiscussionIfAvailable());

            this.productRepository().save(aProduct);

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }
    }
}
