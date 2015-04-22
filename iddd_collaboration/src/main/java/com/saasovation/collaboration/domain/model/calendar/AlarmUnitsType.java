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

package com.saasovation.collaboration.domain.model.calendar;

/**
 *<h3> 警报的时间单位 - 值对象</h3>
 *<p>主要有 {@link #Days 天}、 {@link #Hours 小时}、 {@link #Minutes 分钟} 这三个单位。
 */
public enum AlarmUnitsType {

	/**
	 * 天
	 */
    Days {
        public boolean isDays() {
            return true;
        }
    },

    /**
     * 小时
     */
    Hours {
        public boolean isHours() {
            return true;
        }
    },

    /**
     * 分钟
     */
    Minutes {
        public boolean isMinutes() {
            return true;
        }
    };

    public boolean isDays() {
        return false;
    }

    public boolean isHours() {
        return false;
    }

    public boolean isMinutes() {
        return false;
    }
}
