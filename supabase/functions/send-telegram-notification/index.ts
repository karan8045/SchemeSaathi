// Supabase Edge Function: Send Personalized Telegram Notification
// Location: supabase/functions/send-telegram-notification/index.ts

import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const TELEGRAM_BOT_TOKEN = Deno.env.get("TELEGRAM_BOT_TOKEN") || ""
const SUPABASE_URL = Deno.env.get("SUPABASE_URL") || ""
const SUPABASE_SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") || ""

const supabase = createClient(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY)

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", {
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
      },
    })
  }

  try {
    const { userId, opportunityTitle, category, reason, deadline, applicationUrl, source } = await req.json()

    // Fetch user's telegram connection
    const { data: conn } = await supabase
      ? await supabase.from("telegram_connections").select("telegram_chat_id").eq("user_id", userId).single()
      : { data: null }

    if (!conn?.telegram_chat_id) {
      return new Response(JSON.stringify({ error: "No active Telegram connection found for user" }), { status: 404 })
    }

    const message = `🔔 *New Opportunity Match!*

📌 *${opportunityTitle}*
📂 *Category:* ${category}
💡 *Why match:* ${reason}
📅 *Deadline:* ${deadline || "N/A"}
🏢 *Source:* ${source}

🔗 [Apply on Official Portal](${applicationUrl})

⚠️ *Important:* Scheme Saathi is not a government department. Always verify information on official portals before applying.`

    const tgRes = await fetch(`https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        chat_id: conn.telegram_chat_id,
        text: message,
        parse_mode: "Markdown",
        disable_web_page_preview: false
      })
    })

    const tgData = await tgRes.json()

    return new Response(JSON.stringify({ success: true, result: tgData }), {
      headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" }
    })
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), { status: 500 })
  }
})
