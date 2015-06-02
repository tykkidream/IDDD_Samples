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

/**
 *<h3>讨论的可用状态 - 值对象</h3>
 *
 */
public enum DiscussionAvailability  {

	/**
	 * 
	 */
    ADD_ON_NOT_ENABLED {
        public boolean isAddOnNotAvailable() {
            return true;
        }
    },

    /**
     * 失败
     */
    FAILED {
        public boolean isFailed() {
            return true;
        }
    },

    /**
     * 没有要求
     */
    NOT_REQUESTED {
        public boolean isNotRequested() {
            return true;
        }
    },

    /**
     * 请求
     */
    REQUESTED {
        public boolean isRequested() {
            return true;
        }
    },

    /**
     * 准备
     */
    READY {
        public boolean isReady() {
            return true;
        }
    };

    public boolean isAddOnNotAvailable() {
        return false;
    }

    public boolean isFailed() {
        return false;
    }

    public boolean isNotRequested() {
        return false;
    }

    public boolean isReady() {
        return false;
    }

    public boolean isRequested() {
        return false;
    }
}
