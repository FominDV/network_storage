ALTER TABLE public.users ADD salt varchar(255) NOT NULL;
ALTER TABLE public.users ALTER COLUMN login SET NOT NULL;
ALTER TABLE public.users ALTER COLUMN "password" SET NOT NULL;