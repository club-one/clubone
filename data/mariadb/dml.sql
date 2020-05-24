
use scheduling;

insert into EventStatus (status_id, status_code, status_description) values(1,'CNFRM',"Confirmed"),(1,'CNCL',"Cancelled Without Charges"),(1,'SCHED',"Scheduled / Planned"),(1,'PNLTY',"Cancelled with Charges");

select * from EventStatus;
