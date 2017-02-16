package com.hanbitplate.vertx;

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class SocketRouter {

	public void defineRoutes(Vertx vertx, Router router) {
		SockJSHandler ebHandler = SockJSHandler.create(vertx);
		
		BridgeOptions bridgeOptions = new BridgeOptions()
				.addInboundPermitted(new PermittedOptions().setAddress("hanbit.server"))
				.addOutboundPermitted(new PermittedOptions().setAddress("hanbit.client"));
		
		ebHandler.bridge(bridgeOptions, event -> {
			if (event.type() == BridgeEventType.SEND) {
				String remoteAddress = event.socket().headers().get("X-Real-IP");
				remoteAddress = StringUtils.substringBefore(remoteAddress, ":");
				
				event.getRawMessage().getJsonObject("body").put("ip", remoteAddress);
			}
			
			event.complete(true);
		});

		router.route("/ws/*").handler(ebHandler);
		
		EventBus eventBus = vertx.eventBus();
		
		eventBus.consumer("hanbit.server").handler(message -> {
			eventBus.publish("hanbit.client", message.body());
		});
	}
	
}
