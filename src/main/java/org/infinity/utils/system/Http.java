package org.infinity.utils.system;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;

import com.google.gson.Gson;

public class Http {

	private static final HttpClient CLIENT = HttpClient.newHttpClient();
	private static final Gson GSON = new Gson();

	private enum Method {
		GET, POST
	}

	public static class Request {
		private HttpRequest.Builder builder;
		private Method method;

		public Request(Method method, String url) {
			try {
				this.builder = HttpRequest.newBuilder().uri(new URI(url)).header("User-Agent", "Meteor Client");
				this.method = method;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		private <T> T _send(String accept, HttpResponse.BodyHandler<T> responseBodyHandler) {
			builder.header("Accept", accept);
			if (method != null)
				builder.method(method.name(), HttpRequest.BodyPublishers.noBody());

			try {
				var res = CLIENT.send(builder.build(), responseBodyHandler);
				return res.statusCode() == 200 ? res.body() : null;
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		}

		public Stream<String> sendLines() {
			return _send("*/*", HttpResponse.BodyHandlers.ofLines());
		}
	}

	public static Request get(String url) {
		return new Request(Method.GET, url);
	}

	public static Request post(String url) {
		return new Request(Method.POST, url);
	}
}
