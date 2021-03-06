/*
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context;

import com.google.inject.Provider;
import com.navercorp.pinpoint.bootstrap.context.AsyncContext;
import com.navercorp.pinpoint.bootstrap.context.AsyncState;
import com.navercorp.pinpoint.bootstrap.context.AsyncTraceId;
import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.context.id.AsyncIdGenerator;
import com.navercorp.pinpoint.profiler.context.id.DefaultAsyncTraceId;
import com.navercorp.pinpoint.profiler.context.id.StatefulAsyncTraceId;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import com.navercorp.pinpoint.profiler.context.method.PredefinedMethodDescriptorRegistry;

/**
 * @author Woonduk Kang(emeroad)
 */
public class DefaultAsyncContextFactory implements AsyncContextFactory {

    private final Provider<TraceFactory> traceFactoryProvider;
    private final AsyncIdGenerator asyncIdGenerator;
    private final PredefinedMethodDescriptorRegistry predefinedMethodDescriptorRegistry;
    private final int asyncMethodApiId;

    public DefaultAsyncContextFactory(Provider<TraceFactory> traceFactoryProvider, AsyncIdGenerator asyncIdGenerator, PredefinedMethodDescriptorRegistry predefinedMethodDescriptorRegistry) {
        this.traceFactoryProvider = Assert.requireNonNull(traceFactoryProvider, "traceFactoryProvider must not be null");
        this.asyncIdGenerator = Assert.requireNonNull(asyncIdGenerator, "asyncIdGenerator must not be null");

        this.predefinedMethodDescriptorRegistry = Assert.requireNonNull(predefinedMethodDescriptorRegistry, "predefinedMethodDescriptorRegistry must not be null");

        this.asyncMethodApiId = getAsyncMethodApiId(predefinedMethodDescriptorRegistry);
    }

    private int getAsyncMethodApiId(PredefinedMethodDescriptorRegistry predefinedMethodDescriptorRegistry) {
        final MethodDescriptor asyncMethodDescriptor = predefinedMethodDescriptorRegistry.getAsyncMethodDescriptor();
        return asyncMethodDescriptor.getApiId();
    }

    @Override
    public AsyncContext newAsyncContext(TraceRoot traceRoot) {
        Assert.requireNonNull(traceRoot, "traceRoot must not be null");

        final TraceFactory traceFactory = traceFactoryProvider.get();
        final int asyncId = asyncIdGenerator.nextAsyncId();
        return new DefaultAsyncContext(traceFactory, traceRoot, asyncId, this.asyncMethodApiId);
    }

    @Override
    public AsyncTraceId newAsyncTraceId(TraceRoot traceRoot) {
        Assert.requireNonNull(traceRoot, "traceRoot must not be null");

        final int asyncId = asyncIdGenerator.nextAsyncId();
        return new DefaultAsyncTraceId(traceRoot, asyncId);
    }

    @Override
    public AsyncTraceId newAsyncTraceId(TraceRoot traceRoot, AsyncState asyncState) {
        Assert.requireNonNull(traceRoot, "traceRoot must not be null");
        Assert.requireNonNull(asyncState, "asyncState must not be null");

        final int asyncId = asyncIdGenerator.nextAsyncId();
        return new StatefulAsyncTraceId(traceRoot, asyncId, asyncState);
    }
}
