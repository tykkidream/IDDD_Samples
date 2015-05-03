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

package com.saasovation.agilepm.domain.model.discussion;

import com.saasovation.agilepm.domain.model.ValueObject;

/**
 *<h3>讨论的描述 - 值对象</h3>
 *
 */
public class DiscussionDescriptor extends ValueObject {

    public static final String UNDEFINED_ID = "UNDEFINED";

    /**
     * ID。
     *<p>注意：这个ID不同与实体的唯一标识，这是值对象类型，
     *它实际关联了另外一个限界上下文中的讨论聚合，而且它的
     *唯一标识也是值对象类型，所以这里保存讨论聚合中ID的属
     *性的值。
     **/
    private String id;

    /**
     *<h3>初始化</h3>
     * @param anId
     */
    public DiscussionDescriptor(String anId) {
        this();

        this.setId(anId);
    }

    /**
     *<h3>以另外一个DiscussionDescriptor的ID初始化当前对象</h3>
     * @param aDiscussionDescriptor
     */
    public DiscussionDescriptor(DiscussionDescriptor aDiscussionDescriptor) {
        this(aDiscussionDescriptor.id());
    }

    public String id() {
        return this.id;
    }

    /**
     *<h3>是否未定义</h3>
     *<p>根据ID是否是{@link #UNDEFINED_ID}判定。
     * @return
     */
    public boolean isUndefined() {
        return this.id().equals(UNDEFINED_ID);
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            DiscussionDescriptor typedObject = (DiscussionDescriptor) anObject;
            equalObjects = this.id().equals(typedObject.id());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (72881 * 101)
            + this.id().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "DiscussionDescriptor [id=" + id + "]";
    }

    private DiscussionDescriptor() {
        super();
    }

    private void setId(String anId) {
        this.assertArgumentNotEmpty(anId, "The discussion identity must be provided.");
        this.assertArgumentLength(anId, 36, "The discussion identity must be 36 characters or less.");

        this.id = anId;
    }
}
