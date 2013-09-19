SELECT res_id, grp_id, res_type_id, RTRIM(res_desc) as roomname, RTRIM(res_hdr) as title, capacity
FROM RedESoft.dbo.tbl_res WHERE grp_id IN ??
ORDER BY roomname
