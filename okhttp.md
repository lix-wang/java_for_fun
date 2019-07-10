## OkHttp notes.

* [1.Calls](#1)
* [2.Connections](#2)
* [3.Recipes](#3)
* [4.Interceptors](#4)
* [5.HTTPS](#5)
* [6.EventListener](#6)
* [7.Summary](#7)

<h2 id="1">1.Calls</h2>
&emsp;&emsp; okhttp的Calls会以以下两种方式执行：Synchronous： 请求线程会一直阻塞，直到获取到Response。
Asynchronous：在任意线程进行请求，不阻塞，在执行完后，在另一线程call back。Calls可以被取消，如果Calls没有执行完，就取消，那么该Call会失败。
当Call被取消后，会抛出IOException。对于同步Calls，需要自己管理同步Calls的数量，太多会浪费资源，太少会影响延迟。
对于异步Calls，可以设置单个WebServer最大数量（默认5），总量（默认64）。

<h2 id="2">2.Connections</h2>
&emsp;&emsp; OkHttp通过三种类型来连接webServer：URL，Address，Route。
当你在OkHttp中使用URL来进行请求时，将会执行以下步骤：
1. 使用这个URL和OkHttpClient创建一个address。这个address会指明如何连接到webserver。
2. 利用这个address，尝试从connection pool中获取一个连接。
3. 如果没有获取到连接，会使用一个route来进行尝试。这通常意味着请求一次DNS来获取server的IP地址。
4. 如果这是个新的route，将会创建一个direct socket连接或者一个TLS tunnel或者一个direct TLS 连接。此时会进行TLS handshakes。
5. 进行发送请求获取返回。
<br>
&emsp;&emsp; 如果连接时存在问题，那么会选择另一个route再次尝试。一旦连接成功，那么该连接会被放入到连接池中，这样下次就能复用，
一段时间不用后，连接会过期。

<h2 id="3">3.Recipes</h2>
&emsp;&emsp; Synchronous Get时，使用response.body().string()，如果response body很大，避免使用string(), 因为会把整个body加载到内存中，
这时候，我们更应该使用stream来处理response body。使用header(name, value) 会进行覆盖，使用addHeader(name, value) 不会覆盖。
使用header(name) 会返回最后一个value，如果没有，则返回null。如果想要返回列表，那么使用headers(name);

<br>
&emsp;&emsp; 使用Call.cancel() 来立刻终止一个运行中的call。可以设置各种timeout，如连接超时，读取超时，写入超时。
所有的HTTP 客户端配置都在 OkHttpClient中存活，包括代理设置，超时，缓存。如果想改变单个Call的配置，可以使用OkHttpClient.newBuilder()。
OkHttp会自动重试未校验的请求，当请求是401时，Authenticator会提供credentials，如果未提供，那么就会跳过重试，返回null。

<h2 id="4">4.Interceptors</h2>
&emsp;&emsp; OkHttp具有Application Interceptor 和 Network Interceptor。先执行Application Interceptor 然后执行Network Interceptor。
ApplicationInterceptor 不需要担心中间返回，例如：重定向、重试。总是会执行一次，即使从缓存中返回。
可以用ApplicationInterceptor来对RequestBody来进行压缩。

<h2 id="5">5.HTTPS</h2>
&emsp;&emsp; OkHttp 包含四种安全连接策略：RESTRICTED_TLS 是一种安全配置，满足严格的安全需求。 MODERN_TLS 是一种安全配置，连接到modern HTTPS server。
COMPATIBLE_TLS 是一种安全配置，连接到secure but not current HTTPS server。 CLEARTEXT 不安全配置，used for http:// URLS。

<h2 id="6">6.EventListener</h2>
&emsp;&emsp; event允许在HTTP请求过程中捕获各种状态。Event可以监控HTTP calls大小和频率，可以监控网络性能。成功的Event事件流程如下：
callStart -> (dns -> connectStart -> (secureConnect ->) connectEnd) connectionAcquired -> requestHeaders -> (requestBody ->)
responseHeaders -> responseBody -> connectionReleased -> callEnd。
失败的Event事件流程如下：
callStart -> (dns -> connectStart -> (secureConnect ->) -> [connectFailed -> callFailed] connectEnd ->) connectionAcquire
-> requestHeaders -> [(requestBody ->) responseHeaders -> responseBody -> connectionReleased -> callFailed -> callEnd] 

<h2 id="7">7.Summary</h2>
&emsp;&emsp; 通过查看源码