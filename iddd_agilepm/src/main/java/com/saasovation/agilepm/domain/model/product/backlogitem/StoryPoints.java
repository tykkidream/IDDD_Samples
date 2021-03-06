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
 *<h3>故事点</h3>
 *
 */
public enum StoryPoints  {

    ZERO {
        public int pointValue() {
            return 0;
        }
    },

    ONE {
        public int pointValue() {
            return 1;
        }
    },

    TWO {
        public int pointValue() {
            return 2;
        }
    },

    THREE {
        public int pointValue() {
            return 3;
        }
    },

    FIVE {
        public int pointValue() {
            return 5;
        }
    },

    EIGHT {
        public int pointValue() {
            return 8;
        }
    },

    THIRTEEN {
        public int pointValue() {
            return 13;
        }
    },

    TWENTY {
        public int pointValue() {
            return 20;
        }
    },

    FORTY {
        public int pointValue() {
            return 40;
        }
    },

    ONE_HUNDRED {
        public int pointValue() {
            return 100;
        }
    };

    public abstract int pointValue();
}
