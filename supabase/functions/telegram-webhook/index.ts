// Supabase Edge Function: Telegram Webhook for Linking Chat ID
// Location: supabase/functions/telegram-webhook/index.ts

import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const TELEGRAM_BOT_TOKEN = Deno.env.get("TELEGRAM_BOT_TOKEN") || ""
const SUPABASE_URL = Deno.env.get("SUPABASE_URL") || ""
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") || ""

const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY)

serve(async (req) => {
  try {
    const update = await req.json()
    const message = update?.message
    if (!message) return new Response("OK")

    const chatId = message.chat.id.toString()
    const text = message.text || ""
    const username = message.from?.username || ""

    // Format of start command: /start LINKING_CODE
    if (text.startsWith("/start")) {
      const parts = text.split(" ")
      const code = parts[1] ? parts[1].trim() : null

      if (code) {
        // Linking code is base64 encoded user_id or a temporary lookup code
        let userId = code
        try {
          userId = atob(code)
        } catch {
          // Assuming raw user_id if not base64
        }

        // Save telegram connection in Supabase Postgres
        const { error } = await supabase.from("telegram_connections").upsert({
          user_id: userId,
          telegram_chat_id: chatId,
          telegram_username: username,
          is_active: true,
          connected_at: new Date().toISOString()
        })

        const replyText = error
          ? `❌ Linking failed: ${error.message}`
          : `🎉 *Scheme Saathi Connected!*\n\nYou will now receive instant, personalized alerts for government schemes, scholarships, jobs, and deadline reminders directly in Telegram.`

        await fetch(`https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            chat_id: chatId,
            text: replyText,
            parse_mode: "Markdown"
          })
        })
      } else {
        await fetch(`https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            chat_id: chatId,
            text: `Welcome to Scheme Saathi Bot! Please connect from within the Scheme Saathi Android App Profile screen.`
          })
        })
      }
    }

    return new Response("OK")
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), { status: 500 })
  }
})
