SELECT res_date.sched_id as sched_id,
res_date.res_id as res_id,
udf.udf_id as udf_id,
udf.string_value as udf_string
FROM
RedESoft.dbo.tbl_sched_res_date as res_date
INNER JOIN RedESoft.dbo.tbl_res as res ON res_date.res_id = res.res_id
INNER JOIN RedESoft.dbo.tbl_sched_udf_val as udf ON res_date.sched_id = udf.sched_id
WHERE
udf.sched_id = ?