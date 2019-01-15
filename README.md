# pumpkin-http

A micro-http framework for Java. Developed mostly for my own web needs - using zero external/ third party dependencies (well, apart from logger). I mostly add features on an as need basis, I've developed up to a point to where I can use this for my own personal use   (I'm currently using it to run my own website [foxstephen.net](foxstephen.net)) and further development will most likely be based on this.

                 ooo
                             $ o$
                            o $$
                  ""$$$    o" $$ oo "
              " o$"$oo$$$"o$$o$$"$$$$$ o
             $" "o$$$$$$o$$$$$$$$$$$$$$o     o
          o$"    "$$$$$$$$$$$$$$$$$$$$$$o" "oo  o
         " "     o  "$$$o   o$$$$$$$$$$$oo$$
        " $     " "o$$$$$ $$$$$$$$$$$"$$$$$$$o
      o  $       o o$$$$$"$$$$$$$$$$$o$$"""$$$$o " "
     o          o$$$$$"    "$$$$$$$$$$ "" oo $$   o $
     $  $       $$$$$  $$$oo "$$$$$$$$o o $$$o$$oo o o
     o        o $$$$$oo$$$$$$o$$$$ ""$$oo$$$$$$$$"  " "o
     "   o    $ ""$$$$$$$$$$$$$$  o  "$$$$$$$$$$$$   o "
     "   $      "$$$$$$$$$$$$$$   "   $$$"$$$$$$$$o  o
     $   o      o$"""""$$$$$$$$    oooo$$ $$$$$$$$"  "
     $      o""o $$o    $$$$$$$$$$$$$$$$$ ""  o$$$   $ o
     o     " "o "$$$$  $$$$$""""""""""" $  o$$$$$"" o o
     "  " o  o$o" $$$$o   ""           o  o$$$$$"   o
      $         o$$$$$$$oo            "oo$$$$$$$"    o
      "$   o o$o $o o$$$$$"$$$$oooo$$$$$$$$$$$$$$"o$o
        "o oo  $o$"oo$$$$$o$$$$$$$$$$$$"$$$$$$$$"o$"
         "$ooo $$o$   $$$$$$$$$$$$$$$$ $$$$$$$$o"
            "" $$$$$$$$$$$$$$$$$$$$$$" """"
                             """"""


# How to use

Create a new instance of the Pumpkin server passing the host, port and some class which will handle the requests.

```java
Pumpkin
    .httpServer("127.0.0.1", 8080, new BlogService())
    .start();
```   
        

# Defining resources / endpoints
Resources can be defined using one of the HTTP method annotations along with the path of the resource as defined by the resource parameter. The resource parameter supports standard paths e.g. `/api/blogs` and curly `{}` as delimeters for paths that need parameters e.g. `/api/blogs/{slug}` Each method annotated with a HTTP method should accept an instance of `HttpRequest` as this contains the request sent by some client, it is also needed for responding to created responses via the `HttpResponse` api. 

## GET

```java
@Get(resource = "/api/blogs")
public void getArticles(HttpRequest request) {
    List<Blog> blogs = repository.query(GET_BLOGS);
    String result = MAPPER.writeValueAsString(blogs);
    HttpResponse.forRequest(request).setBody(result).send();
}

// Parameters defined using curly braces 
@Get(resource = "/api/blogs/{slug}")
public void getArticles(HttpRequest request) {
    //  Use request.getPathParam for path paremeters
    String slug = request.getPathParam("slug");
    Blog blog = repository.query(GET_BLOG_WITH_SLUG);
    String result = MAPPER.writeValueAsString(blog);
    HttpResponse.forRequest(request).setBody(result).send();
}
```


## POST

```java
@Post(resource = "/api/blogs")
public void getArticles(HttpRequest request) {
    String body = request.getBody();
    Blog blog = MAPPER.readValue(body, Blog.class);
    List<Blog> blogs = repository.add(blog);
    HttpResponse.forRequest(request).setBody(result).send();
}
```

The rest of the HTTP methods follow the same pattern

```java
// PATCH
@Patch(resource = "/api/blogs/{id}")

// DELETE
@Delete(resource = "/api/blogs/{id}")

// PUT
@Put(resource = "/api/blogs/{id}")

... etc
```

## Static resources

Pumpkin can also serve static resource from the filesystem or resources bundled in a jar file. The static handlers are defined when the Pumpkin server is created. This can be done by creating a `new StaticHandler` class passing in the HTTP resource as the first param (full regex support), the second param defines where the file is located (jar or filesystem), the third param defines where the file can be found whether in a .jar file or on the filsystem. 

Examples:

```java
// For any requests matching /index.hmtl or /favicon.ico find them at resources/ in the .jar file.
Pumpkin.httpServer("127.0.0.1", 8080, new BlogService())
    .addHandler(new StaticHandler("/index.html|/favicon.ico", ""))
    .start();
```

```java
// For any requests matching /static/.* find the at resources/static/ in the .jar file.
Pumpkin.httpServer("127.0.0.1", 8080, new BlogService())
    .addHandler(new StaticHandler("/static/.*", "static/"))
    .start();
```

```java
// For any requests matching /images/.* find the at /etc/images on the filesystem.
Pumpkin.httpServer("127.0.0.1", 8080, new BlogService())
    .addHandler(new StaticHandler("/images/.*", "/etc/images/", true)) 
    .start();
```


