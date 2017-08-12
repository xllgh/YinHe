package com.hb.test.httpservertest;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import android.util.Log;

public class HttpServer {

	private static final String TAG = "HttpServer";

	private Object mLock = new Object();
	private int mPort = 8080;
	private int mMaxClients = 1;
	private HttpHandler[] mHandlers = null;
	private ServerSocket mServerSocket = null;
	private Thread mServerThread = null;
	private ArrayList<DefaultHttpServerConnection> mClientConnects = new ArrayList<DefaultHttpServerConnection>();

	public HttpServer(int port, int maxClients, HttpHandler[] handlers) {
		mPort = port;
		mMaxClients = maxClients;
		mHandlers = Arrays.copyOf(handlers, handlers.length);
	}

	public void start() {
		synchronized (mLock) {
			LogUtils.time("HttpServer start");
			if (mServerThread != null)
				return;

			BasicHttpProcessor httproc = new BasicHttpProcessor();
			httproc.addInterceptor(new ResponseDate());
			httproc.addInterceptor(new ResponseServer());
			httproc.addInterceptor(new ResponseContent());
			httproc.addInterceptor(new ResponseConnControl());

			HttpService httpserv = null;
			httpserv = new HttpService(httproc,
					new DefaultConnectionReuseStrategy(),
					new DefaultHttpResponseFactory());
			HttpRequestHandlerRegistry reg = null;
			reg = new HttpRequestHandlerRegistry();

			for (HttpHandler handle : mHandlers) {
				reg.register(handle.mPattern, handle.mHandler);
			}

			httpserv.setHandlerResolver(reg);

			try {
				mServerSocket = new ServerSocket(mPort);
				mServerThread = new RequestListenerThread(mServerSocket,
						httpserv);
				mServerThread.setDaemon(false);
				mServerThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void Stop() {
		synchronized (mLock) {
			if (mServerThread == null)
				return;

			mServerThread.interrupt();
			try {
				mServerSocket.close();
				mServerThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mServerSocket = null;
			mServerThread = null;
		}
	}

	public static class HttpHandler {
		public String mPattern = "";
		public HttpRequestHandler mHandler = null;

		HttpHandler(String pattern, HttpRequestHandler handler) {
			mPattern = pattern;
			mHandler = handler;
		}
	}

	private class RequestListenerThread extends Thread {

		private final ServerSocket serversocket;
		private final HttpService httpService;

		public RequestListenerThread(final ServerSocket serversocket,
				final HttpService httpService) throws IOException {
			this.serversocket = serversocket;
			this.httpService = httpService;
		}

		@Override
		public void run() {
			Log.i(TAG, "Listening on port " + serversocket.getLocalPort());
			while (!Thread.interrupted()) {
				try {
					// Set up HTTP connection
					LogUtils.time("Listening port "
							+ serversocket.getLocalPort());
					Socket socket = serversocket.accept();
					LogUtils.time("Listening port "
							+ serversocket.getLocalPort() + "success");

					Log.e(TAG,
							"Incoming connection from "
									+ socket.getInetAddress());
					DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
					conn.bind(socket, new BasicHttpParams());
					// Start worker thread
					synchronized (mClientConnects) {
						if (mClientConnects.size() >= mMaxClients) {
							conn.shutdown();
							conn.close();
							Log.i(TAG, "Max Clients reached.");
							Log.e(TAG, "Max Clients reached.");
							continue;
						}
						mClientConnects.add(conn);
					}

					Thread t = new WorkerThread(this.httpService, conn);
					t.setDaemon(true);
					t.start();
				} catch (InterruptedIOException ex) {
					break;
				} catch (IOException e) {
					Log.e(TAG,
							"I/O error initialising connection thread: "
									+ e.getMessage());
					break;
				}
			}

			synchronized (mClientConnects) {
				for (DefaultHttpServerConnection c : mClientConnects) {
					try {
						c.shutdown();
						c.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				mClientConnects.clear();
			}
		}
	}

	private class WorkerThread extends Thread {

		private final HttpService httpservice;
		private final DefaultHttpServerConnection conn;

		public WorkerThread(final HttpService httpservice,
				final DefaultHttpServerConnection conn) {
			super();
			this.httpservice = httpservice;
			this.conn = conn;
		}

		@Override
		public void run() {
			Log.i(TAG, "New connection thread");
			LogUtils.time("New WorkerThread thread");
			HttpContext context = new BasicHttpContext(null);
			try {
				while (!Thread.interrupted() && this.conn.isOpen()) {
					LogUtils.time("httpservice.handleRequest");
					this.httpservice.handleRequest(this.conn, context);
				}
			} catch (ConnectionClosedException ex) {
				Log.e(TAG, "Client closed connection");
			} catch (IOException ex) {
				Log.e(TAG, " httpservice.handleRequest I/O error: " + ex.getMessage());
			} catch (HttpException ex) {
				Log.e(TAG,
						"Unrecoverable HTTP protocol violation: "
								+ ex.getMessage());
			} finally {
				try {
					synchronized (mClientConnects) {
						mClientConnects.remove(this.conn);
					}
					this.conn.shutdown();
					this.conn.close();
				} catch (IOException ignore) {
				}
			}
		}

	}

}
