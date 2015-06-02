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

import java.util.Date;

/**
 *<h3>长时处理过程</h3>
 *
 *<p>长时处理过程是一种事件驱动的、分布式的并行处理模式。
 *
 *<p>有可能出现这样一种情况：在一个任务处理过程中，某种领域事件只能表示该过程中的一部分。
 *只有在所有的参与事件都处理之后，我们才能认为这个多任务处理过程完成了。
 *
 *<p>在某个用例中开始执行一个任务，当这个任务完成时，将发布一个事件，此时整个处理过程便开
 *始了。接下来有一个事件处理组件订阅了刚才发布的事件，而这个处理组件完成时，也将发布一个
 *新事件，整个处理流程继续。以此类推，下一个事件处理组件将处理上一个事件处理组件所发布的
 *事件，不断地迭代这样的过程，直到最后不再发布事件或没有事件处理组件，此时整个流程才执行
 *完毕。
 *
 *<p>长时处理过程是很灵活的，可以根据需要加入新事件和新过滤器，还应慎重地配置各个处理器的
 *顺序。不过一般不会经常性地改变领域事件的处理流程。在真实的企业应用里，通过这种模式将一个
 *大问题分解成若干个较小的步骤来完成，这使得分布式处理更容易理解和管理。而在真实的DDD应
 *用场景中，领域事件的名字将反映业务操作，每个步骤可以发生在相同的或不同的限界上下文中。
 *
 *<p>对于跟踪有些长时处理过程来说，需要考虑时间敏感性。在过程处理超时，既可以采用被动的
 *，亦可以采取主动。参考 {@link TimeConstrainedProcessTracker} 。
 */
public interface Process {

    public enum ProcessCompletionType {
        NotCompleted,
        CompletedNormally,
        TimedOut
    }

    public long allowableDuration();

    public boolean canTimeout();

    public long currentDuration();

    public String description();

    public boolean didProcessingComplete();

    public void informTimeout(Date aTimedOutDate);

    /**
     *<h3>过程是否已完成</h3>
     *
     *<p>长时处理过程的状态通常有一个名为 isCompleted() 的方法。每当某个执行流程执行完成，
     *其对应的状态属性也将随之更新，随后执行器将这调用 isComplete() 方法。该方法检查所有的
     *并行执行流程是否全部支行完毕。当 isCompleted() 返回 true 时，执行器将根据业务需要发布最
     *终的领域事件。如果该长时处理过程是更在的并行处理过程中一个分支，那么向外发布该事件
     *便是非常有必要的了。
     *
     *@return
     */
    public boolean isCompleted();

    /**
     *<h3>过程是否已超时</h3>
     *
     *
     *@return
     */
    public boolean isTimedOut();

    public boolean notCompleted();

    public ProcessCompletionType processCompletionType();

    public ProcessId processId();

    public Date startTime();

    public TimeConstrainedProcessTracker timeConstrainedProcessTracker();

    public Date timedOutDate();

    public long totalAllowableDuration();

    public int totalRetriesPermitted();
}
