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

package com.saasovation.common.domain.model.process;

import java.util.UUID;

import com.saasovation.common.domain.model.AbstractId;

/**
 *<h3>长时处理过程的唯一标识 - 值对象</h3>
 *<p>由于所有的构造函数的访问级别都为 protected ，外部创建实例时只
 *能通过 {@link #existingProcessId(String)} 和 {@link #newProcessId()} 。
 *
 */
public final class ProcessId extends AbstractId {

    private static final long serialVersionUID = 1L;

    /**
     *<h3>现有的过程ID</h3>
     *<p>这是一个工厂方法，返回一个已存在的产品ID实例。
     *@param anId
     *@return
     */
    public static ProcessId existingProcessId(String anId) {
        ProcessId processId = new ProcessId(anId);

        return processId;
    }

    /**
     *<h3>新的过程ID</h3>
     *<p>这是一个工厂方法，创建一个已存在的过程ID实例，id值为UUID。
     *@return
     */
    public static ProcessId newProcessId() {
        ProcessId processId = new ProcessId(UUID.randomUUID().toString().toLowerCase());

        return processId;
    }

    /**
     *<h3>构建ProcessId</h3>
     *<p>需要提供有效的ID值。
     *
     *@param anId
     */
    protected ProcessId(String anId) {
        super(anId);
    }

    /**
     *<h3>构建ProcessId</h3>
     */
    protected ProcessId() {
        super();
    }

    @Override
    protected int hashOddValue() {
        return 3773;
    }

    @Override
    protected int hashPrimeValue() {
        return 43;
    }
}
