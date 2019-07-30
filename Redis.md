## Redis Notes

*[1.Why I give up RedisTemplate?](#1)
*[2.Jedis](#2)
*[3.《Redis In Action》 notes](#3)
*[3.1 Redis Introduction](#4)

<h2 id="1">1.Why I give up RedisTemplate?</h2>

&emsp;&emsp; 开始准备使用Spring的RedisTemplate来减少重复代码。RedisTemplate是Spring做的模板之一，类似于RestTemplate用来简化使用，
减少重复代码，并且提供了一些额外的功能。主要提供了高水准的Redis交互抽象功能，提供了序列化、连接管理等来使得开发人员不再需要关注这些细节问题。
但是感觉日常使用并没有用到RedisTemplate提供的那么多功能，最常使用的Redis交互种类可能比较少，而且在RedisTemplate有对Jedis的强依赖。
例如：我是用最新的spring-data-redis:2.1.9.RELEASE 和最新的jedis:3.1.0，发现最新版spring-data-redis并不支持最新的jedis。
在spring-data-redis中会看到很多强依赖，比如: import redis.clients.util.SafeEncoder 而在最新的jedis中，我发现jedis文件目录是有变动的，
这就导致，spring-data-redis必须与jedis版本进行完美匹配，一旦不匹配会导致很多问题。两者如果同时使用，相互制约比较明显。因此我选择直接使用Jedis。

<h2 id="2">2.Jedis</h2>
&emsp;&emsp; 如果在多线程的环境下使用同样的Jedis实例，会出现很多问题。如果每个线程都创建一个实例，那么资源会造成很大的浪费，通常的做法就是使用Pool。
然后通过JedisPool.getResource()获取Jedis实例。如果在Pool中有空闲的实例，那么将会进行activation，如果activation失败，或者testOnBorrow为true，
并且validation失败，那么该实例将被销毁，下一个实例将进行同样的验证，直到又一个实例被返回，或者所有的实例都验证失败。

<br>
&emsp;&emsp; 如果Pool没有空闲的实例，那么会根据maxTotal、blockWhenExhausted、borrowMaxWaitMillis进行下一步操作。
如果Pool实例数量没有达到maxTotal，那么将会创建一个新的实例，然后继续进行activation和validation最终返回，如果validation失败，那么将抛出NoSuchElementException异常。
如果Pool is exhausted，那么接下来会block还是NosuchElementException将取决于blockWhenExhausted的值，如果blockWhenExhausted is true，
那么将会阻塞，阻塞时间为borrowMaxWaitMillis，如果阻塞期间有空闲实例产生，那么线程获取实例的顺序取决于请求的先后顺序。

<br>
&emsp;&emsp; 可以通过jedis.slaveof(host, port)，把当前jedis设置为目标地址的slave。从2.6开始slave只能读不能写。
通过jedis.slaveofNoOne() 清除slave设置，此时该jedis不是其他server的slave。

<br>
&emsp;&emsp; 我在JedisProxy中实现了Jedis Alternative机制，如果有设置master和alternatives，那么优先使用master，master无法使用的情况下，
会依次遍历，尝试切换到alternative，并且一旦切换成功，那么之前失败的server会根据尝试的顺序依次加入到队尾。这样下一次在继续寻找alternative时，
会把之前尝试过但失败的alternative放到最后再尝试。

<h2 id="3">3.《Redis In Action》 notes</h2>

<h3 id="3.1">3.1 Redis Introduction</h3>
&emsp;&emsp; Redis提供5中存储结构：STRING，可以是字符串、整数或者浮点数，可以对整个字符串或者字符串中的其中一部分执行操作，对整数和浮点数执行自增或者自减。
LIST，一个链表，链表上每个节点都包含了一个字符串，从链表的两端推入或者弹出元素，根据偏移量对链表进行修剪（trim），读取单个或者多个元素，根据值查找或者移除元素。
SET，包含字符串的无序收集器，并且被包含的每个字符串都是不同的，添加、获取、移除单个元素，检查元素是否存在于集合中，计算交集、并集、差集，从集合中随机获取元素。
HASH，包含键值对的无序散列表，添加、获取、移除单个键值对，获取所有键值对。
ZSET，字符串成员之间与浮点数分值之间的有序映射，元素的排列顺序由分值的大小决定，添加、获取、删除单个元素，根据分值范围或者成员来获取元素。

<br>
&emsp;&emsp; Redis 字符串命令：GET、SET、DEL。

<br>
&emsp;&emsp; Redis列表命令：LPUSH，将元素推入到列表的左端，RPUSH，将元素推入到列表的右端，LPOP，从列表左端弹出元素，RPOP，从列表右端弹出元素。
LINDEX，获取列表在给定位置上的一个元素，LRANGE，获取列表在给定范围上的所有元素。

<br>
&emsp;&emsp; Redis集合命令：SADD，将给定元素添加到集合，SMEMBERS，返回集合包含的所有元素，SISMEMBER，检查给定的元素是否存在与集合中。
SREM，如果给定的元素存在于集合中，那么移除该元素。SINTER、SUNION、SDIFF，交集、并集、差集计算。

<br>
&emsp;&emsp; Redis散列命令：HSET，在散列里关联给定的键值对，HGET，获取散列键的值，HGETALL，获取散列中包含的所有键值对，HDEL，删除散列中的这个键。

<br>
&emsp;&emsp; Redis有序集合命令：ZADD，将一个带有给定分值的成员添加到有序集合中，ZRANGE，根据元素在有序集合中的位置，获取多个元素，
ZRANGEBYSCORE，获取有序集合在给定分值范围内的所有元素，ZREM，如果给定成员存在于有序集合中，那么删除该成员。
