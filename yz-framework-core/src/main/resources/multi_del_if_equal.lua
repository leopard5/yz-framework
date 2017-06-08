local n = table.getn(KEYS)
for i=1,n do
  if redis.call('GET',KEYS[i])== ARGV[1] then
    redis.call('DEL',KEYS[i])
  end
end
return 'OK'

