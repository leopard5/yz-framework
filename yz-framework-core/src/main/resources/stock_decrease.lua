local currentStock = redis.call('GET',KEYS[1])
if currentStock then
	local currentStockNum = tonumber(currentStock)
	local increment = tonumber(ARGV[1])
	if currentStockNum > 0 and (currentStockNum - increment) >= 0 then
		redis.call('INCRBY', KEYS[1], -(increment))
		return 'OK'
	end
	return 'NO'
end
return "NOKEY"