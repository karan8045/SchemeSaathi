-- Scheme Saathi Supabase SQL Migration
-- Run this in your Supabase SQL Editor or via Supabase CLI migrations.

create extension if not exists pgcrypto;

-- 1. Profiles Table
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

-- 2. Notification Preferences Table
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

-- 3. Telegram Connections Table
create table if not exists public.telegram_connections (
  user_id uuid primary key references auth.users(id) on delete cascade,
  telegram_chat_id text unique not null,
  telegram_username text,
  is_active boolean default true,
  connected_at timestamptz default now(),
  last_seen_at timestamptz
);

-- 4. Saved Items Table
create table if not exists public.saved_items (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  item_type text not null,
  item_id text not null,
  notes text,
  created_at timestamptz default now(),
  unique(user_id, item_type, item_id)
);

-- 5. Application Tracking Table
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

-- 6. Deadline Reminders Table
create table if not exists public.deadline_reminders (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references auth.users(id) on delete cascade,
  item_type text not null,
  item_id text not null,
  reminder_date date not null,
  is_enabled boolean default true,
  created_at timestamptz default now()
);

-- 7. User Documents Table
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

-- 8. AI Conversations Table
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

-- Indexes for performance
create index if not exists idx_saved_items_user_id on public.saved_items(user_id);
create index if not exists idx_app_tracking_user_id on public.application_tracking(user_id);
create index if not exists idx_deadline_reminders_user_id on public.deadline_reminders(user_id);
create index if not exists idx_user_documents_user_id on public.user_documents(user_id);

-- Enable Row Level Security (RLS)
alter table public.profiles enable row level security;
alter table public.notification_preferences enable row level security;
alter table public.telegram_connections enable row level security;
alter table public.saved_items enable row level security;
alter table public.application_tracking enable row level security;
alter table public.deadline_reminders enable row level security;
alter table public.user_documents enable row level security;
alter table public.ai_conversations enable row level security;

-- Row Level Security Policies
-- Profiles Policy
create policy "Users can manage their own profile" on public.profiles
  for all using (auth.uid() = id) with check (auth.uid() = id);

-- Notification Preferences Policy
create policy "Users can manage their own notification preferences" on public.notification_preferences
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- Telegram Connections Policy
create policy "Users can manage their own telegram connections" on public.telegram_connections
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- Saved Items Policy
create policy "Users can manage their own saved items" on public.saved_items
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- Application Tracking Policy
create policy "Users can manage their own application tracking" on public.application_tracking
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- Deadline Reminders Policy
create policy "Users can manage their own deadline reminders" on public.deadline_reminders
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- User Documents Policy
create policy "Users can manage their own document metadata" on public.user_documents
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- AI Conversations Policy
create policy "Users can manage their own AI conversations" on public.ai_conversations
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
