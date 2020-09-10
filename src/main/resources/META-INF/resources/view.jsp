<%@ include file="/init.jsp" %>

<%
	List<CommerceOrder> commerceOpenOrders = (List<CommerceOrder>)request.getAttribute("commerceOpenOrders");

%>
<h3><liferay-ui:message key="quick-order"/></h3>
<p>
	<b><liferay-ui:message key="quick-order-instructions"/></b>
</p>

<portlet:actionURL name="submitPartNumbers" var="submitPartNumbersActionURL" />

<aui:form action="<%= submitPartNumbersActionURL %>" data-senna-off="false" method="post" name="fm">

	<aui:fieldset>
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

		<aui:select label="select-order" id="orderId" name="orderId" >
		<aui:option value="0"><liferay-ui:message key="create-new-order"/></aui:option>
			<%
				for (CommerceOrder commerceOrder:commerceOpenOrders){
					long commerceOrderId = commerceOrder.getCommerceOrderId();
			%>
			<aui:option value="<%= commerceOrderId %>"><%= commerceOrderId %></aui:option>
			<%
				}
			%>
		</aui:select>

		<aui:input type="textarea" id=""  label="part-number-quantity" placeholder="part-number-quantity-instruction" name="partsList" value="" />

		<aui:button value="submit" type="submit"></aui:button>
	</aui:fieldset>
</aui:form>