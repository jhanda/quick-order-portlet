package com.liferay.commerce.demo.quick.order.portlet;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.demo.quick.order.configuration.QuickOrderConfiguration;
import com.liferay.commerce.demo.quick.order.constants.QuickOrderPortletKeys;
import com.liferay.commerce.demo.quick.order.util.QuickOrderHelper;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jeff Handa
 */
@Component(
		configurationPid = "com.liferay.commerce.demo.quick.order.configuration.QuickOrderConfiguration",
		immediate = true,
	property = {
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=QuickOrder",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + QuickOrderPortletKeys.QUICKORDER,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class QuickOrderPortlet extends MVCPortlet {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(
				QuickOrderConfiguration.class, properties);
	}

	@Override
	public void render(RenderRequest request, RenderResponse response) throws IOException, PortletException {

		QuickOrderHelper quickOrderHelper = new QuickOrderHelper(request, _commerceChannelLocalService);
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);

		//Get Channel
		CommerceChannel commerceChannel = quickOrderHelper.getCommerceChannel();

		//Get Account
		CommerceAccount commerceAccount = quickOrderHelper.getCommerceAccount();

		//Get Open Orders
		List<CommerceOrder> commerceOpenOrders =
				getCommerceOpenOrders(commerceAccount.getCommerceAccountId(), commerceChannel.getGroupId());

		request.setAttribute("commerceOpenOrders", commerceOpenOrders);

		super.render(request, response);
	}

	public void submitPartNumbers(ActionRequest request, ActionResponse response) throws PortalException {

		QuickOrderHelper quickOrderHelper = new QuickOrderHelper(request, _commerceChannelLocalService);
		ServiceContext serviceContext = ServiceContextFactory.getInstance(request);
		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
		long companyId = themeDisplay.getCompanyId();
		String catalogUrl = _configuration.quickOrderCatalogUrl();


		//Get Channel
		CommerceChannel commerceChannel =  quickOrderHelper.getCommerceChannel();

		//Get Account
		CommerceAccount commerceAccount = quickOrderHelper.getCommerceAccount();

		//Get Order
		long commerceOrderId = ParamUtil.getLong(request, "orderId");

		if (commerceOrderId == 0){

			CommerceOrder commerceOrder = _commerceOrderLocalService.addCommerceOrder(
					themeDisplay.getUserId(),
					commerceChannel.getGroupId(),
					commerceAccount.getCommerceAccountId(),
					_commerceCurrencyLocalService.getCommerceCurrency(companyId, commerceChannel.getCommerceCurrencyCode()).getCommerceCurrencyId());

			commerceOrderId = commerceOrder.getCommerceOrderId();
			_log.debug("Commerce Order: " + commerceOrderId);
		}

		//Create Commerce Context
		CommerceContext commerceContext = quickOrderHelper.getCommerceContext();

		//Process order
		HashMap<String, Integer> orderMap = getOrderItems(request);
		List<String> orderResults = new ArrayList<String>();
		for (String sku : orderMap.keySet()) {

			int quantity = GetterUtil.getInteger(orderMap.get(sku));
			_log.debug("Adding SKU: " + sku + " -- Quantity:  " + quantity);

			String json = "";

			try {

				long cpInstanceId = _cpInstanceLocalService.getCPInstanceByExternalReferenceCode(companyId, sku).getCPInstanceId();
				_log.debug("CPInstance: " + cpInstanceId);

				_commerceOrderItemLocalService.addCommerceOrderItem(commerceOrderId, cpInstanceId, quantity, 0, json, commerceContext, serviceContext);
				orderResults.add(sku + ":" + quantity + ":" + "Added to order");
			} catch (NoSuchCPInstanceException e){
				orderResults.add(sku + ":" + quantity + ":" + "Part Number not found.  <a target='_blank' href='" + catalogUrl + "?q=" + sku + "'> Search </a>");
			}

		}

		request.setAttribute(QuickOrderConfiguration.class.getName(), _configuration);
		request.setAttribute("orderResults", orderResults);
		response.setRenderParameter("mvcPath", "/confirmation.jsp");

	}

	private List<CommerceOrder> getCommerceOpenOrders(long commerceAccountId, long commerceChannelGroupId){
		List<CommerceOrder> commerceOrders =_commerceOrderLocalService.getCommerceOrders(commerceChannelGroupId, commerceAccountId, -1, -1, null );
		List<CommerceOrder> commerceOpenOrders = new ArrayList<CommerceOrder>();
		for (CommerceOrder commerceOrder:commerceOrders){
			if (commerceOrder.getOrderStatus() == CommerceOrderConstants.ORDER_STATUS_OPEN){
				commerceOpenOrders.add(commerceOrder);
				_log.debug("Adding " + commerceOrder.getCommerceOrderId());
			}

		}
		_log.debug("Number of open orders: " + commerceOpenOrders.size());
		return commerceOpenOrders;
	}

	private HashMap<String, Integer> getOrderItems(ActionRequest request){

		HashMap<String, Integer> orderMap = new HashMap<String, Integer>();
		String partsList = ParamUtil.getString(request, "partsList");
		Pattern pattern = Pattern.compile("^(.*?)[\\t]{1}.*?(.*)$",Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(partsList);

		while (matcher.find()) {
			orderMap.put(matcher.group(1), Integer.valueOf(matcher.group(2)));
			_log.debug("SKU: " + matcher.group(1) + " -- Quantity : " + matcher.group(2));
		}

		return orderMap;
	}

	private volatile QuickOrderConfiguration _configuration;

	private static final Log _log = LogFactoryUtil.getLog(
			QuickOrderPortlet.class);

	@Reference
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Reference
	private CommerceOrderLocalService _commerceOrderLocalService;

	@Reference
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Reference
	private CPInstanceLocalService _cpInstanceLocalService;

	@Reference
	private CommerceChannelLocalService _commerceChannelLocalService;

}