SELECT res_date.sched_id as sched_id,
RTRIM(sched.sched_desc) as reservationname,
RTRIM(res.res_desc) as roomname,
res_date.res_id as res_id,
res.grp_id as grp_id,
res.res_type_id as res_type_id,
RTRIM(res.res_hdr) as title,
res.capacity as capacity,
mtg_start_date_local as startdate,
mtg_end_date_local as enddate,
sched.num_attendees as num_attendees
FROM
RedESoft.dbo.tbl_sched_res_date as res_date,
RedESoft.dbo.tbl_res as res,
RedESoft.dbo.tbl_sched as sched 
WHERE 
res_date.res_id = res.res_id
AND res_date.sched_id = ? AND sched.sched_id = res_date.sched_id
AND sched.deleted_flag = 0
ORDER BY roomname