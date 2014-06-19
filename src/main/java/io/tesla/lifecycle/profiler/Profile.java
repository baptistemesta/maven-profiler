/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler;

import org.codehaus.plexus.component.annotations.Requirement;

public class Profile {

    protected long elapsedTime;

    @Requirement
    protected Timer timer;

    protected Profile(final Timer timer) {
        this.timer = timer;
    }

    public void stop() {
        timer.stop();
    }

    void setElapsedTime(final long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public long getElapsedTime() {
        if(elapsedTime != 0) {
            return elapsedTime;
        }
        return timer.getTime();
    }
}
