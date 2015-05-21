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
 *<h3>待定项类型 - 值对象</h3>
 *
 */
public enum BacklogItemType  {

    /**
     *<h3特征</h3>
     *
     */
    FEATURE {
        public boolean isFeature() {
            return true;
        }
    },

    /**
     *<h3>增强</h3>
     *
     */
    ENHANCEMENT {
        public boolean isEnhancement() {
            return true;
        }
    },

    /**
     *<h3>缺陷</h3>
     *
     */
    DEFECT {
        public boolean isDefect() {
            return true;
        }
    },

    /**
     *<h3>基础</h3>
     *
     */
    FOUNDATION {
        public boolean isFoundation() {
            return true;
        }
    },

    /**
     *<h3>集成</h3>
     *
     */
    INTEGRATION {
        public boolean isIntegration() {
            return true;
        }
    };

    public boolean isDefect() {
        return false;
    }

    public boolean isEnhancement() {
        return false;
    }

    public boolean isFeature() {
        return false;
    }

    public boolean isFoundation() {
        return false;
    }

    public boolean isIntegration() {
        return false;
    }
}
