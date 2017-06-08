local n = table.getn(KEYS)
for i=1,n do
  if not redis.call('SET',KEYS[i],ARGV[1],'PX',ARGV[2],'NX') then
    for j = i-1,1,-1 do
      redis.call('DEL',KEYS[j])
    end
    return nil
  elseif i == n then
    return 'OK'
  end
end

