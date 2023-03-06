---
--- Generated by Luanalysis
--- Created by 87517.
--- DateTime: 2023.03.06 下午 02:41
---
-- 1.参数列表
-- 1.1 优惠券Id
local voucherId = ARGV[1]
-- 1.2 用户Id
local userId = ARGV[2]

-- 2.数据key
-- 2.1 库存key
local stockKey = "seckill:stock:" .. voucherId
-- 2.2 订单key
local orderKey = "seckill:order:" .. voucherId

if (tonumber(redis.call('get', stockKey)) <= 0) then
    return 1
end

if (redis.call('sismember', orderKey, userId) == 1) then
    return 2
end

redis.call('incrby', stockKey, -1)
redis.call('sadd', orderKey, userId)
return 0