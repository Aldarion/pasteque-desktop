--    POS-tech is a point of sales software
--    Copyright (C) 2012 SARL SCOP Scil
--    http://trac.scil.coop/pos-tech
--
--    This file is part of POS-Tech
--
--    POS-tech is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    POS-tech is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with POS-tech. If not, see <http://www.gnu.org/licenses/>.

-- Database upgrade script for MYSQL

-- db v3 - v4

-- final script
CREATE TABLE SUBGROUPS (
    ID INTEGER NOT NULL AUTO_INCREMENT,
    COMPOSITION VARCHAR(255) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    IMAGE MEDIUMBLOB,
    PRIMARY KEY(ID),
    CONSTRAINT SUBGROUPS_FK_1 FOREIGN KEY (COMPOSITION) REFERENCES PRODUCTS(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SUBGROUPS_PROD (
    SUBGROUP INTEGER NOT NULL AUTO_INCREMENT,
    PRODUCT VARCHAR(255) NOT NULL,
    PRIMARY KEY (SUBGROUP, PRODUCT),
    CONSTRAINT SUBGROUPS_PROD_FK_1 FOREIGN KEY (SUBGROUP) REFERENCES SUBGROUPS(ID) ON DELETE CASCADE,
    CONSTRAINT SUBGROUPS_PROD_FK_2 FOREIGN KEY (PRODUCT) REFERENCES PRODUCTS(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO CATEGORIES(ID, NAME) VALUES ('0', 'Formules');
INSERT INTO CATEGORIES(ID, NAME) VALUES ('-1', 'Pré-paiement');

CREATE TABLE TARIFFAREAS (
    ID INTEGER NOT NULL AUTO_INCREMENT,
    NAME VARCHAR(255) NOT NULL,
    TARIFFORDER INTEGER DEFAULT 0,
    PRIMARY KEY(ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE UNIQUE INDEX TARIFFAREAS_NAME_INX ON TARIFFAREAS(NAME);

CREATE TABLE TARIFFAREAS_PROD (
    TARIFFID INTEGER NOT NULL AUTO_INCREMENT,
    PRODUCTID VARCHAR(255) NOT NULL,
    PRICESELL DOUBLE NOT NULL,
    PRIMARY KEY (TARIFFID, PRODUCTID),
    CONSTRAINT TARIFFAREAS_PROD_FK_1 FOREIGN KEY (TARIFFID) REFERENCES TARIFFAREAS(ID) ON DELETE CASCADE,
    CONSTRAINT TARIFFAREAS_PROD_FK_2 FOREIGN KEY (PRODUCTID) REFERENCES PRODUCTS(ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE TICKETS ADD COLUMN TARIFFAREA INTEGER DEFAULT NULL;
ALTER TABLE TICKETS ADD CONSTRAINT TICKETS_TARIFFAREA FOREIGN KEY (TARIFFAREA) REFERENCES TARIFFAREAS(ID);

ALTER TABLE CUSTOMERS ADD COLUMN PREPAID DOUBLE DEFAULT 0 NOT NULL;

UPDATE APPLICATIONS SET NAME = $APP_NAME{}, VERSION = 4 WHERE ID = $APP_ID{};
