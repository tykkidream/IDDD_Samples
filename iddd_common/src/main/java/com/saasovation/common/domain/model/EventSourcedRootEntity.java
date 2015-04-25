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

package com.saasovation.common.domain.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.saasovation.common.AssertionConcern;

/**
 * <p>事件源根实体。
 *
 */
public abstract class EventSourcedRootEntity extends AssertionConcern {

	/**
	 * <p>重放事件、重建聚合状态的方法的名称。
	 * <p>这样的方法是多个的重载方法，它们的名称相同，但是参数是不同事件类型。
	 * <p>因此，在定义重建聚合状态的方法时，它的名称一定要与这里相同。
	 **/
    private static final String MUTATOR_METHOD_NAME = "when";

    /**
     * <p>保存重建聚合状态方法的Method对象的缓存。
     */
    private static Map<String, Method> mutatorMethods =
            new HashMap<String, Method>();

    /**
     * <p>重建聚合的事件流。
     * 
     */
    private List<DomainEvent> mutatingEvents;
    
    /**
     * <p>事件流的版本。
     */
    private int unmutatedVersion;

    public int mutatedVersion() {
        return this.unmutatedVersion() + 1;
    }

    /**
     * <p>获取重建聚合的事件流。
     * <p>
     * @return
     */
    public List<DomainEvent> mutatingEvents() {
        return this.mutatingEvents;
    }

    /**
     * <p>获取以事件源方式构建聚合时使用的事件流的版本。
     * <p>注意：这是获取数据的方法，领域模型的风格，不是POJO风格，
     * 也没有公共的修改此数据的方法。
     * @return
     */
    public int unmutatedVersion() {
        return this.unmutatedVersion;
    }

    /**
     * <p>根据事件流重建聚合。
     * @param anEventStream
     * @param aStreamVersion
     */
    protected EventSourcedRootEntity(
            List<DomainEvent> anEventStream,
            int aStreamVersion) {

        this();

        // 循环获取每个事件，将聚合恢复到最新版本。
        for (DomainEvent event : anEventStream) {
        	// 重放事件
            this.mutateWhen(event);
        }

        this.setUnmutatedVersion(aStreamVersion);
    }

    protected EventSourcedRootEntity() {
        super();

        // 设置一个空的事件流集合。
        this.setMutatingEvents(new ArrayList<DomainEvent>(2));
    }

    /**
     *<h3>应用某个事件</h3>
     *<p>事件将会被追加到本聚合内部的“事件流"对象的末尾，同时使用“重放事件”的方式处理事件，
     * 构建聚会的新状态。
     * <p>这个事件流可以通过 {@link #mutatingEvents()} 获取。
     * @param aDomainEvent
     */
    protected void apply(DomainEvent aDomainEvent) {

        this.mutatingEvents().add(aDomainEvent);

        this.mutateWhen(aDomainEvent);
    }

