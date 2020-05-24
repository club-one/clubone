drop database if exists scheduling;

create database scheduling;

use scheduling;

create table EventStatus (
	status_id SMALLINT,
	status_code VARCHAR(5),
	status_description VARCHAR(100)
);

CREATE TABLE EventInstance(
	EventInstanceId INT NOT NULL primary key,
	EventSeriesId INT NOT NULL,
	ExternalCorrelationId VARCHAR(200),
	EventState INT NOT NULL,
	CancelledEventInstanceId int,
	SessionTypeId int,
	StartTimeMilliseconds BIGINT NOT NULL,
	EndTimeMilliseconds BIGINT NOT NULL,
	TimeZoneId VARCHAR(100),
	StartTime TIMESTAMP NULL,
	EndTime TIMESTAMP NULL,
	CreatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
	CreatedBy VARCHAR(50),
	ModifiedOn timestamp DEFAULT CURRENT_TIMESTAMP(),
	ModifiedBy VARCHAR(50)
);

CREATE SEQUENCE EventInstance_Sequence INCREMENT BY 1 START WITH 1 ;

CREATE TABLE EventSeries(
	EventSeriesId int NOT NULL,
	LegacyCrmTrainerId VARCHAR(200) NOT NULL,
	LocationId VARCHAR(30) NOT NULL,
	ExternalEventId VARCHAR(200) NULL,
	ReplacementForSeriesId INT NULL,
	ReplacemdBySeriesId INT NULL,
	CreatedOn TIMESTAMP  DEFAULT CURRENT_TIMESTAMP(),
	CreatedBy VARCHAR(50) NULL,
	ModifiedOn TIMESTAMP  DEFAULT CURRENT_TIMESTAMP(),
	ModifiedBy VARCHAR(50) NULL
	);

CREATE SEQUENCE EventSeries_Sequence INCREMENT BY 1 START WITH 1 ;
