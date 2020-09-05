package com.liferay.commerce.demo.quick.order.util;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.account.service.CommerceAccountLocalService;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

public class QuickOrderHelper {

    public QuickOrderHelper(PortletRequest request) {

        _request = request;
        _themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);

    }



    private static final Log _log = LogFactoryUtil.getLog(
            QuickOrderHelper.class);

    private static final String _CURRENT_COMMERCE_ACCOUNT_ID_KEY =
            "LIFERAY_SHARED_CURRENT_COMMERCE_ACCOUNT_ID_";

    @Reference
    private CommerceAccountLocalService _commerceAccountLocalService;

    @Reference
    private CommerceChannelLocalService _commerceChannelLocalService;

    @Reference
    private Portal _portal;

    private PortletRequest _request;
    private ThemeDisplay _themeDisplay;


}
