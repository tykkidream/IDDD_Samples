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
 *<h3>状态 - 值对象</h3>
 *
 */
public enum BacklogItemStatus {

    /**
     *<h3>计划完</h3>
     *
     */
    PLANNED {
        public boolean isPlanned() {
            return true;
        }
    },

    /**
     *<h3>排定完</h3>
     *
     */
    SCHEDULED {
        public boolean isScheduled() {
            return true;
        }
    },

    /**
     *<h3>承诺</h3>
     *
     */
    COMMITTED {
        public boolean isCommitted() {
            return true;
        }
    },

    /**
     *<h3>完成的</h3>
     *
     */
    DONE {
        public boolean isDone() {
            return true;
        }
    },

    /**
     *<h3>移除的</h3>
     *
     */
    REMOVED {
        public boolean isRemoved() {
            return true;
        }
    };

    public boolean isCommitted() {
        return false;
    }

    public boolean isDone() {
        return false;
    }

    public boolean isPlanned() {
        return false;
    }

    public boolean isRemoved() {
        return false;
    }

    public boolean isScheduled() {
        return false;
    }

    public BacklogItemStatus regress() {
        if (this.isPlanned()) {
            return PLANNED;
        } else if (this.isScheduled()) {
            return PLANNED;
        } else if (this.isCommitted()) {
            return SCHEDULED;
        } else if (this.isDone()) {
            return COMMITTED;
        } else if (this.isRemoved()) {
            return PLANNED;
        }

        return PLANNED;
    }
}
