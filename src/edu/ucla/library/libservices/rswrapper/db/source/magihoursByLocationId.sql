SELECT OpenDateTime as startdate, CloseDateTime as enddate FROM MAGI.dbo.view_scheduler_latestschedule
WHERE 
(
(OpenDateTime >= ? AND OpenDateTime <= ?)
OR
(CloseDateTime >= ? AND CloseDateTime <= ?)
OR
(OpenDateTime <= ? AND CloseDateTime >= ?)
)
AND LocationID = ? AND OpenDateTime != CloseDateTime
ORDER BY OpenDateTime;

