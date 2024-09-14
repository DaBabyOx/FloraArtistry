create database if not exists FloraArtistry;
use FloraArtistry;

create table if not exists MsUser(
    UserID char(5) primary key check(UserID regexp '^US[0-9]{3}$'),
    UserName varchar(50) not null,
    UserEmail varchar(50) unique not null,
    UserPassword varchar(50) not null,
    UserAddress varchar(255) not null,
    UserPhonenumber varchar(20) not null,
    UserRole varchar(20) not null);

create table if not exists MsFlower(
    FlowerID char(5) primary key check(FlowerID regexp '^FL[0-9]{3}$'),
    FlowerName varchar(50) not null,
    FlowerType varchar(50) not null,
    FlowerPrice int not null);

create table if not exists MsCart(
    UserID char(5) not null,
    FlowerID char(5) not null,
    Quantity int not null,
    primary key(UserID, FlowerID),
    foreign key(UserID) references MsUser(UserID),
    foreign key(FlowerID) references MsFlower(FlowerID));

create table if not exists TransactionHeader(
    TransactionID char(5) primary key check(TransactionID regexp '^TR[0-9]{3}$'),
    UserID char(5) not null,
    foreign key (UserID) references MsUser(UserID));

create table if not exists TransactionDetail(
  TransactionID char(5) not null,
  FlowerID char(5) not null,
  Quantity int not null,
  primary key(TransactionID,FlowerID),
  foreign key (TransactionID) references TransactionHeader(TransactionID),
  foreign key (FlowerID) references MsFlower(FlowerID));

insert into MsUser(UserID, UserName, UserEmail, UserPassword, UserAddress, UserPhonenumber, UserRole) values('US001', 'Customer', 'customer@gmail.com', 'customer123','Jl. Bypass', '0812128', 'Customer');
insert into MsUser(UserID, UserName, UserEmail, UserPassword, UserAddress, UserPhonenumber, UserRole) values('US003', 'Admin', 'admin@gmail.com', 'admin123','Jl.Bypass2', '08121281231', 'Admin');

