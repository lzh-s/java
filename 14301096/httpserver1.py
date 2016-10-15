package work3.pyrmont.first;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import work3.pyrmont.Request;
import work3.pyrmont.Response;
import work3.pyrmont.StaticResourceProcessor;

public class HttpServer1 {

    // 关闭服务命令
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    public static void main(String[] args) {
        HttpServer1 server = new HttpServer1();
        //等待连接请求
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            //服务器套接字对象
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // 循环等待请求
        while (true) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                //等待连接，连接成功后，返回一个Socket对象
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

                // 创建Request对象并解析
                Request request = new Request(input);
                request.parse();
                // 检查是否是关闭服务命令
                if (request.getUri().equals(SHUTDOWN_COMMAND)) {
                    break;
                }
                
                // 创建 Response 对象
                Response response = new Response(output);
                response.setRequest(request);

                if (request.getUri().startsWith("/servlet/")) {
                    //请求uri以/servlet/开头，表示servlet请求
                    ServletProcessor1 processor = new ServletProcessor1();
                    processor.process(request, response);
                } else {
                    //静态资源请求
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }

                // 关闭 socket
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
