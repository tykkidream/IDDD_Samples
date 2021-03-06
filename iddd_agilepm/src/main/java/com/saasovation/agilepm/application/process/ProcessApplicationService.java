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

package com.saasovation.agilepm.application.process;

import java.util.Collection;

import com.saasovation.agilepm.application.ApplicationServiceLifeCycle;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTracker;
import com.saasovation.common.domain.model.process.TimeConstrainedProcessTrackerRepository;

/**
 *<h3>长时处理过程 - 应用服务</h3>
 *
 */
public class ProcessApplicationService {

    private TimeConstrainedProcessTrackerRepository processTrackerRepository;

    public ProcessApplicationService(
            TimeConstrainedProcessTrackerRepository aProcessorTrackerRepository) {

        super();

        this.processTrackerRepository = aProcessorTrackerRepository;
    }

    /**
     *<h3>检查超时处理过程</h3>
     */
    public void checkForTimedOutProcesses() {
        ApplicationServiceLifeCycle.begin();

        try {
        	// 加载：所有超时的跟踪状态
            Collection<TimeConstrainedProcessTracker> trackers = this.processTrackerRepository().allTimedOut();

            for (TimeConstrainedProcessTracker tracker : trackers) {
            	// 命令：通知长时处理过程已超时
                tracker.informProcessTimedOut();

                this.processTrackerRepository().save(tracker);
            }

            ApplicationServiceLifeCycle.success();

        } catch (RuntimeException e) {
            ApplicationServiceLifeCycle.fail(e);
        }
    }

    private TimeConstrainedProcessTrackerRepository processTrackerRepository() {
        return this.processTrackerRepository;
    }
}
