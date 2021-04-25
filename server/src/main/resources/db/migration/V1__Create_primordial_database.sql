CREATE TABLE user_profile (
    id serial NOT NULL,
    login varchar(255) NULL,
    "password" varchar(255) NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE directory (
    id serial NOT NULL,
    "path" varchar(255) NULL,
    user_id int8 NULL,
    CONSTRAINT directories_pkey PRIMARY KEY (id),
    CONSTRAINT fkbgjnujqwquudtcbx2jrgh8rgk FOREIGN KEY (user_id) REFERENCES user_profile(id)
);

CREATE TABLE dirtodir (
    parent_id int8 NULL,
    child_id int8 NOT NULL,
    CONSTRAINT dirtodir_pkey PRIMARY KEY (child_id),
    CONSTRAINT fkirdbqsajw35cdgsqxgb95rkaa FOREIGN KEY (child_id) REFERENCES directory(id),
    CONSTRAINT fkm8xctryq6iqllixcxsslpxfly FOREIGN KEY (parent_id) REFERENCES directory(id)
);

CREATE TABLE file (
    id serial NOT NULL,
    "name" varchar(255) NULL,
    directory_id int8 NULL,
    CONSTRAINT files_pkey PRIMARY KEY (id),
    CONSTRAINT fkgo9v5vmtvaq34fyqi3ci3acka FOREIGN KEY (directory_id) REFERENCES directory(id)
);

CREATE TABLE root_directory (
    directory_id int8 NULL,
    user_id int8 NOT NULL,
    CONSTRAINT root_directory_pkey PRIMARY KEY (user_id),
    CONSTRAINT fk2snus80tynap1v9i6wipqu846 FOREIGN KEY (directory_id) REFERENCES directory(id),
    CONSTRAINT fkpvmj75qcfoics3dns70rbyu2w FOREIGN KEY (user_id) REFERENCES user_profile(id)
);
