package com.stephenfox.pumpkin.http;

//             ooo
//                         $ o$
//                        o $$
//              ""$$$    o" $$ oo "
//          " o$"$oo$$$"o$$o$$"$$$$$ o
//         $" "o$$$$$$o$$$$$$$$$$$$$$o     o
//      o$"    "$$$$$$$$$$$$$$$$$$$$$$o" "oo  o
//     " "     o  "$$$o   o$$$$$$$$$$$oo$$
//    " $     " "o$$$$$ $$$$$$$$$$$"$$$$$$$o
//  o  $       o o$$$$$"$$$$$$$$$$$o$$"""$$$$o " "
// o          o$$$$$"    "$$$$$$$$$$ "" oo $$   o $
// $  $       $$$$$  $$$oo "$$$$$$$$o o $$$o$$oo o o
// o        o $$$$$oo$$$$$$o$$$$ ""$$oo$$$$$$$$"  " "o
// "   o    $ ""$$$$$$$$$$$$$$  o  "$$$$$$$$$$$$   o "
// "   $      "$$$$$$$$$$$$$$   "   $$$"$$$$$$$$o  o
// $   o      o$"""""$$$$$$$$    oooo$$ $$$$$$$$"  "
// $      o""o $$o    $$$$$$$$$$$$$$$$$ ""  o$$$   $ o
// o     " "o "$$$$  $$$$$""""""""""" $  o$$$$$"" o o
// "  " o  o$o" $$$$o   ""           o  o$$$$$"   o
//  $         o$$$$$$$oo            "oo$$$$$$$"    o
//  "$   o o$o $o o$$$$$"$$$$oooo$$$$$$$$$$$$$$"o$o
//    "o oo  $o$"oo$$$$$o$$$$$$$$$$$$"$$$$$$$$"o$"
//     "$ooo $$o$   $$$$$$$$$$$$$$$$ $$$$$$$$o"
//        "" $$$$$$$$$$$$$$$$$$$$$$" """"
//                         """"""

/**
 * Use this class to create a http server.
 *
 * @author Stephen Fox.
 */
public class Pumpkin {
  private Pumpkin() {}

  /**
   * Create a new http server.
   *
   * @param host The host to run the server on.
   * @param port The port to listen to connection on.
   * @param handlerClass The class which contains the handlers for all endpoints.
   */
  public static HttpServer httpServer(String host, int port, Class<?> handlerClass) {
    return new PumpkinHttpServer(host, port, handlerClass);
  }

  /**
   * Create a new http server.
   *
   * @param host The host to run the server on.
   * @param port The port to listen to connections on.
   * @param handlerObject The object which contains the handlers for all endpoints.
   */
  public static HttpServer httpServer(String host, int port, Object handlerObject) {
    return new PumpkinHttpServer(host, port, handlerObject);
  }
}
