
CREATE TABLE users (
                       id int8 NOT NULL,
                       login varchar(255) NULL,
                       "password" varchar(255) NULL,
                       CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE directories (
                             id int8 NOT NULL,
                             "path" varchar(255) NULL,
                             user_id int8 NULL,
                             CONSTRAINT directories_pkey PRIMARY KEY (id),
                             CONSTRAINT fkbgjnujqwquudtcbx2jrgh8rgk FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE dirtodir (
                          parent_id int8 NULL,
                          child_id int8 NOT NULL,
                          CONSTRAINT dirtodir_pkey PRIMARY KEY (child_id),
                          CONSTRAINT fkirdbqsajw35cdgsqxgb95rkaa FOREIGN KEY (child_id) REFERENCES directories(id),
                          CONSTRAINT fkm8xctryq6iqllixcxsslpxfly FOREIGN KEY (parent_id) REFERENCES directories(id)
);

CREATE TABLE files (
                       id int8 NOT NULL,
                       "name" varchar(255) NULL,
                       directory_id int8 NULL,
                       CONSTRAINT files_pkey PRIMARY KEY (id),
                       CONSTRAINT fkgo9v5vmtvaq34fyqi3ci3acka FOREIGN KEY (directory_id) REFERENCES directories(id)
);

CREATE TABLE root_directory (
                                directory_id int8 NULL,
                                user_id int8 NOT NULL,
                                CONSTRAINT root_directory_pkey PRIMARY KEY (user_id),
                                CONSTRAINT fk2snus80tynap1v9i6wipqu846 FOREIGN KEY (directory_id) REFERENCES directories(id),
                                CONSTRAINT fkpvmj75qcfoics3dns70rbyu2w FOREIGN KEY (user_id) REFERENCES users(id)
);
