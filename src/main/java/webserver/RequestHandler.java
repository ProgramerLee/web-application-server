package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line = br.readLine();

            if(line == null){return;}
            int lineNum = 1;

            if(line != null){
                while(!"".equals(line)){
                    if(lineNum ==1){ //첫번째 줄일 때
                        String[] requsetInfos =line.split(" "); //url을 얻기 위해서
                        Path path = Paths.get("./webapp"+requsetInfos[1]);  //해당 파일의 경로를 얻는다.

                        byte[] body = Files.readAllBytes(path); //파일을 바이트로 읽는다.
                        DataOutputStream dos = new DataOutputStream(out);
                        //byte[] body = "Hello World".getBytes();
                        response200Header(dos, body.length);
                        responseBody(dos, body);
                    }
                    line = br.readLine();
                    lineNum++;
                }
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
