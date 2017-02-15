package com.hanbitplate.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {
	
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		
		vertx.createHttpClient().getAbs("http://localhost:8080/shutdown",
				response -> {
					
				})
				.connectionHandler(connection -> {
					connection.exceptionHandler(exception -> {
						System.err.println("server stopped...");
					});
				})
				.exceptionHandler(exception -> {
					vertx.deployVerticle(new MainVerticle());
					System.out.println("server started...");
				}).end();
	}

	@Override
	public void start() throws Exception {
		Router router = Router.router(vertx);
		
		router.route("/shutdown").handler(ctx -> {
			System.exit(0);
		});
		
		router.route("/api/main/section/:sectionCode/items").handler(ctx -> {
			HttpServerRequest request = ctx.request();
			HttpServerResponse response = ctx.response();
			
			String sectionCode = request.getParam("sectionCode");
			
			response.putHeader("content-type", "application/json;charset=UTF-8");
			response.sendFile("json/section" + sectionCode + ".items.json");
		});
		
		router.route("/api/main/section/:sectionCode/categories").handler(ctx -> {
			HttpServerRequest request = ctx.request();
			HttpServerResponse response = ctx.response();
			
			String sectionCode = request.getParam("sectionCode");
			
			response.putHeader("content-type", "application/json;charset=UTF-8");
			response.sendFile("json/section" + sectionCode + ".categories.json");
		});
		
		router.route("/api/main/imgs").handler(ctx -> {
			HttpServerResponse response = ctx.response();
			
			response.putHeader("content-type", "application/json;charset=UTF-8");
			response.sendFile("json/main.imgs.json");
		});
		
		router.route("/api/common/hotplaces").handler(ctx -> {
			HttpServerResponse response = ctx.response();
			
			response.putHeader("content-type", "application/json;charset=UTF-8");
			response.sendFile("json/common.hotplaces.json");
		});
		
		new TopListRouter().defineRoutes(router);
		new SocketRouter().defineRoutes(vertx, router);
		
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
	}
	
}















