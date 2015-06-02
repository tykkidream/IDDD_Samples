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

package com.saasovation.common.domain.model;

import java.util.Date;

/**
 *<h3>领域事件</h3>
 *
 *<p>领域事件的名字将反映业务操作。
 *
 *<p>事件可能有不同的类型，这里关心的是领域事件。除此之外可能还有系统事件，
 *比如用于系统日志和监控的事件等。
 *
 * <p>事件驱动架构（Event Driven Architecture，EDA）是一种用于处理事件的生成
 * 发现和处理等任务的软件架构。
 * 
 * 
 * <p>事件驱动架构可以与分层架构等其它构架一同使用，其中与六边形架构一起使
 * 用是比较合适有好处的。一个系统的输出 端口所发出的领域事件将被发送到另一
 * 个系统的输入端口，此后输入端口的事件订阅方将对事件进行处理。对于不同的限
 * 界上下文来说，不同的领域事件具有不同含义，也有可能没有任何含义。
 * 
 * <p>事件基于消息，而基于消息的系统通常呈现一种管道和过滤器的风格。它有以下
 * 特征：
 * <ul>
 * <li>管理是消息通道：过滤器通过输入管道接收数据，通过输出管道发送数据。实际上
 * ，管道即是一个消息通道。</li>
 * <li>端口连接过滤器和管道：过滤器通过闸口连接到输入和输出管道。端口使得六边形
 * 架构成为首选的架构。</li>
 * <li>过滤器即是处理器：过滤器可以对消息进行处理，而不见得一定对消息进行过滤。</li>
 * <li>分离处理器：每个过滤处理器都是一个分离的组件。</li>
 * <li>松耦合：每个过滤处理器都相对独立地参与处理过程，处理器组合可以通过配置完
 * 成。</li>
 * <li>可换性：根据用例需求，我们可以重新组织不同处理器的执行顺序，这同样是通过
 * 配置完成。</li>
 * <li>过滤器可以使用多个管道：消息过滤器可以从不同的管道中读写数据，这表示了一
 * 种并行的处理过程。</li>
 * <li>并行使用同种类型的过滤器：对于最繁忙的和最慢的过滤器来说，我们可以并行地
 * 采用多个相同的过滤器来增加处理量。</li>
 * </ul>
 * 
 */
public interface DomainEvent {

	/**
	 * 事件版本
	 * @return
	 */
    public int eventVersion();

    /**
     * 事件发生时间
     * @return
     */
    public Date occurredOn();
}
