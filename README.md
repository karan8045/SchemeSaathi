# Scheme Saathi - Civic Welfare Discovery & Application Assistant

**Scheme Saathi** is a smooth, multilingual Android application designed for Indian citizens to discover government welfare schemes, scholarships, jobs, internships, apprenticeships, pensions, health insurance, farming grants, housing assistance, and skill development programs.

---

## 🌟 Key Features

1. **Deterministic Eligibility Engine:** Accurately evaluates minimum/maximum age (calculated live from Date of Birth), state/district residency, education level, income limits, gender, social category, farmer/student/disability status.
2. **Supabase Cloud Sync & Restoration:** User profiles, saved items, application tracking, deadline reminders, document metadata, and Telegram connection status are saved in Supabase Postgres and restored after reinstall upon signing in with Email OTP.
3. **Mandatory Onboarding:** Educates users on data privacy, storage, and non-government platform disclaimers before collecting necessary profile parameters.
4. **Ask Saathi AI (Gemini Integration):** Provides simple-language explanations, document checklists, and answers user questions in English, Hindi, and regional languages.
5. **Personalized Telegram Notifications:** Connects directly with Telegram Bot (`t.me/SchemeSaathiBot`) for instant alerts when matching opportunities or deadlines appear.
6. **Application Prefill Assistant:** Prefills application form fields from user profile data, highlights missing information, generates document checklists, and requires explicit user review before opening official government application portals.
7. **Demo Mode & Personas:** Native Demo Mode allows testing all features without requiring live Supabase/Gemini/Telegram keys, pre-seeded with 6 Demo Personas and 40+ verified opportunities.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose + Material 3 (Deep Blue & Saffron Orange theme)
- **Architecture:** MVVM + Clean Architecture + Flow
- **Dependency Injection:** Dagger Hilt
- **Local Database:** Room Database (Offline Cache)
- **Local Preferences:** Jetpack DataStore Preferences
- **Backend & Database:** Supabase Auth (Email OTP), Supabase Postgres, Supabase Row Level Security (RLS), Supabase Edge Functions
- **AI Integration:** Google Gemini API (via Supabase Edge Function)
- **Messaging:** Telegram Bot API
- **Background Tasks:** WorkManager

---

## 🔑 Environment Variables & Configuration

### Required Environment Variables

To run Scheme Saathi in full production mode, set the following environment variables on your server or in local configuration:

```env
# Client & Server
SUPABASE_URL="https://your-supabase-project.supabase.co"
SUPABASE_ANON_KEY="your-supabase-anon-key"

# Server-Side Only (Never embed in Android App)
SUPABASE_SERVICE_ROLE_KEY="your-supabase-service-role-key"
GEMINI_API_KEY="your-google-gemini-api-key"
TELEGRAM_BOT_TOKEN="your-telegram-bot-token"
TELEGRAM_WEBHOOK_SECRET="your-telegram-webhook-secret"
APP_ENV="production"
```

### TODO Markers in Codebase

- `SupabaseClientManager.kt`: Update `supabaseUrl` and `supabaseAnonKey` with your live Supabase project details.
- `supabase/functions/gemini-assistant/index.ts`: Set `GEMINI_API_KEY` environment variable in Supabase Secrets.
- `supabase/functions/telegram-webhook/index.ts`: Set `TELEGRAM_BOT_TOKEN` in Supabase Secrets.

---

## 🗄️ Supabase Postgres Setup & Migrations

1. Log into your [Supabase Dashboard](https://supabase.com).
2. Go to the **SQL Editor**.
3. Copy and run the migration script located at:
   `supabase/migrations/20261010000000_schema.sql`
4. This script creates the following user-owned tables with Row Level Security (RLS) enabled:
   - `profiles`
   - `notification_preferences`
   - `telegram_connections`
   - `saved_items`
   - `application_tracking`
   - `deadline_reminders`
   - `user_documents`
   - `ai_conversations`
5. All RLS policies restrict operations to `auth.uid() = id` or `auth.uid() = user_id`.

---

## 🤖 Telegram Bot & Edge Functions Setup

1. Create a bot using [@BotFather](https://t.me/BotFather) on Telegram and note the `TELEGRAM_BOT_TOKEN`.
2. Deploy Supabase Edge Functions using Supabase CLI:
   ```bash
   supabase functions deploy gemini-assistant
   supabase functions deploy telegram-webhook
   supabase functions deploy send-telegram-notification
   ```
3. Set secrets in Supabase CLI:
   ```bash
   supabase secrets set TELEGRAM_BOT_TOKEN="your_token"
   supabase secrets set GEMINI_API_KEY="your_key"
   ```
4. Set the Telegram Webhook:
   ```bash
   curl -F "url=https://<your-project-id>.supabase.co/functions/v1/telegram-webhook" \
     https://api.telegram.org/bot<YOUR_TELEGRAM_BOT_TOKEN>/setWebhook
   ```

---

## 📱 Demo Mode & Demo Personas Guide

When running without live backend credentials, Scheme Saathi automatically operates in **Demo Mode**:

1. **Sign In:** Type any email address and use OTP `123456`.
2. **Switch Demo Personas:** Open the **Profile** or **Onboarding** screen and tap any of the 6 Demo Personas:
   - **Kavya Sharma** (22yo Student, Maharashtra)
   - **Ramesh Patil** (55yo Farmer, Maharashtra)
   - **Lakshmi Devi** (65yo Elderly Widow, UP)
   - **Anand Kumar** (23yo Graduate Job Seeker, Karnataka)
   - **Suresh Verma** (28yo PwD Office Assistant, Delhi)
   - **Sunita Devi** (38yo Self Help Group Worker, Bihar)
3. **Simulate Opportunity & Telegram Alert:** Open the **Alerts** tab and tap **"Simulate New Opportunity"**. The app creates a custom opportunity, matches it against your active persona, and generates a simulated Telegram notification alert.

---

## 🛡️ Security, Fraud Warnings & Privacy

- **No Government Representation:** Scheme Saathi is an independent discovery platform and explicitly reminds users to verify details on official government portals (`*.gov.in`, `*.nic.in`).
- **No Sensitive Financial Data:** Scheme Saathi never asks for bank passwords, UPI PINs, Aadhaar OTPs, or recruitment fees.
- **Explicit Review Required:** The Application Prefill Assistant requires user review and explicit confirmation before navigating to external government websites.
- **Local Storage Default:** User document checklists and local metadata remain strictly on-device by default.

---

## 🧪 Running Tests

- **Unit Tests:**
  ```bash
  ./gradlew testDebugUnitTest
  ```
- **Android InstrumentedTests:**
  ```bash
  ./gradlew connectedCheck
  ```
