--[[
    cosocket即coroutine+socket
    顺序执行,但它是非阻塞执行方式
    因为nginx core是非阻塞执行;
    redis中subscribe是阻塞方式,
    因此在nginx_lua平台中使用redis
    中sub特性无法保持阻塞连接状态;
    流程模型:http://www.cnblogs.com/foundwant/p/6382083.html
]]
local args = ngx.req.get_uri_args()
local ttype = args.type -- pub/sub

local function newRedis(timeout, ip, port, section)
    local red = redis.new()
    red:set_timeout(timeout)

    local ok, err = red:connect(ip, port)
    if not ok then
        nlog.dinfo("connect:" .. err)
    end

    red:select(section)

    return red
end

local red = newRedis(10000, "127.0.0.1", "6379", 0)
local bak = newRedis(10000, "127.0.0.1", "6379", 0)

local function subscribe(channel)
    local res, err = red:subscribe(channel)
    if not res then
        nlog.dinfo("subscribe error.")
        return nil, err
    end

    --这里以函数返回,不然sub会在这里断连失去可操作性
    --这就是提到的特殊之一
    local function read_func(do_read)
        if nil == do_read or true == do_read then
            res, err = red:read_reply()
            if not res then
                return nil, err
            end

            return res
        end

        red:unsubscribe(channel)
        red:set_keepalive(60000, 100)

        --连接回收
        bak:close()
        bak:set_keepalive(60000, 100)

        --断连后重启等待
        red = newRedis(10000, "127.0.0.1", "6379", 0)
        red:subscribe(channel)

        bak = newRedis(10000, "127.0.0.1", "6379", 0)
        return
    end

    return read_func
end

local subset = "subset" --set
local channel = "test" --list

consume = function(length)
    --若订阅者消息队列有残余,先消费,再订阅
    for i = 1, llength do
        local recv, err = red:lpop(channel) --头部开始消费
        nlog.dinfo("recv:" .. cjson.encode(recv))
    end
    redis_util.coroutine_count = 1
    coroutine.yield()
end

--订阅者
if "sub" == ttype then
    --向set集合增加"订阅者id"
    red:sadd(subset, channel)

    --为每个"订阅者id"建立list
    local llength = red:llen(channel)
    if 0 == llength then
        red:rpush(channel, "hello")
    else
        --若订阅者消息队列有残余,先消费,再订阅
        for i = 1, llength do
            local recv, err = red:lpop(channel) --头部开始消费
            nlog.dinfo("recv:" .. cjson.encode(recv))
        end
    end
    nlog.dinfo("run coroutine after...")

    --开始订阅
    local func, err = subscribe(channel)
    while true do
        local res, err = func() --res:["message","test","world"]
        if err then
            func(false)
        end
        --在redis的订阅模式中,
        --单例模式下只能使用固定几个命令[ (P)SUBSCRIBE,(P)UNSUBSCRIBE,QUIT,PING,... ],
        --无法使用其它命令,比如lpop, rpush等命令,
        --所以这里无法使用red:lpop()来执行出队删除操作,
        --只能另起一个客户端对象来进行删除操作;
        local oo, ooerr = bak:lpop(channel)
        nlog.dinfo("bak lpop:" .. cjson.encode(oo))
        nlog.dinfo("res:" .. cjson.encode(res))
        ngx.sleep(1)
    end
end

--发布者,测试用,实际调用是在业务层
if "pub" == ttype then
    --先发布,再追加队列
    --local subchannel, err = red:spop(subset)
    --nlog.dinfo("subchannel:" .. type(subchannel))
    --if "userdata" ~= type(subchannel) then
    for i = 1, 1000 do
        local str = "world_" .. i
        red:publish(channel, str)
        red:rpush(channel, str) --尾部追加
        ngx.sleep(0.1)
    end
    --end
end

--监听器,crontab定时运行
if "spy" == ttype then
    while true do
        red:publish(channel, "0")
        ngx.sleep(60)
    end
end

ok, err = red:set_keepalive(60000, 100)
if not ok then
    ngx.say("set_keepalive:", err)
end

ngx.print("rpush done.")
ngx.exit(200)