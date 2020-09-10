<%@ include file="/init.jsp" %>

<%
    ArrayList<String> orderResults = (ArrayList<String>)request.getAttribute("orderResults");
    QuickOrderConfiguration quickOrderConfiguration = (QuickOrderConfiguration)GetterUtil.getObject(request.getAttribute(QuickOrderConfiguration.class.getName()));
    String checkoutURL = quickOrderConfiguration.quickOrderCheckoutUrl();
%>

<h3><liferay-ui:message key="quick-order-entry-complete"/></h3>
<div class="table-responsive">
    <table class="table table-centered table-nowrap mb-0">
        <thead class="thead-light">
            <tr>
                <th><liferay-ui:message key="part-number"/></th>
                <th><liferay-ui:message key="quantity"/></th>
                <th><liferay-ui:message key="message"/></th>
            </tr>
        </thead>
        <tbody>
<%
    for (String orderResult : orderResults) {

        String [] orderResultDetail = orderResult.split(":");
%>
            <tr>
                <td><%=orderResultDetail[0]%></td>
                <td><%=orderResultDetail[1]%></td>
                <td><%=orderResultDetail[2]%></td>
            </tr>
<%
    }
%>
        </tbody>
    </table>

    <a href="<%= checkoutURL %>" class="btn btn-primary"><liferay-ui:message key="checkout"/></a>

</div>