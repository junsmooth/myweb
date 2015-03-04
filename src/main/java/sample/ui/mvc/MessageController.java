/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package sample.ui.mvc;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.validation.Valid;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import sample.ui.Message;
import sample.ui.MessageRepository;

/**
 * @author Rob Winch
 */
@Controller
@RequestMapping("/")
public class MessageController {
	private final MessageRepository messageRepository;
	private static Logger logger = LoggerFactory
			.getLogger(MessageController.class);

	private final static String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	private final static String AGENT = "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0";
	private final static String ZHANGDAIYIXIAN="zhangdaiyixian";
	private final static String ZHANGHUIFENG="zhanghuifeng";
	@Autowired
	public MessageController(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	@RequestMapping
	public ModelAndView list() {
		Iterable<Message> messages = this.messageRepository.findAll();
		return new ModelAndView("messages/list", "messages", messages);
	}

	@RequestMapping("{id}")
	public ModelAndView view(@PathVariable("id") Message message) {
		return new ModelAndView("messages/view", "message", message);
	}

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(@ModelAttribute Message message) {
		return "messages/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@Valid Message message, BindingResult result,
			RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("messages/form", "formErrors",
					result.getAllErrors());
		}
		message = this.messageRepository.save(message);
		redirect.addFlashAttribute("globalMessage",
				"Successfully created a new message");
		logger.info("do click");
		doClick(message);

		System.out.println(message);
		// message.setId(1l);

		return new ModelAndView("redirect:/{message.id}", "message.id",
				message.getId());
	}

