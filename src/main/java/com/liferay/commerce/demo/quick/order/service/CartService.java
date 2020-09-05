package com.liferay.commerce.demo.quick.order.service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.URL;
import java.net.Authenticator;


public class CartService {

    public CartService() {

    }

    public long[] getCartIds(String url, long channelId){

        String user = "brenda.collins";
        String password = "asdf";

//        HttpAuthenticationFeature feature = HttpAuthenticationFeature
//                        .basicBuilder()
//                        .credentials(user, password)
//                        .build();
//
//        ClientConfig clientConfig = new ClientConfig();
//        clientConfig.register(feature);

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url + "/o/headless-commerce-delivery-cart/v1.0/channels/" + channelId + "/carts");

        String jsonResponse = webTarget.request(MediaType.APPLICATION_JSON_TYPE).get(String.class);

        System.out.println(jsonResponse);

        long cartIds[] = new long[20];




        return cartIds;
    }

    private URL _url;
    private Client _client;
    private WebTarget _webTarget;
}
