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

import java.util.Date;

import com.saasovation.common.AssertionConcern;

/**
 *<h3>重复时间 - 值对象</h3>
 *<p>指日历任务的重复情况，比如每周一。
 */
public final class Repetition extends AssertionConcern {

	/** 时间 **/
    private Date ends;
    /** 重复类型 **/
    private RepeatType repeats;

    /**
     *<h3>创建一个不重复实例</h3>
     *<p>这是一个工厂方法。
     *
     * @param anEnds
     * @return
     */
    public static Repetition doesNotRepeatInstance(Date anEnds) {
        return new Repetition(RepeatType.DoesNotRepeat, anEnds);
    }

    /**
     *<h3>创建一个无限期重复实例</h3>
     *<p>这是一个工厂方法。
     * 
     * @param aRepeatType
     * @return
     */
    public static Repetition indefinitelyRepeatsInstance(RepeatType aRepeatType) {
        Date ends = new Date(31536000000000L); // 1000 years from 1/1/1970

        return new Repetition(aRepeatType, ends);
    }

    public Repetition(RepeatType aRepeats, Date anEndsOn) {
        super();

        this.setEnds(anEndsOn);
        this.setRepeats(aRepeats);
    }

    public Date ends() {
        return this.ends;
    }

    public RepeatType repeats() {
        return this.repeats;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Repetition typedObject = (Repetition) anObject;
            equalObjects =
                this.repeats().name().equals(typedObject.repeats().name()) &&
                this.ends().equals(typedObject.ends());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
                + (7895 * 83)
                + this.repeats().name().hashCode()
                + this.ends().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Repetition [repeats=" + repeats + ", ends=" + ends + "]";
    }

    private void setEnds(Date anEnds) {
        this.assertArgumentNotNull(anEnds, "The ends date must be provided.");

        this.ends = anEnds;
    }

    private void setRepeats(RepeatType aRepeatType) {
        this.assertArgumentNotNull(aRepeatType, "The repeat type must be provided.");

        this.repeats = aRepeatType;
    }
}
