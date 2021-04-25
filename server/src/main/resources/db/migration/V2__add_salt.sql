ALTER TABLE public.user_profile ADD salt varchar(255) NOT NULL;
ALTER TABLE public.user_profile ALTER COLUMN login SET NOT NULL;
ALTER TABLE public.user_profile ALTER COLUMN "password" SET NOT NULL;