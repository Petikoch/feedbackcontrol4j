/**
 * Copyright 2021 Peti Koch
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
package ch.petikoch.libs.feedbackcontrol4j.control;

import ch.petikoch.libs.feedbackcontrol4j.controllable.Controllable;
import ch.petikoch.libs.feedbackcontrol4j.controller.Controller;
import ch.petikoch.libs.feedbackcontrol4j.filter.Filter;
import ch.petikoch.libs.feedbackcontrol4j.util.CustomNameThreadFactory;
import ch.petikoch.libs.feedbackcontrol4j.util.Throwables;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of a feedback control closed loop with time periodic control.
 *
 * @param <S> type of setpoint
 * @param <V> type of controllable value
 */
public class TimeperiodicFeedbackControlLoop<S, V> extends EventBasedFeedbackControlLoop<S, V> implements ITimeperiodicFeedbackControlLoop {

    private final Logger logger = Logger.getLogger(TimeperiodicFeedbackControlLoop.class.getName());

    private final ScheduledExecutorService scheduledExecutorService = createScheduledExecutorService();
    private final AtomicReference<ScheduledFuture<?>> lastFutureRef = new AtomicReference<>();

    public TimeperiodicFeedbackControlLoop(final Controller<S, V> controller,
                                           final Controllable<V> controllable) {
        super(controller, controllable);
    }

    public TimeperiodicFeedbackControlLoop(final Controller<S, V> controller,
                                           final Filter<V> controllerOutputFilter,
                                           final Controllable<V> controllable) {
        super(controller, controllerOutputFilter, controllable);
    }

    public TimeperiodicFeedbackControlLoop(final Controller<S, V> controller,
                                           final List<Filter<V>> controllerOutputFilters,
                                           final Controllable<V> controllable) {
        super(controller, controllerOutputFilters, controllable);
    }

    private ScheduledExecutorService createScheduledExecutorService() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
                1,
                new CustomNameThreadFactory(true, getClass().getSimpleName() + "-scheduled-threadpool")
        );
        executor.setKeepAliveTime(1, TimeUnit.SECONDS);
        return executor;
    }

    @Override
    public final void setInterval(final long millis) {
        if (!isStoppedOrIsStopping()) {
            final ScheduledFuture<?> future = scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        doControl();
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Unexpected: " + ex.getMessage() + ". Will stop.", ex);
                        stop(); // to indicate the issue
                    }
                }
            }, 0, millis, TimeUnit.MILLISECONDS);
            final ScheduledFuture<?> lastFuture = lastFutureRef.getAndSet(future);
            if (lastFuture != null) {
                lastFuture.cancel(false);
            }
        } else {
            // ignore
        }
    }

    @Override
    public void stop() {
        if (!(isStoppedOrIsStopping())) {
            scheduledExecutorService.shutdown();
            try {
                scheduledExecutorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS); // blocks "forever" until really stopped
            } catch (InterruptedException e) {
                Throwables.propagate(e);
            }
        }
    }

    private boolean isStoppedOrIsStopping() {
        return scheduledExecutorService.isShutdown() || scheduledExecutorService.isTerminated();
    }
}
