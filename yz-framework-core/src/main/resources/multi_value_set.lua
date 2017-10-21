local n = table.getn(KEYS)
for i=1,n do
  local regxEverythingExceptComma = '([^|]+)'
  local kv = {}
  for x in string.gmatch(KEYS[i], regxEverythingExceptComma) do
    table.insert(kv,x)
  end
  local nx_xx = ARGV[2]
  if nx_xx then
    if nx_xx == 'NX' then
      redis.call('SET',kv[1],kv[2],'EX',ARGV[1], 'NX')
    else
      redis.call('SET',kv[1],kv[2],'EX',ARGV[1], 'XX')
    end
  else
    redis.call('SET',kv[1],kv[2],'EX',ARGV[1])
  end
end
return 'OK'