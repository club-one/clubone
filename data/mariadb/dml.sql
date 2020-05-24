
use scheduling;

insert into EventStatus (status_id, status_code, status_description) values(1,'CNFRM',"Confirmed"),(1,'CNCL',"Cancelled"),(1,'SCHED',"Scheduled"),(1,'PNLTY',"Cancelled with penalty");

select * from EventStatus;
