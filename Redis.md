## Redis Notes

* [1.Why I give up RedisTemplate?](#1)
* [2.Jedis](#2)
* [3.《Redis In Action》 notes](#3)
* [3.1 Redis Introduction](#3.1)
* [3.2 STRING](#3.2)
* [3.3 LIST](#3.3)
* [3.4 SET](#3.4)
* [3.5 HASH](#3.5)
* [3.6 SORTED SET](#3.6)
* [3.7 PUBLISH / SUBSCRIBE](#3.7)

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

<h3 id="3.2">3.2 STRING</h3>
&emsp;&emsp; 可以对存储着整数或者浮点数的字符串进行自增或者自减操作，整数的取整范围和系统的长整数取值范围相同。
如果对于可被解释成十进制整数或者浮点数，那么能进行各种自增自减操作，如果对一个不存在的键或者保存了空串的键，那么会把这个键的值当0处理，
如果值无法被解释为整数或浮点数，那么将返回一个错误。
自增自减命令：INCR，将键存储的值加1，DECR，将键存储的值减1，INCRBY，将键存储的值加上整数amount，DECRBY，将键存储的值减去整数amount，
INCRBYFLOAT，将键存储的值加上浮点数amount。

<br>
&emsp;&emsp; APPEND，将值追加到给定键的末尾。GETRANGE获取 >= start 到 <= end 范围内的所有字符组成的字串。SETRANGE，将从start偏移量开始的子串设置为给定值。
GETBIT，将字符串看作是二进制位串，并返回位串中偏移量为offset的二进制位的值。SETBIT，将字节串看作为二进制位串，并将位串中偏移量为offset的二进制位的值设置为value。
BITCOUNT，统计二进制位串中值为1的二进制位的数量，如果给定了可选的start偏移量和end偏移量，那么只对指定范围内的二进制位进行统计。
BITOP，对一个或者多个二进制位串执行并（AND）、或（OR）、亦或（XOR）、非（NOT）在内的任意一种按位运算操作，并将计算得出的结果保存在dest-key键里面。

<br>
&emsp;&emsp; 在使用SETRANGE 或者 SETBIT对字符串进行写入的时候，如果长度不满足写入要求，会自动使用空字节来扩展至所需的长度，然后执行写入或者更新操作。
在使用GETRANGE读取字符串时，超出字符串末尾的数据会被视为空串，使用GETBIT时，超出字符串末尾的二进制位会被视为0。

<h3 id="3.3">3.3 LIST</h3>
&emsp;&emsp; 常用的列表命令：RPUSH，将一个或多个值推入到列表的右端。LPUSH，将一个或者多个值推入到列表的左端。RPOP，移除并返回列表最右端的元素。
LPOP，移除并返回列表最左端的元素。LINDEX，返回偏移量为offset的元素。LRANGE，返回 >= start到 <= end偏移量范围内的所有元素。
LTRIM，修剪列表，只保留 >= start 到 <= end 偏移量范围内的元素。

<br>
&emsp;&emsp; 列表阻塞式命令：BLPOP，从第一个非空的列表中弹出最左侧的元素，或者在timeout秒内，阻塞并等待元素被弹出。
BRPOP，从第一个非空的列表中弹出位于最右侧的元素，或者在timeout秒之内阻塞并等待元素弹出。
RPOPLPUSH，从source_key列表中弹出最右端的元素，并将这个元素推入到dest_key列表最左端，并返回该元素。
BRPOPLPUSH，从source_key列表中弹出最右端的元素，然后将元素推入到dest_key列表最左端，如果source_key为空，那么阻塞timeout秒，等待元素弹出。

<h3 id="3.4">s.4 SET</h3>
&emsp;&emsp; 集合以无序的方式存储多个不相同的元素，常见命令：SADD，将一个或者多个元素添加到集合中，并返回被添加元素原本不存在于集合中的元素数量。
SREM，从集合中移除一个或多个元素，返回被移除元素的数量。SISMEMBER，检查元素item是否在集合中。SCARD，返回集合包含的元素数量。
SMEMBERS，返回集合包含的所有元素。
SRANDMEMBER，从集合中随机的返回一个或多个元素，当count为正，返回的随机元素不会重复，当count为负，返回的随机元素可能会重复。
SPOP，随机移除一个元素，并返回被移除的元素。SMOVE，如果集合source_key中包含元素item，那么从source_key中移除元素item，并将元素添加到dest_key中，成功移除返回1，否则返回0。

<br>
&emsp;&emsp; 用以组合和处理多个集合的命令：SDIFF，返回存在于第一个集合，但不存在于其他集合中的元素。
SDIFFSTORE，将存在于第一个集合但并不存在于其他集合的元素，存储到dest_key里面。SINTER，返回同时存在于所有集合的元素。
SINTERSTORE，将同时存在于所有集合的元素，存储到dest_key里面。SUNION，返回至少存在于一个集合中的元素。
SUNIONSTORE，将至少存在于一个集合中的元素，存储到dest_key中。

<h3 id="3.5">3.5 HASH</h3>
&emsp;&emsp; 常用的散列命令：HMGET，从散列中获取一个或多个键的值。HMSET，为散列里面的一个或多个键设置值。
HDEL，删除散列中的一个或多个键值对，返回成功找到并删除的键值对数量。HLEN，返回散列包含的键值对数量。

<br>
&emsp;&emsp; 散列高级特性：HEXISTS，检查给定的键是否存在于散列中，HKEYS，获取散列包含的所有键，HVALS，获取散列包含的所有值。
HGETALL，获取散列包含的所有的键值对，HINCRBY，将键存储的值加上整数increment，HINCRBYFLOAT，将键存储的值加上浮点数increment。

<h3 id="3.6">3.6 SORTED SET</h3>
&emsp;&emsp; 常用的有序集合命令：ZADD，将带有给定分值的成员添加到有序集合中，ZREM，从有序集合里面移除给定的成员，并返回被移除成员的数量。
ZCARD，返回有序集合包含的成员数量。ZINCRBY，将member成员的分值加上increment，ZCOUNT，返回分值介于min和max之间的成员数量。
ZRANK，返回成员member在有序集合中的排名。ZSCORE，返回成员member的分值，ZRANGE，返回有序集合中排名介于start和stop之间的成员，如果给定了可选的WITHSCORES，那么分值会一并返回。

<br>
&emsp;&emsp; 有序集合高级命令：ZREVRANK，返回有序集合成员member的排名，成员按照分值从到小排列。ZREVRANGE，返回有序集合给定排名范围内的成员，成员按照分值从大到小排列。
ZRANGEBYSCORE，返回有序集合中，介于min和max之间的所有成员。ZREVRANGEBYSCORE，获取有序集合中分值介于min和max之间的所有成员，并按照分值从大到小的顺序返回。
ZREMRANGEBYRANK，移除排名介于start和stop之间的所有成员。ZREMRANGEBYSCORE，移除有序集合中分值介于min和max之间的成员。
ZINTERSTORE，对于给定的有序集合执行类似集合的交集运算。ZUNIONSTORE，对给定的有序集合执行类似集合的并集运算。INTER分值取sum，UNION取min。

<h3 id="3.7">3.7 PUBLISH / SUBSCRIBE</h3>
&emsp;&emsp; 发布订阅命令：SUBSCRIBE，订阅给定的一个或多个命令。UNSUBSCRIBE，退订给定的一个或多个频道，如果执行时没有给定任何频道，那么退订所有的频道。
PUBLISH 向特定频道发送消息。PSUBSCRIBE 订阅与给定模式相匹配的所有频道。PUNSUBSCRIBE，退订给定的模式如果执行时没有给定给定任何模式，那么退订所有模式。