    /**
     * <p>自动重放指定的事件，重建当前聚合的状态。
     * @param aDomainEvent
     */
    protected void mutateWhen(DomainEvent aDomainEvent) {

    	// 得到当前类的类型。
        Class<? extends EventSourcedRootEntity> rootType = this.getClass();

        // 得到事件的类型。
        Class<? extends DomainEvent> eventType = aDomainEvent.getClass();

        // 组织出一个Key，这个Key在mutatorMethods（Map<String, Method>缓存）中对应一个Method对象。
        String key = rootType.getName() + ":" + eventType.getName();

        // 从mutatorMethods缓存中得到用于重放eventType事件的方法的Method对象。
        Method mutatorMethod = mutatorMethods.get(key);

        if (mutatorMethod == null) {
        	// 如果mutatorMethods缓存没有这个Method对象，则查找它并缓存。
            mutatorMethod = this.cacheMutatorMethodFor(key, rootType, eventType);
        }

        try {
        	// 使用反射执行这个方法。
            mutatorMethod.invoke(this, aDomainEvent);

        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw new RuntimeException(
                        "Method "
                                + MUTATOR_METHOD_NAME
                                + "("
                                + eventType.getSimpleName()
                                + ") failed. See cause: "
                                + e.getMessage(),
                        e.getCause());
            }

            throw new RuntimeException(
                    "Method "
                            + MUTATOR_METHOD_NAME
                            + "("
                            + eventType.getSimpleName()
                            + ") failed. See cause: "
                            + e.getMessage(),
                    e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "Method "
                            + MUTATOR_METHOD_NAME
                            + "("
                            + eventType.getSimpleName()
                            + ") failed because of illegal access. See cause: "
                            + e.getMessage(),
                    e);
        }
    }

    /**
     * <p>反射得到用于重建聚合状态的方法的 {@link Method} 对象，并将其缓存。
     * <p>如果未找到将抛出 {@link IllegalArgumentException} 异常。
     * @param aKey
     * @param aRootType
     * @param anEventType
     * @return
     */
    private Method cacheMutatorMethodFor(
            String aKey,
            Class<? extends EventSourcedRootEntity> aRootType,
            Class<? extends DomainEvent> anEventType) {

    	// 并发保护
        synchronized (mutatorMethods) {
            try {
            	// 反射得到方法的Method对象。
                Method method = this.hiddenOrPublicMethod(aRootType, anEventType);

                // method可能是私有的，需要对它设置可访问性，才可以正常使用。
                method.setAccessible(true);

                // 将method缓存到mutatorMethods中。
                mutatorMethods.put(aKey, method);

                return method;

            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "I do not understand "
                                + MUTATOR_METHOD_NAME
                                + "("
                                + anEventType.getSimpleName()
                                + ") because: "
                                + e.getClass().getSimpleName() + ">>>" + e.getMessage(),
                        e);
            }
        }
    }

    /**
     * <p>利用反射获取重建聚合状态的方法的 {@link Method} 对象。
     * <p>反射中使用的除了 aRootType 和 anEventType 这两个参数外，还用到了
     * {@link #MUTATOR_METHOD_NAME} 所指定的方法名称。
     **
     * @param aRootType 聚合类的类型。
     * @param anEventType 参数类的类型。
     * @return
     * @throws Exception
     */
    private Method hiddenOrPublicMethod(
            Class<? extends EventSourcedRootEntity> aRootType,
            Class<? extends DomainEvent> anEventType)
    throws Exception {

        Method method = null;

        try {

            // assume protected or private...
        	// 得到aRootType类自身声明的方法的Method对象，无论这个方法是公共的、保护的、私有的，
        	// 方法名称为MUTATOR_METHOD_NAME，参数为anEventType类型。
        	// XXX 这里无法获取父类中的方法。
            method = aRootType.getDeclaredMethod(
                    MUTATOR_METHOD_NAME,
                    anEventType);

        } catch (Exception e) {

            // then public...
        	// FIXME 之前getDeclaredMethod应该是能获得到公共方法的，不明为何多此一步。
            method = aRootType.getMethod(
                    MUTATOR_METHOD_NAME,
                    anEventType);
        }

        return method;
    }

    /**
     * <p>设置事件流集合。
     * <p>注意：这个方法是POJO风格的setter，是私有的，而且没有对应的getter。
     * 聚合、实体是不提倡POJO的。
     * @param aMutatingEventsList
     */
    private void setMutatingEvents(List<DomainEvent> aMutatingEventsList) {
        this.mutatingEvents = aMutatingEventsList;
    }

    /**
     * <p>设置事件流的版本。
     * <p>注意：这个方法是POJO风格的setter，是私有的，而且没有对应的getter。
     * 聚合、实体是不提倡POJO的。
     * @param aStreamVersion
     */
    private void setUnmutatedVersion(int aStreamVersion) {
        this.unmutatedVersion = aStreamVersion;
    }
}
