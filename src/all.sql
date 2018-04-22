#sql("findGirl")
select
'01min' type
,min(executed_at) from_ ,max(executed_at) to_
,ifnull(sum(case when side = 'buy' then price * amount else 0 end), 0) buy1
,ifnull(sum(case when side = 'buy' then 1 else 0 end), 0) buy2
,ifnull(sum(case when side = 'sell' then price * amount else 0 end), 0) sell1
,ifnull(sum(case when side = 'sell' then 1 else 0 end), 0) sell2
from transactions
where executed_at > strftime('%s','now','-1 minutes')*1000
union
select
'05min' type
,min(executed_at) from_ ,max(executed_at) to_
,ifnull(sum(case when side = 'buy' then price * amount else 0 end), 0) buy1
,ifnull(sum(case when side = 'buy' then 1 else 0 end), 0) buy2
,ifnull(sum(case when side = 'sell' then price * amount else 0 end), 0) sell1
,ifnull(sum(case when side = 'sell' then 1 else 0 end), 0) sell2
from transactions
where executed_at > strftime('%s','now','-5 minutes')*1000
union
select
'10min' type
,min(executed_at) from_ ,max(executed_at) to_
,ifnull(sum(case when side = 'buy' then price * amount else 0 end), 0) buy1
,ifnull(sum(case when side = 'buy' then 1 else 0 end), 0) buy2
,ifnull(sum(case when side = 'sell' then price * amount else 0 end), 0) sell1
,ifnull(sum(case when side = 'sell' then 1 else 0 end), 0) sell2
from transactions
where executed_at > strftime('%s','now','-10 minutes')*1000
union
select
'15min' type
,min(executed_at) from_ ,max(executed_at) to_
,ifnull(sum(case when side = 'buy' then price * amount else 0 end), 0) buy1
,ifnull(sum(case when side = 'buy' then 1 else 0 end), 0) buy2
,ifnull(sum(case when side = 'sell' then price * amount else 0 end), 0) sell1
,ifnull(sum(case when side = 'sell' then 1 else 0 end), 0) sell2
from transactions
where executed_at > strftime('%s','now','-15 minutes')*1000
union
select
'20min' type
,min(executed_at) from_ ,max(executed_at) to_
,ifnull(sum(case when side = 'buy' then price * amount else 0 end), 0) buy1
,ifnull(sum(case when side = 'buy' then 1 else 0 end), 0) buy2
,ifnull(sum(case when side = 'sell' then price * amount else 0 end), 0) sell1
,ifnull(sum(case when side = 'sell' then 1 else 0 end), 0) sell2
from transactions
where executed_at > strftime('%s','now','-20 minutes')*1000
union
select
'25min' type
,min(executed_at) from_ ,max(executed_at) to_
,ifnull(sum(case when side = 'buy' then price * amount else 0 end), 0) buy1
,ifnull(sum(case when side = 'buy' then 1 else 0 end), 0) buy2
,ifnull(sum(case when side = 'sell' then price * amount else 0 end), 0) sell1
,ifnull(sum(case when side = 'sell' then 1 else 0 end), 0) sell2
from transactions
where executed_at > strftime('%s','now','-25 minutes')*1000
union
select
'30min' type
,min(executed_at) from_ ,max(executed_at) to_
,ifnull(sum(case when side = 'buy' then price * amount else 0 end), 0) buy1
,ifnull(sum(case when side = 'buy' then 1 else 0 end), 0) buy2
,ifnull(sum(case when side = 'sell' then price * amount else 0 end), 0) sell1
,ifnull(sum(case when side = 'sell' then 1 else 0 end), 0) sell2
from transactions
where executed_at > strftime('%s','now','-30 minutes')*1000
#end