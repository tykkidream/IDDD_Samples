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

import com.saasovation.agilepm.domain.model.ValueObject;

/**
 *<h3>优先级汇总 - 值对象</h3>
 *
 */
public class BusinessPriorityTotals extends ValueObject {

    /**
     * 总收益
     */
    private int totalBenefit;
    /**
     * 总成本
     */
    private int totalCost;
    /**
     * 总罚款
     */
    private int totalPenalty;
    /**
     * 总风险
     */
    private int totalRisk;
    /**
     * 总价值
     */
    private int totalValue;

    /**
     *<h3></h3>
     *
     * @param aTotalBenefit 总收益
     * @param aTotalPenalty 总罚款
     * @param aTotalValue 总价值
     * @param aTotalCost 总成本
     * @param aTotalRisk 总风险
     */
    public BusinessPriorityTotals(
            int aTotalBenefit,
            int aTotalPenalty,
            int aTotalValue,
            int aTotalCost,
            int aTotalRisk) {

        this();

        this.setTotalBenefit(aTotalBenefit);
        this.setTotalCost(aTotalCost);
        this.setTotalPenalty(aTotalPenalty);
        this.setTotalRisk(aTotalRisk);
        this.setTotalValue(aTotalValue);
    }

    /**
     *<h3></h3>
     *
     * @param aBusinessPriorityTotals
     */
    public BusinessPriorityTotals(BusinessPriorityTotals aBusinessPriorityTotals) {
        this(aBusinessPriorityTotals.totalBenefit(),
             aBusinessPriorityTotals.totalPenalty(),
             aBusinessPriorityTotals.totalValue(),
             aBusinessPriorityTotals.totalCost(),
             aBusinessPriorityTotals.totalRisk());
    }

    public int totalBenefit() {
        return this.totalBenefit;
    }

    public int totalCost() {
        return this.totalCost;
    }

    public int totalPenalty() {
        return this.totalPenalty;
    }

    public int totalRisk() {
        return this.totalRisk;
    }

    public int totalValue() {
        return this.totalValue;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            BusinessPriorityTotals typedObject = (BusinessPriorityTotals) anObject;
            equalObjects =
                this.totalBenefit() == typedObject.totalBenefit() &&
                this.totalCost() == typedObject.totalCost() &&
                this.totalPenalty() == typedObject.totalPenalty() &&
                this.totalRisk() == typedObject.totalRisk() &&
                this.totalValue() == typedObject.totalValue();
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (47091 * 19)
            + this.totalBenefit()
            + this.totalCost()
            + this.totalPenalty()
            + this.totalRisk()
            + this.totalValue();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "BusinessPriorityTotals [totalBenefit=" + totalBenefit + ", totalCost=" + totalCost + ", totalPenalty="
                + totalPenalty + ", totalRisk=" + totalRisk + ", totalValue=" + totalValue + "]";
    }

    /**
     *<h3></h3>
     *
     */
    private BusinessPriorityTotals() {
        super();
    }

    private void setTotalBenefit(int aTotalBenefit) {
        this.totalBenefit = aTotalBenefit;
    }

    private void setTotalCost(int aTotalCost) {
        this.totalCost = aTotalCost;
    }

    private void setTotalPenalty(int aTotalPenalty) {
        this.totalPenalty = aTotalPenalty;
    }

    private void setTotalRisk(int aTotalRisk) {
        this.totalRisk = aTotalRisk;
    }

    private void setTotalValue(int aTotalValue) {
        this.totalValue = aTotalValue;
    }
}
