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

import com.saasovation.common.domain.model.AbstractId;

/**
 *<h3>论坛讨论唯一标识 - 值对象</h3> 
 *
 */
public final class DiscussionId extends AbstractId {

    private static final long serialVersionUID = 1L;

    /**
     *<h3>构造Forum</h3>
     * <p>公共的构造函数，允许被模块（包）外访问，必须提供ID值。
     * @param anId
     */
    public DiscussionId(String anId) {
        super(anId);
    }

    /**
     *<h3>构造Forum</h3>
     * <p>受保护的构造函数，不允许模块（包）外访问。由于此函数没有参数，
     * 所有创建的实例是没有值的。
     * 
     */
    protected DiscussionId() {
        super();
    }

    @Override
    protected int hashOddValue() {
        return 11735;
    }

    @Override
    protected int hashPrimeValue() {
        return 37;
    }
}
