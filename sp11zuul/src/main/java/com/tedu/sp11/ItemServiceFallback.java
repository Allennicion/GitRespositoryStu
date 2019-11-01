package com.tedu.sp11;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.tedu.web.util.JsonResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemServiceFallback implements FallbackProvider {

	/**
	 * 路由规则,返回一个service-id,
	 * 当前降级类,只对指定的微服务有效.
	 * return null或"",则对所有服务有效.
	 */
	@Override
	public String getRoute() {
		return "item-service";
	}

	/**
	 * 降级响应,将响应的数据封装成response对象返回给用户.
	 */
	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
		return response();
	}

	private ClientHttpResponse response() {
		return new ClientHttpResponse() {
			
			@Override
			public HttpHeaders getHeaders() {
				/**
				 * 协议头
				 * Content-Type: application/json
				 */
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				return headers;
			}
			
			@Override
			public InputStream getBody() throws IOException {
				/**
				 * 协议体,响应给用户.
				 * 这里想返回一个JsonResult对象,放入流中.
				 */
				String json = JsonResult.err("商品服务执行故障!").toString();
				return new ByteArrayInputStream(json.getBytes());
			}
			
			/**
			 * 下面三个都是协议号
			 */
			@Override
			public String getStatusText() throws IOException {
				return HttpStatus.OK.getReasonPhrase();
			}
			
			@Override
			public HttpStatus getStatusCode() throws IOException {
				return HttpStatus.OK;
			}
			
			@Override
			public int getRawStatusCode() throws IOException {
				return HttpStatus.OK.value();
			}
			
			@Override
			public void close() {
				
			}
		};
	}

}
