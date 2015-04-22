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

import com.saasovation.common.AssertionConcern;

/**
 * <h3>警报 - 值对象</h3>
 *
 */
public final class Alarm extends AssertionConcern {

    private int alarmUnits;
    private AlarmUnitsType alarmUnitsType;

    public Alarm(AlarmUnitsType anAlarmUnitsType, int anAlarmUnits) {
        this();

        this.setAlarmUnits(anAlarmUnits);
        this.setAlarmUnitsType(anAlarmUnitsType);
    }

    /**
     * 
     * @return
     */
    public int alarmUnits() {
        return this.alarmUnits;
    }

    /**
     * 获取警报时间类型
     * @return
     */
    public AlarmUnitsType alarmUnitsType() {
        return this.alarmUnitsType;
    }

    /**
     *<h3>与其它对象判断是否相等</h3>
     *<p>本类是值对象，所以比较时只能同本类对象进行比较，
     *而且要对所有属性进行比较。
     */
    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Alarm typedObject = (Alarm) anObject;
            equalObjects =
                this.alarmUnitsType().equals(typedObject.alarmUnitsType()) &&
                this.alarmUnits() == typedObject.alarmUnits();
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (48483 * 97)
            + this.alarmUnitsType().hashCode()
            + this.alarmUnits();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Alarm [alarmUnits=" + alarmUnits + ", alarmUnitsType=" + alarmUnitsType + "]";
    }

    /**
     * <h3>初始化Alarm</h3>
     * <p>本方法是受保护的protected，只限本模块成员使用。
     */
    protected Alarm() {
        super();
    }

    /**
     *<h3>设置报警值</h3>
     *<p>POJO风格的Setter方法。
     *在本类带参的构造函数中使用，给属性赋值，只被使用了这一次。
     *另外可被一些框架调用赋值。
     * @param anAlarmUnits
     */
    protected void setAlarmUnits(int anAlarmUnits) {
        this.alarmUnits = anAlarmUnits;
    }

    /**
     * <h3>设置报警时间单位</h3>
     * <p>POJO风格的Setter方法。
     *在本类带参的构造函数中使用，给属性赋值，只被使用了这一次。
     *另外可被一些框架调用赋值。
     * @param anAlarmUnitsType
     */
    protected void setAlarmUnitsType(AlarmUnitsType anAlarmUnitsType) {
        this.alarmUnitsType = anAlarmUnitsType;
    }
}
