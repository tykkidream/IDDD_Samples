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

package com.saasovation.agilepm.domain.model.product;

import com.saasovation.agilepm.domain.model.ValueObject;
import com.saasovation.agilepm.domain.model.discussion.DiscussionAvailability;
import com.saasovation.agilepm.domain.model.discussion.DiscussionDescriptor;

/**
 *<h3>产品讨论 - 值对象</h3>
 *<p>被本地用于关联到远程中实际的讨论。
 */
public final class ProductDiscussion extends ValueObject {

	/** 讨论可用状态 **/
    private DiscussionAvailability availability;
    /** 讨论的描述 **/
    private DiscussionDescriptor descriptor;

    /**
     *<h3>根据讨论的可用状态创建一个产品讨论</h3>
     *
     *
     * @param anAvailability
     * @return
     */
    public static ProductDiscussion fromAvailability(DiscussionAvailability anAvailability) {

        if (anAvailability.isReady()) {
            throw new IllegalArgumentException("Cannot be created ready.");
        }

        DiscussionDescriptor descriptor = new DiscussionDescriptor(DiscussionDescriptor.UNDEFINED_ID);

        return new ProductDiscussion(descriptor, anAvailability);
    }

    /**
     *<h3>构建ProductDiscussion</h3>
     * @param aDescriptor
     * @param anAvailability
     */
    public ProductDiscussion(DiscussionDescriptor aDescriptor, DiscussionAvailability anAvailability) {
        super();

        this.setAvailability(anAvailability);
        this.setDescriptor(aDescriptor);
    }

    /**
     *<h3>构建ProductDiscussion</h3>
     * 
     * @param aProductDiscussion
     */
    public ProductDiscussion(ProductDiscussion aProductDiscussion) {
        this(aProductDiscussion.descriptor(), aProductDiscussion.availability());
    }

    public DiscussionAvailability availability() {
        return this.availability;
    }

    public DiscussionDescriptor descriptor() {
        return this.descriptor;
    }

    /**
     *<h3>现在已经准备好</h3>
     *
     * @param aDescriptor
     * @return
     */
    public ProductDiscussion nowReady(DiscussionDescriptor aDescriptor) {
        if (aDescriptor == null || aDescriptor.isUndefined()) {
            throw new IllegalStateException("The discussion descriptor must be defined.");
        }
        if (!this.availability().isRequested()) {
            throw new IllegalStateException("The discussion must be requested first.");
        }

        return new ProductDiscussion(aDescriptor, DiscussionAvailability.READY);
    }

    
    private void setAvailability(DiscussionAvailability anAvailability) {
        this.assertArgumentNotNull(anAvailability, "The availability must be provided.");

        this.availability = anAvailability;
    }

    private void setDescriptor(DiscussionDescriptor aDescriptor) {
        this.assertArgumentNotNull(aDescriptor, "The descriptor must be provided.");

        this.descriptor = aDescriptor;
    }
}
