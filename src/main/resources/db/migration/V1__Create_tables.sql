-- Создание таблицы person
CREATE TABLE IF NOT EXISTS person (
    id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    avatar bytea
);

-- Создание таблицы posts
CREATE TABLE IF NOT EXISTS posts (
    post_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INTEGER NOT NULL,
    post_text TEXT,
    post_image BYTEA,

    -- Ограничение внешнего ключа: user_id ссылается на id из таблицы person
    CONSTRAINT posts_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES person (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

-- Создание таблицы friendship
CREATE TABLE IF NOT EXISTS friendship
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    user_id integer,
    friend_id integer,
    status character varying COLLATE pg_catalog."default",
    created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT friendship_pkey PRIMARY KEY (id),
    CONSTRAINT unique_friendship UNIQUE (user_id, friend_id),
    CONSTRAINT fk_friend FOREIGN KEY (friend_id)
        REFERENCES public.person (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES public.person (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts (user_id);