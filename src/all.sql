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

#sql("candles")
select
    ceil
    , substr(min(z.open_time2), 32) open_time
    , substr(min(z.open2), 32) open
    , max(high) high
    , min(low) low
    , substr(max(z.close2), 32) close
    , datetime(substr(min(z.open_time2), 32)/1000, 'unixepoch', '+9 hours') open_time2
    from (
        select y.*
        , GBV||','||y.open_time open_time2
        , GBV||','||y.open open2
        , GBV||','||y.close close2
        from (
            select x.rowNum
            , printf("%030.20f", groupby) GBV
            , round(x.groupby + 0.5) - CASE WHEN x.groupby <= -0.5 OR round(x.groupby + 0.5) - x.groupby != 1.0 THEN 0.0 ELSE 1.0 END [ceil]
            , x.OPEN_TIME, x.OPEN, x.HIGH, x.LOW, x.CLOSE, x.CLOSE_TIME
            from (
                select t.rowNum
                , t.rowNum/? groupby
                , t.OPEN_TIME, t.OPEN, t.HIGH, t.LOW, t.CLOSE, t.CLOSE_TIME
                from (
                    select
                    --(SELECT COUNT(*) FROM xrp_jpy AS t2 WHERE t2.OPEN_TIME <= t1.OPEN_TIME) AS rowNum,
                    ((OPEN_TIME - ?)/60000)+1 AS rowNum
                    , OPEN_TIME, OPEN, HIGH, LOW, CLOSE, CLOSE_TIME
                    --,datetime(OPEN_TIME/1000, 'unixepoch', '+9 hours') open_time2
                    from xrp_jpy t1
                    where t1.OPEN_TIME >= ?
                    order by t1.OPEN_TIME asc
                ) t
            ) x
        ) y
    ) z group by z.ceil order by z.ceil asc
#end