/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.java.executor.action;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowStatusManagerUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.action.executor.ActionExecutor;
import com.liferay.portal.workflow.kaleo.runtime.action.executor.ActionExecutorException;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Inácio Nery
 */
@Component(
	immediate = true,
	property = "com.liferay.portal.workflow.kaleo.runtime.action.executor.language=java",
	service = ActionExecutor.class
)
public class JavaActionExecutor implements ActionExecutor {

	@Override
	public void execute(
			KaleoAction kaleoAction, ExecutionContext executionContext)
		throws ActionExecutorException {

		try {
			Map<String, Serializable> workflowContext =
				executionContext.getWorkflowContext();

			if (Objects.equals(
					workflowContext.get("transitionName"), "reject")) {

				WorkflowStatusManagerUtil.updateStatus(
					WorkflowConstants.STATUS_DENIED, workflowContext);
				WorkflowStatusManagerUtil.updateStatus(
					WorkflowConstants.STATUS_PENDING, workflowContext);
			}
			else if (Objects.equals(
						workflowContext.get("transitionName"), "approve")) {

				WorkflowStatusManagerUtil.updateStatus(
					WorkflowConstants.STATUS_APPROVED, workflowContext);
			}
		}
		catch (WorkflowException we) {
			_log.error(we, we);

			throw new ActionExecutorException(we);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JavaActionExecutor.class);

}