/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.tesla.lifecycle.profiler.internal;

import io.tesla.lifecycle.profiler.MojoProfile;
import io.tesla.lifecycle.profiler.PhaseProfile;
import io.tesla.lifecycle.profiler.ProjectProfile;
import io.tesla.lifecycle.profiler.SessionProfile;
import io.tesla.lifecycle.profiler.SessionProfileRenderer;
import io.tesla.lifecycle.profiler.Timer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.component.annotations.Component;

@Named
@Singleton
@Component(role = SessionProfileRenderer.class)
public class SummarySessionProfileRenderer implements SessionProfileRenderer {

    private final Timer timer;

    @Inject
    public SummarySessionProfileRenderer(final DefaultTimer timer) {
        this.timer = timer;
    }

    @Override
    public void render(final SessionProfile sessionProfile) {

        Map<String, Long> phases = new HashMap<String, Long>();
        Map<String, Long> projects = new HashMap<String, Long>();
        Map<String, Long> mojos = new HashMap<String, Long>();

        for(ProjectProfile pp : sessionProfile.getProjectProfiles()) {
            putOrAdd(projects, pp.getProjectName(), pp.getElapsedTime());
            for(PhaseProfile phaseProfile : pp.getPhaseProfile()) {
                putOrAdd(phases, phaseProfile.getPhase(), phaseProfile.getElapsedTime());
                for (MojoProfile mojo : phaseProfile.getMojoProfiles()) {
                    putOrAdd(mojos, mojo.getId(), mojo.getElapsedTime());
                }
            }
        }

        TreeMap<String, Long> sortedPhases = new TreeMap<String, Long>(new ValueComparator(phases));
        TreeMap<String, Long> sortedProjects = new TreeMap<String, Long>(new ValueComparator(projects));
        TreeMap<String, Long> sortedMojos = new TreeMap<String, Long>(new ValueComparator(mojos));
        sortedPhases.putAll(phases);
        sortedProjects.putAll(projects);
        sortedMojos.putAll(mojos);
        render("Phases ordered by time:");
        for (Entry<String, Long> phase : sortedPhases.entrySet()) {
            render(" " + phase.getKey() + " " + timer.format(phase.getValue()));
        }
        render("Projects ordered by time:");
        for (Entry<String, Long> phase : sortedProjects.entrySet()) {
            render(" " + phase.getKey() + " " + timer.format(phase.getValue()));
        }
        render("Mojos ordered by time:");
        for (Entry<String, Long> phase : sortedMojos.entrySet()) {
            render(" " + phase.getKey() + " " + timer.format(phase.getValue()));
        }
    }

    class ValueComparator implements Comparator<String> {

        Map<String, Long> base;

        public ValueComparator(final Map<String, Long> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        @Override
        public int compare(final String a, final String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
    /**
     * @param map
     * @param phase
     * @param elapsedTime
     */
    private void putOrAdd(final Map<String, Long> map, final String phase, final long elapsedTime) {
        if (!map.containsKey(phase)) {
            map.put(phase, 0l);
        }
        map.put(phase, map.get(phase) + elapsedTime);
    }

    private void render(final String s) {
        System.out.println(s);
    }
}
