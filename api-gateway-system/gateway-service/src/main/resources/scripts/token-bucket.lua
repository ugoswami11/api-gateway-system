local tokenKey = KEYS[1]
local lastRefillKey = KEYS[2]

local capacity = tonumber(ARGV[1]) or 10
local refillRate = tonumber(ARGV[2]) or 1
local currentTime = tonumber(ARGV[3]) or 0

--------------------------------------------------
-- Fetch values safely
--------------------------------------------------

local tokens = redis.call("GET", tokenKey)
local lastRefillTime = redis.call("GET", lastRefillKey)

--------------------------------------------------
-- Safe initialization (independent checks)
--------------------------------------------------

if not tokens then
    tokens = capacity
else
    tokens = tonumber(tokens) or capacity
end

if not lastRefillTime then
    lastRefillTime = currentTime
else
    lastRefillTime = tonumber(lastRefillTime) or currentTime
end

--------------------------------------------------
-- Refill Logic
--------------------------------------------------

local elapsedTime = math.floor(
    (currentTime - lastRefillTime) / 1000
)

if elapsedTime > 0 then
    local tokensToAdd = elapsedTime * refillRate

    tokens = math.min(
        capacity,
        tokens + tokensToAdd
    )

    lastRefillTime = currentTime
end

--------------------------------------------------
-- Allow / Reject
--------------------------------------------------

local allowed = 0

if tokens > 0 then
    tokens = tokens - 1
    allowed = 1
end

--------------------------------------------------
-- Save state
--------------------------------------------------

redis.call("SET", tokenKey, tokens)
redis.call("SET", lastRefillKey, lastRefillTime)

redis.call("EXPIRE", tokenKey, 3600)
redis.call("EXPIRE", lastRefillKey, 3600)

--------------------------------------------------
-- Return result
--------------------------------------------------

return {
    allowed,
    tokens
}