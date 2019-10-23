INSERT INTO Room (roomId, numOfBedRooms, featureSummary, roomType, roomStatus, LASTMAINTAINANCEDATE)
VALUES
('R001',1,'Air Condition-TV-WiFi',0,0,'2019-10-11'),
('R002',2,'Air Condition-TV-WiFi',0,0,'2019-10-07'),
('R003',4,'Air Condition-TV-WiFi',0,0,'2019-10-17'),
('S001',6,'Air Condition-TV-WiFi',1,0,'2019-10-18'),
('S002',6,'Air Condition-TV-WiFi',1,0,'2019-10-05'),
('S003',6,'Air Condition-TV-WiFi',1,0,'2019-10-19');



INSERT INTO HiringRecord (roomId, customerId, rentDate, estimatedReturnDate)
VALUES
('R001','CUS001','2019-10-18','2019-10-20'),
('R001','CUS002','2019-10-14','2019-10-16'),
('S001','CUS003','2019-10-18','2019-10-20'),
('S001','CUS004','2019-10-05','2019-10-10');
