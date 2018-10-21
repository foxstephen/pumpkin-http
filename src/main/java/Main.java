import com.stephenfox.pumpkin.Pumpkin;
import com.stephenfox.pumpkin.http.HttpRequest;
import com.stephenfox.pumpkin.http.HttpResponse;
import com.stephenfox.pumpkin.http.handler.Get;
import com.stephenfox.pumpkin.http.handler.Post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    Pumpkin.httpServer("127.0.0.1", 8080, Main.class).start();
  }

  @Get(resource = "/")
  public void handleGet(HttpRequest request) {
    LOGGER.debug("Handler received request {} ", request);
    HttpResponse.forRequest(request).setBody("Hello World!").setCode(200).send();
  }

  @Post(resource = "/")
  public void handlePost(HttpRequest request) {
    LOGGER.debug("Handler received request {} ", request);
    HttpResponse.forRequest(request).setBody(request.getBody()).setCode(200).send();
  }
}
