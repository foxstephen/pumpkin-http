package com.stephenfox.pumpkin.http;

class Constants {
  private Constants() {}

  // Http header keys
  static final String CONTENT_LENGTH = "Content-Length";
  static final String CONNECTION = "Connection";
  static final String CONTENT_TYPE = "Content-Type";

  // Http header values
  static final String TEXT_PLAIN = "text/plain";
  static final String TEXT_HTML = "text/html";
  static final String TEXT_JS = "text/javascript";
  static final String TEXT_CSS = "text/css";
  static final String IMAGE_X_ICON = "image/x-icon";
  static final String IMAGE_PNG = "image/png";
  static final String IMAGE_JPEG = "image/jpeg";

  static final String CLOSE = "close";

  // Http messages
  static final String NOT_FOUND = "Not found";
  static final String OK = "Ok";
  static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  static final String NO_REASON = "No Reason";

  static final String PUMPKIN =
      "//             ooo\n"
          + "//                         $ o$\n"
          + "//                        o $$\n"
          + "//              \"\"$$$    o\" $$ oo \"\n"
          + "//          \" o$\"$oo$$$\"o$$o$$\"$$$$$ o\n"
          + "//         $\" \"o$$$$$$o$$$$$$$$$$$$$$o     o\n"
          + "//      o$\"    \"$$$$$$$$$$$$$$$$$$$$$$o\" \"oo  o\n"
          + "//     \" \"     o  \"$$$o   o$$$$$$$$$$$oo$$\n"
          + "//    \" $     \" \"o$$$$$ $$$$$$$$$$$\"$$$$$$$o\n"
          + "//  o  $       o o$$$$$\"$$$$$$$$$$$o$$\"\"\"$$$$o \" \"\n"
          + "// o          o$$$$$\"    \"$$$$$$$$$$ \"\" oo $$   o $\n"
          + "// $  $       $$$$$  $$$oo \"$$$$$$$$o o $$$o$$oo o o\n"
          + "// o        o $$$$$oo$$$$$$o$$$$ \"\"$$oo$$$$$$$$\"  \" \"o\n"
          + "// \"   o    $ \"\"$$$$$$$$$$$$$$  o  \"$$$$$$$$$$$$   o \"\n"
          + "// \"   $      \"$$$$$$$$$$$$$$   \"   $$$\"$$$$$$$$o  o\n"
          + "// $   o      o$\"\"\"\"\"$$$$$$$$    oooo$$ $$$$$$$$\"  \"\n"
          + "// $      o\"\"o $$o    $$$$$$$$$$$$$$$$$ \"\"  o$$$   $ o\n"
          + "// o     \" \"o \"$$$$  $$$$$\"\"\"\"\"\"\"\"\"\"\" $  o$$$$$\"\" o o\n"
          + "// \"  \" o  o$o\" $$$$o   \"\"           o  o$$$$$\"   o\n"
          + "//  $         o$$$$$$$oo            \"oo$$$$$$$\"    o\n"
          + "//  \"$   o o$o $o o$$$$$\"$$$$oooo$$$$$$$$$$$$$$\"o$o\n"
          + "//    \"o oo  $o$\"oo$$$$$o$$$$$$$$$$$$\"$$$$$$$$\"o$\"\n"
          + "//     \"$ooo $$o$   $$$$$$$$$$$$$$$$ $$$$$$$$o\"\n"
          + "//        \"\" $$$$$$$$$$$$$$$$$$$$$$\" \"\"\"\"\n"
          + "//                         \"\"\"\"\"\"";
}
