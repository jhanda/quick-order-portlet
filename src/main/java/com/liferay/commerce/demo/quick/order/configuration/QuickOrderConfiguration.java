package com.liferay.commerce.demo.quick.order.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

@ExtendedObjectClassDefinition(
        category = "quick-order", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
        id = "com.liferay.commerce.demo.quick.order.configuration.QuickOrderConfiguration",
        localization = "content/Language", name = "quick-order-configuration-name"
)
public interface QuickOrderConfiguration {

    @Meta.AD(
            deflt = "/group/minium/checkout", description = "quick-order-checkout-url",
            name = "quick-order-checkout-url-name", required = false
    )
    public String quickOrderCheckoutUrl();

    @Meta.AD(
            deflt = "/group/minium/catalog", description = "quick-order-catalog-url",
            name = "quick-order-catalog-url-name", required = false
    )
    public String quickOrderCatalogUrl();
}