	private void doClick(Message message) {

		Date fabiaoTime = message.getBidOpenTime();

		// get bid id
		String bidId = "";

		while (true) {
			if (System.currentTimeMillis() < fabiaoTime.getTime()) {
				try {
					Thread.sleep(1000);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			bidId = getBidId(message);
			if ("".equals(bidId)) {
				continue;
			} else {
				break;
			}
		}
		String bidMima = "";
		logger.info("bidId=" + bidId);
		// bidId="346";
		if (!"".equals(bidId)) {
			bidMima = getBidMima(bidId);
		}
		logger.info("Bid Mima=" + bidMima);
		message.setBidid(Integer.parseInt(bidId));
		message.setMima(bidMima);

		Worker w = new Worker(message,"5000",ZHANGDAIYIXIAN);
		Worker w2 = new Worker(message,"5000",ZHANGDAIYIXIAN);
		Worker w3 = new Worker(message,"5000",ZHANGDAIYIXIAN);
		Worker w4 = new Worker(message,"5000",ZHANGDAIYIXIAN);
		Worker w5 = new Worker(message,"5000",ZHANGDAIYIXIAN);
		Worker w6 = new Worker(message,"1000",ZHANGDAIYIXIAN);
		Worker w7 = new Worker(message,"5000",ZHANGHUIFENG);
		ExecutorService service = Executors.newFixedThreadPool(7);
		logger.info("submit ");
		service.submit(w);
		service.submit(w2);
		service.submit(w3);
		service.submit(w4);
		service.submit(w5);
		service.submit(w6);
		service.submit(w7);
	}

	private String getBidMima(String bidId) {
//		 bidId = "346";
		String url = "http://wujinsuo.cn/index.php?ctl=deal&id=" + bidId
				+ "&act=bid";
		String mima = "";
		try {
			BasicCookieStore cookieStore = new BasicCookieStore();
			CloseableHttpClient httpclient = HttpClients.custom()
					.setDefaultCookieStore(cookieStore).build();
			doLogin(cookieStore, httpclient,ZHANGDAIYIXIAN);

			HttpGet httpget = new HttpGet(url);
			httpget.addHeader("Accept", ACCEPT);
			httpget.addHeader("User-Agent", AGENT);

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}

			};
			String resultString = httpclient.execute(httpget, responseHandler);
			int index = resultString.indexOf("#mima\").val())!=");
			mima = resultString.substring(index + 16, index + 20);
			logger.info(mima);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mima;
	}

	private String getBidId(Message message) {
		try {
			BasicCookieStore cookieStore = new BasicCookieStore();
			CloseableHttpClient httpclient = HttpClients.custom()
					.setDefaultCookieStore(cookieStore).build();
			doLogin(cookieStore, httpclient,ZHANGDAIYIXIAN);

			//

			String bidName = message.getBidName();
			// time
			//
			String mainUrl = "http://www.wujinsuo.cn:80/index.php";
			HttpGet httpget = new HttpGet(mainUrl);
			httpget.addHeader("Accept", ACCEPT);
			httpget.addHeader("User-Agent", AGENT);

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				public String handleResponse(final HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine().getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}

			};
			String resultString = httpclient.execute(httpget, responseHandler);
			// parse html
			Document doc = Jsoup.parse(resultString);
			Elements links = doc.select("a[href]");

			Element aElement = null;
			for (Element e : links) {
				List<Node> childNode = e.childNodes();
				if (childNode.size() != 1)
					continue;
				Node node = childNode.get(0);
				if ("span".equals(node.nodeName())) {
					String html = node.outerHtml();
					logger.info(html);
					if (html.contains(bidName)) {
						// okle
						aElement = e;
					}
				}
			}
			if (aElement == null) {
				// retry
				return "";
			} else {

				String href = aElement.attr("href");
				String bidId = StringUtils.substringAfter(href, "id=");
				logger.info(bidId);
				return bidId;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private class Worker implements Runnable {
		private Message msg;
		private String money = "5000";
		private String userName=ZHANGDAIYIXIAN;
		public Worker(Message message, String money,String userName) {
			this.msg = message;
			this.money = money;
			this.userName=userName;
		}

		@Override
		public void run() {
			logger.info("run worker.");
			int bidid = msg.getBidid();
			String mima = msg.getMima();
			Date d = msg.getBidDate();
			// Post login
			BasicCookieStore cookieStore = new BasicCookieStore();
			CloseableHttpClient httpclient = HttpClients.custom()
					.setDefaultCookieStore(cookieStore).build();

			try {
				doLogin(cookieStore, httpclient,userName);
				// wait entil
				// DateTime dt=new DateTime(2015,3,1,10,10,1);
				while (System.currentTimeMillis() < d.getTime()) {
					// logger.info("sleep 100,bidid:" + bidid + ",mima:" +
					// mima);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				String testUrl = "http://www.wujinsuo.cn/index.php?ctl=uc_center";
				String realUrl = "http://www.wujinsuo.cn:80/index.php?ctl=deal&act=dobid&ajax=1&bid_money="
						+ money
						+ "&mima="
						+ mima
						+ "&user_paypwd=0&id="
						+ bidid;
				logger.info("realUrl:" + realUrl);

				HttpGet httpget = new HttpGet(realUrl);
				httpget.addHeader("Accept", ACCEPT);
				httpget.addHeader("User-Agent", AGENT);

				ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

					public String handleResponse(final HttpResponse response)
							throws ClientProtocolException, IOException {
						int status = response.getStatusLine().getStatusCode();
						if (status >= 200 && status < 300) {
							HttpEntity entity = response.getEntity();
							return entity != null ? EntityUtils
									.toString(entity) : null;
						} else {
							throw new ClientProtocolException(
									"Unexpected response status: " + status);
						}
					}

				};
				String resultString = httpclient.execute(httpget,
						responseHandler);
				logger.info("ResultString:" + resultString.length());

				// get bid
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void doLogin(BasicCookieStore cookieStore,
			CloseableHttpClient httpclient,String userName) throws URISyntaxException,
			IOException, ClientProtocolException {
		HttpUriRequest login = RequestBuilder
				.post()
				.setUri(new URI(
						"http://www.wujinsuo.cn:80/index.php?ctl=user&act=dologin"))
				.addParameter("email", userName)
				.addParameter("user_pwd", "070500").build();
		
		CloseableHttpResponse response2 = httpclient.execute(login);
		try {
			HttpEntity entity = response2.getEntity();
			logger.info("Login form get: " + response2.getStatusLine());
			EntityUtils.consume(entity);
			logger.info("Post logon cookies:");
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				logger.info("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					logger.info("- " + cookies.get(i).toString());
				}
			}
		} finally {
			response2.close();
		}
	}

	@RequestMapping("foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}

}
