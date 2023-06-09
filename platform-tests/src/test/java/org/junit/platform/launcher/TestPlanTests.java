/*
 * Copyright 2015-2023 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.launcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

class TestPlanTests {

	private final ConfigurationParameters configParams = mock();

	private EngineDescriptor engineDescriptor = new EngineDescriptor(UniqueId.forEngine("foo"), "Foo");

	@Test
	void doesNotContainTestsForEmptyContainers() {
		engineDescriptor.addChild(
			new AbstractTestDescriptor(engineDescriptor.getUniqueId().append("test", "bar"), "Bar") {
				@Override
				public Type getType() {
					return Type.CONTAINER;
				}
			});

		var testPlan = TestPlan.from(Set.of(engineDescriptor), configParams);

		assertThat(testPlan.containsTests()).as("contains tests").isFalse();
	}

	@Test
	void containsTestsForTests() {
		engineDescriptor.addChild(
			new AbstractTestDescriptor(engineDescriptor.getUniqueId().append("test", "bar"), "Bar") {
				@Override
				public Type getType() {
					return Type.TEST;
				}
			});

		var testPlan = TestPlan.from(Set.of(engineDescriptor), configParams);

		assertThat(testPlan.containsTests()).as("contains tests").isTrue();
	}

	@Test
	void containsTestsForContainersThatMayRegisterTests() {
		engineDescriptor.addChild(
			new AbstractTestDescriptor(engineDescriptor.getUniqueId().append("test", "bar"), "Bar") {
				@Override
				public Type getType() {
					return Type.CONTAINER;
				}

				@Override
				public boolean mayRegisterTests() {
					return true;
				}
			});

		var testPlan = TestPlan.from(Set.of(engineDescriptor), configParams);

		assertThat(testPlan.containsTests()).as("contains tests").isTrue();
	}

}
