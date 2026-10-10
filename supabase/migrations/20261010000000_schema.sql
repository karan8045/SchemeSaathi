-- Scheme Saathi Supabase Postgres Schema & Row Level Security Policies
-- Migration: 20261010000000_schema.sql

create extension if not exists pgcrypto;

-- 1. PROFILES TABLE
create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  full_name text not null,
  date_of_birth date not null,
  gender text,
  state text,
  district text,
  location_type text,
  occupation text,
  education_level text,
  skills text[] default '{}',
  employment_status text,
  income_range text,
  social_category text,
  disability_status boolean default false,
  student_status boolean default false,
  farmer_status boolean default false,
  preferred_job_types text[] default '{}',
  preferred_work_locations text[] default '{}',
  preferred_language text default 'en',
  interested_categories text[] default '{}',
  profile_complete boolean default false,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

-- 2. NOTIFICATION PREFERENCES TABLE
create table if not exists public.notification_preferences (
  user_id uuid primary key references auth.users(id) on delete cascade,
  scholarships boolean default true,
  jobs boolean default true,
  internships boolean default true,
  apprenticeships boolean default true,
  pensions boolean default true,
  health_insurance boolean default true,
  farming boolean default true,
  education boolean default true,
  housing boolean default true,
  skill_development boolean default true,
  self_employment boolean default true,
  women_and_child_support boolean default true,
  disability_support boolean default true,
  frequency text default 'instant',
  quiet_hours_start time,
  quiet_hours_end time,
  preferred_language text default 'en',
  enabled boolean default true,
  updated_at timestamptz default now()
);

-- 3. TELEGRAM CONNECTIONS TABLE
create table if not exists public.telegram_connections (
  user_id uuid primary key references auth.users(id) on delete cascade,
  telegram_chat_id text unique not null,
  telegram_username text,
  is_active boolean default true,
  connected_at timestamptz default now(),
  last_seen_at timestamptz
);

-- 4. SAVED ITEMS TABLE
create table if not exists public.saved_items (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  item_type text not null,
  item_id text not null,
  notes text,
  created_at timestamptz default now(),
  unique(user_id, item_type, item_id)
);

-- 5. APPLICATION TRACKING TABLE
create table if not exists public.application_tracking (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  item_type text not null,
  item_id text not null,
  status text default 'not_started',
  notes text,
  updated_at timestamptz default now(),
  unique(user_id, item_type, item_id)
);

-- 6. DEADLINE REMINDERS TABLE
create table if not exists public.deadline_reminders (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  item_type text not null,
  item_id text not null,
  reminder_date date not null,
  is_enabled boolean default true,
  created_at timestamptz default now()
);

-- 7. USER DOCUMENTS TABLE
create table if not exists public.user_documents (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  document_type text not null,
  document_name text,
  status text default 'missing',
  expires_on date,
  storage_path text,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

-- 8. AI CONVERSATIONS TABLE
create table if not exists public.ai_conversations (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  context_type text,
  context_id text,
  user_message text,
  assistant_message text,
  language text,
  created_at timestamptz default now()
);

-- ROW LEVEL SECURITY (RLS) POLICIES

-- Enable RLS on all user-owned tables
alter table public.profiles enable row level security;
alter table public.notification_preferences enable row level security;
alter table public.telegram_connections enable row level security;
alter table public.saved_items enable row level security;
alter table public.application_tracking enable row level security;
alter table public.deadline_reminders enable row level security;
alter table public.user_documents enable row level security;
alter table public.ai_conversations enable row level security;

-- PROFILES POLICIES
create policy "Users can view their own profile"
  on public.profiles for select
  using (auth.uid() = id);

create policy "Users can insert their own profile"
  on public.profiles for insert
  with check (auth.uid() = id);

create policy "Users can update their own profile"
  on public.profiles for update
  using (auth.uid() = id);

create policy "Users can delete their own profile"
  on public.profiles for delete
  using (auth.uid() = id);

-- NOTIFICATION PREFERENCES POLICIES
create policy "Users can view their notification preferences"
  on public.notification_preferences for select
  using (auth.uid() = user_id);

create policy "Users can insert their notification preferences"
  on public.notification_preferences for insert
  with check (auth.uid() = user_id);

create policy "Users can update their notification preferences"
  on public.notification_preferences for update
  using (auth.uid() = user_id);

create policy "Users can delete their notification preferences"
  on public.notification_preferences for delete
  using (auth.uid() = user_id);

-- TELEGRAM CONNECTIONS POLICIES
create policy "Users can view their telegram connection"
  on public.telegram_connections for select
  using (auth.uid() = user_id);

create policy "Users can insert their telegram connection"
  on public.telegram_connections for insert
  with check (auth.uid() = user_id);

create policy "Users can update their telegram connection"
  on public.telegram_connections for update
  using (auth.uid() = user_id);

create policy "Users can delete their telegram connection"
  on public.telegram_connections for delete
  using (auth.uid() = user_id);

-- SAVED ITEMS POLICIES
create policy "Users can manage their saved items"
  on public.saved_items for all
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

-- APPLICATION TRACKING POLICIES
create policy "Users can manage their application tracking"
  on public.application_tracking for all
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

-- DEADLINE REMINDERS POLICIES
create policy "Users can manage their deadline reminders"
  on public.deadline_reminders for all
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

-- USER DOCUMENTS POLICIES
create policy "Users can manage their documents"
  on public.user_documents for all
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

-- AI CONVERSATIONS POLICIES
create policy "Users can manage their AI conversations"
  on public.ai_conversations for all
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

-- AUTOMATIC UPDATED_AT TRIGGER
create or replace function public.handle_updated_at()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language plpgsql;

create trigger set_profiles_updated_at
  before update on public.profiles
  for each row execute function public.handle_updated_at();

create trigger set_notification_preferences_updated_at
  before update on public.notification_preferences
  for each row execute function public.handle_updated_at();

create trigger set_application_tracking_updated_at
  before update on public.application_tracking
  for each row execute function public.handle_updated_at();

create trigger set_user_documents_updated_at
  before update on public.user_documents
  for each row execute function public.handle_updated_at();
