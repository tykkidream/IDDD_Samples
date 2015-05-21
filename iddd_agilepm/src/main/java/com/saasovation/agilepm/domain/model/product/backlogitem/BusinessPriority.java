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
 *<h3>业务优先级 - 值对象</h3>
 *
 */
public class BusinessPriority extends ValueObject {

    /**
     * 业务优先级排序
     */
    private BusinessPriorityRatings ratings;

    /**
     *<h3></h3>
     *
     * @param aBusinessPriorityRatings
     */
    public BusinessPriority(BusinessPriorityRatings aBusinessPriorityRatings) {
        this();

        this.setRatings(aBusinessPriorityRatings);
    }

    /**
     *<h3></h3>
     *
     * @param aBusinessPriority
     */
    public BusinessPriority(BusinessPriority aBusinessPriority) {
        this(new BusinessPriorityRatings(aBusinessPriority.ratings()));
    }

    /**
     *<h3>成本所占百分比</h3>
     *
     * @param aTotals
     * @return
     */
    public float costPercentage(BusinessPriorityTotals aTotals) {
        return (float) 100 * this.ratings().cost() / aTotals.totalCost();
    }

    /**
     *<h3>计算优先值</h3>
     *
     * @param aTotals
     * @return
     */
    public float priority(BusinessPriorityTotals aTotals) {
        float costAndRisk = this.costPercentage(aTotals) + this.riskPercentage(aTotals);

        return this.valuePercentage(aTotals) / costAndRisk;
    }

    /**
     *<h3>风险所占百分比</h3>
     *
     * @param aTotals
     * @return
     */
    public float riskPercentage(BusinessPriorityTotals aTotals) {
        return (float) 100 * this.ratings().risk() / aTotals.totalRisk();
    }

    /**
     *<h3>计算总价值</h3>
     *
     * @return
     */
    public float totalValue() {
        return this.ratings().benefit() + this.ratings().penalty();
    }

    /**
     *<h3>价值所占百分比</h3>
     *
     * @param aTotals
     * @return
     */
    public float valuePercentage(BusinessPriorityTotals aTotals) {
        return (float) 100 * this.totalValue() / aTotals.totalValue();
    }

    /**
     *<h3>获取优先级排序</h3>
     *
     * @return
     */
    public BusinessPriorityRatings ratings() {
        return this.ratings;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            BusinessPriority typedObject = (BusinessPriority) anObject;
            equalObjects = this.ratings().equals(typedObject.ratings());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (15681 * 13)
            + this.ratings().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "BusinessPriority [ratings=" + ratings + "]";
    }

    
    /**
     *<h3></h3>
     *<p>
     */
    private BusinessPriority() {
        super();
    }

    private void setRatings(BusinessPriorityRatings aRatings) {
        this.assertArgumentNotNull(aRatings, "The ratings must be provided.");

        this.ratings = aRatings;
    }
}
