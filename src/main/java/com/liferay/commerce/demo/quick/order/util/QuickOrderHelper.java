package com.liferay.commerce.demo.quick.order.util;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.account.service.CommerceAccountLocalService;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletRequest;

public class QuickOrderHelper {

    public QuickOrderHelper(PortletRequest request, CommerceChannelLocalService commerceChannelLocalService) {

        _request = request;
        _commerceChannelLocalService = commerceChannelLocalService;
        _themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
        _commerceContext = (CommerceContext)request.getAttribute(CommerceWebKeys.COMMERCE_CONTEXT);

    }

    public CommerceAccount getCommerceAccount(){

        CommerceAccount commerceAccount = null;

        try {
            commerceAccount = _commerceContext.getCommerceAccount();
        } catch (PortalException e) {
            e.printStackTrace();
        }

        return commerceAccount;
    }

    public CommerceChannel getCommerceChannel(){
        CommerceChannel commerceChannel = null;
        try {
            commerceChannel = _commerceChannelLocalService.getCommerceChannel(_commerceContext.getCommerceChannelId());
        } catch (PortalException e) {
            e.printStackTrace();
        }
        return commerceChannel;
    }

    public CommerceContext getCommerceContext(){
        return _commerceContext;
    }

    private static final Log _log = LogFactoryUtil.getLog(
            QuickOrderHelper.class);


    private PortletRequest _request;
    private ThemeDisplay _themeDisplay;
    private CommerceContext _commerceContext;
    private CommerceChannelLocalService _commerceChannelLocalService;

}
