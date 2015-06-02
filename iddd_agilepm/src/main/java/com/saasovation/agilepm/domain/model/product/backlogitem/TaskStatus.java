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

package com.saasovation.agilepm.domain.model.product.backlogitem;

/**
 *<h3>任务状态 - 值对象</h3>
 *
 */
public enum TaskStatus  {

    /**
     *<h3>未启动</h3>
     *
     */
    NOT_STARTED {
        public boolean isNotStarted() {
            return true;
        }
    },

    /**
     *<h3>进行中</h3>
     *
     */
    IN_PROGRESS {
        public boolean isInProgress() {
            return true;
        }
    },

    /**
     *<h3>阻碍</h3>
     *
     */
    IMPEDED {
        public boolean isImpeded() {
            return true;
        }
    },

    /**
     *<h3>完成</h3>
     *
     */
    DONE {
        public boolean isDone() {
            return true;
        }
    };

    public boolean isDone() {
        return false;
    }

    public boolean isImpeded() {
        return false;
    }

    public boolean isInProgress() {
        return false;
    }

    public boolean isNotStarted() {
        return false;
    }
}
