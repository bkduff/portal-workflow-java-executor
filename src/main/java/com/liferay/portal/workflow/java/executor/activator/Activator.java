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

package com.liferay.portal.workflow.java.executor.activator;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionManager;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;

import java.io.InputStream;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(immediate = true, service = Activator.class)
public class Activator {

	@Activate
	protected void activate() throws Exception {
		List<Company> companies = _companyLocalService.getCompanies();

		for (Company company : companies) {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(company.getCompanyId());

			int kaleoDefinitionsCount =
				_kaleoDefinitionLocalService.getKaleoDefinitionsCount(
					"Single Approver Java", serviceContext);

			if (kaleoDefinitionsCount > 0) {
				return;
			}

			Class<?> clazz = getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			InputStream inputStream = classLoader.getResourceAsStream(
				"single-approver-java-definition.xml");

			if (inputStream == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to find definition file for Single Approver " +
							"Java with file name " +
								"single-approver-java-definition.xml");
				}

				return;
			}

			User defaultUser = _userLocalService.getDefaultUser(
				company.getCompanyId());

			String localizedTitle = LocalizationUtil.updateLocalization(
				StringPool.BLANK, "title", "Single Approver Java",
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()));

			_workflowDefinitionManager.deployWorkflowDefinition(
				serviceContext.getCompanyId(), defaultUser.getUserId(),
				localizedTitle, "Single Approver Java",
				FileUtil.getBytes(inputStream));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(Activator.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}