local currentChannelStock = redis.call('GET',KEYS[1])
local personRemainingcount = redis.call('GET',KEYS[2])
if currentChannelStock then
	local currentStockNum = tonumber(currentChannelStock)
	local increment = tonumber(ARGV[1])
	local perPersonLimit = tonumber(ARGV[2])
	local ttl = tonumber(ARGV[3])
	if personRemainingcount then
		if currentStockNum > 0 and (currentStockNum - increment) >= 0 then
			personRemainingcount = tonumber(personRemainingcount)
			if personRemainingcount > 0 and (personRemainingcount - increment) >= 0 then
				redis.call('INCRBY', KEYS[1], -(increment))
				redis.call('INCRBY', KEYS[2], -(increment))
				return 'OK'
 			end
			return 'LIMIT'
		end
		return 'NO'
	else
		if currentStockNum > 0 and (currentStockNum - increment) >= 0 then
			redis.call('INCRBY', KEYS[1], -(increment))
			redis.call('SET', KEYS[2], perPersonLimit - increment, 'EX', ttl, 'NX')
			return 'OK'
		end
		return 'NO'
	end
end
return "NOKEY"