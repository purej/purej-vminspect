// Copyright (c), 2013, adopus consulting GmbH Switzerland, all rights reserved.
package com.purej.vminspect.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.purej.vminspect.http.HttpRequest;
import com.purej.vminspect.http.HttpResponse;
import com.purej.vminspect.http.RequestController;
import com.purej.vminspect.util.Utils;

/**
 * Each instance of this class executes a single HTTP request and writes
 * the response to the socket's output-stream. The given socket will be closed
 * after processing the request.
 * <p/>
 * This class implements the {@link Runnable} interface and allows to be executed
 * asynchronously using an ExecutorService.
 *
 * @author Stefan Mueller
 */
final class RequestExecutor implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(VmInspectionServer.class);
  private final Socket socket;
  private final RequestController controller;

  /**
   * Creates a new instance of this class that executes the sockets request.
   */
  RequestExecutor(Socket socket, RequestController controller) {
    super();
    this.socket = socket;
    this.controller = controller;
  }

  /**
   * Executes the request from the specified client-socket and closes streams and the socket.
   */
  @Override
  public void run() {
    try {
      try {
        var in = socket.getInputStream();
        var out = socket.getOutputStream();
        try {
          // Create the request, process it and write the response:
          HttpRequest request = parseRequest(in);
          if (request == null) {
            LOGGER.debug("Non valid HTTP request from {}, will be ignored", socket.getRemoteSocketAddress());
          }
          else {
            LOGGER.debug("HTTP request from {} with parameters {}", socket.getRemoteSocketAddress(), request.getParameters());
            try {
              var httpResponse = controller.process(request);
              writeResponse(httpResponse, out);
            }
            catch (SocketException e) {
              // This might occur if the browser terminates open requests, just log...
              LOGGER.debug("Socket closed by remote host: {}", e.getMessage());
            }
            catch (Exception e) {
              LOGGER.debug("An error occurred processing request!", e);
              writeErrorResponse(e, "500 Server Error", out);
            }
          }
        }
        finally {
          out.close();
          in.close();
        }
      }
      finally {
        socket.close();
      }
    }
    catch (Exception e) {
      LOGGER.error("An error occurred handling HTTP request!", e);
    }
  }

  private static HttpRequest parseRequest(InputStream input) throws Exception {
    var in = new BufferedReader(new InputStreamReader(input, "UTF-8"));
    var header = readHttpHeader(in);
    if (header.size() > 0) {
      var firstLine = header.get(0);
      if (firstLine.startsWith("GET ")) {
        // HTTP GET request, parse it and search for optional cookies:
        var request = new HttpRequest();
        parseGet(firstLine, request);
        var cookies = getLine(header, "Cookie: ");
        if (cookies != null) {
          parseCookies(cookies, request);
        }
        return request;
      }
      else if (firstLine.startsWith("POST ")) {
        var length = getLine(header, "Content-Length: ");
        if (length != null) {
          int contentLength = Integer.parseInt(length.substring("Content-Length: ".length()));
          char[] data = new char[contentLength];
          int read = in.read(data);
          if (read == contentLength) {
            HttpRequest request = new HttpRequest();
            parsePost(new String(data), request);
            return request;
          }
        }
      }
    }
    // Invalid / incomplete request:
    return null;
  }

  private static List<String> readHttpHeader(BufferedReader in) throws IOException {
    var result = new ArrayList<String>();
    while (true) {
      var line = in.readLine();
      line = line != null ? line.trim() : line;
      if (line == null || line.length() == 0) {
        break; // End of header or no more data...
      }
      result.add(line);
    }
    return result;
  }

  private static String getLine(List<String> lines, String prefix) {
    for (var line : lines) {
      if (line.startsWith(prefix)) {
        return line;
      }
    }
    return null;
  }

  private static void parseGet(String line, HttpRequest request) throws Exception {
    // Format is: GET[space]Request-URI[space]HTTP-Version
    int idx = line.indexOf('?');
    if (idx > 0) {
      var params = line.substring(idx + 1, line.lastIndexOf(' ')).split("&");
      splitKeyValues(params, request.getParameters());
    }
  }

  private static void parsePost(String line, HttpRequest request) throws Exception {
    // Format is: list of parameters=values (all encoded)
    var params = line.split("&");
    splitKeyValues(params, request.getParameters());
  }

  private static void parseCookies(String line, HttpRequest request) throws Exception {
    // Format is: Cookies:[space]name=value;[space]name=value;[space]...
    var cookies = line.substring("Cookie: ".length()).split("; ");
    splitKeyValues(cookies, request.getCookies());
  }

  private static void splitKeyValues(String[] keyValues, Map<String, String> map) {
    for (var keyValue : keyValues) {
      int idx = keyValue.indexOf('=');
      if (idx > 0) {
        var key = keyValue.substring(0, idx);
        var value = keyValue.substring(idx + 1);
        int length = value.length();
        value = length > 2 && value.charAt(0) == '"' && value.charAt(length - 1) == '"' ? value.substring(1, length - 1) : value;
        map.put(key, Utils.urlDecode(value));
      }
    }
  }

  private static void writeResponse(HttpResponse response, OutputStream out) throws IOException {
    // Sanity check first:
    var data = response.getContentBytes();
    if (data == null || data.length == 0) {
      writeErrorResponse(null, "", out);
      return;
    }

    // Write in correct order: a) Status
    var builder = new StringBuilder(512);
    appendResponseStatus("200 OK", builder);

    // b) Cookies:
    for (var entry : response.getCookies().entrySet()) {
      builder.append("\r\nSet-Cookie: ").append(entry.getKey()).append("=").append(Utils.urlEncode(entry.getValue()));
      builder.append("; Max-Age=").append(30 * 24 * 60 * 60); // 30 days
    }

    // c) Caching:
    if (response.getCacheSeconds() > 0) {
      builder.append("\r\nCache-Control: max-age=" + response.getCacheSeconds());
    }
    else {
      builder.append("\r\nCache-Control: no-cache");
    }

    // d) Content type and length:
    builder.append("\r\nContent-Type: " + response.getContentType());
    builder.append("\r\nContent-Length: " + data.length);

    // e) Finalize to output-stream - header and binary content:
    builder.append("\r\n\r\n");
    out.write(builder.toString().getBytes("UTF-8"));
    out.write(data);
  }

  private static void writeErrorResponse(Exception e, String errorPart, OutputStream out) throws IOException {
    var builder = new StringBuilder(100);
    appendResponseStatus(errorPart, builder);
    var details = Utils.getHtmlExceptionInfo(e);
    builder.append("\r\nCache-Control: no-cache");
    builder.append("\r\nContent-Type: text/html; charset=utf-8");
    builder.append("\r\n\r\n<html><head><title>Error ").append(errorPart).append("</title>");
    builder.append("<body><h2>").append(errorPart).append("</h2>");
    builder.append("<p><pre>").append(details).append("</pre></p><hr/></body>");
    out.write(builder.toString().getBytes("UTF-8"));
  }

  private static void appendResponseStatus(String statusPart, StringBuilder builder) {
    builder.append("HTTP/1.0 ").append(statusPart);
    builder.append("\r\nServer: VmInspectionServer (simple Java HTTP server)\r\nAllow: GET\r\nConnection: close");
  }
}
