// Supabase Edge Function: Gemini Assistant for Scheme Saathi
// Location: supabase/functions/gemini-assistant/index.ts

import { serve } from "https://deno.land/std@0.168.0/http/server.ts"

const GEMINI_API_KEY = Deno.env.get("GEMINI_API_KEY") || ""

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
    const { prompt, context, language = "en" } = await req.json()

    if (!prompt) {
      return new Response(JSON.stringify({ error: "Prompt is required" }), {
        status: 400,
        headers: { "Content-Type": "application/json" },
      })
    }

    const systemInstruction = `You are Saathi, an empathetic and knowledgeable AI assistant for Scheme Saathi, a civic welfare discovery app in India.
Your mission is to explain government welfare schemes, scholarships, jobs, pensions, farming grants, and apprenticeships in simple, clear language.
Rules:
1. Always respond in the user's requested language (${language}).
2. Keep answers concise, factual, structured, and friendly for first-time smartphone users.
3. Clearly state required documents, deadlines, and official steps when relevant.
4. Never invent deadlines, official application links, or guarantee eligibility.
5. Remind users to verify details on official portals before applying.`

    // Call Gemini API (using gemini-2.5-flash or gemini-pro endpoint)
    const geminiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${GEMINI_API_KEY}`

    const response = await fetch(geminiUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [
          {
            role: "user",
            parts: [
              { text: `${systemInstruction}\n\nContext: ${JSON.stringify(context || {})}\n\nUser Question: ${prompt}` }
            ]
          }
        ]
      })
    })

    const data = await response.json()
    const text = data?.candidates?.[0]?.content?.parts?.[0]?.text || "I am currently unable to generate an explanation. Please check official scheme portals."

    return new Response(
      JSON.stringify({ answer: text }),
      { headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" } }
    )
  } catch (error) {
    return new Response(
      JSON.stringify({ error: error.message }),
      { status: 500, headers: { "Content-Type": "application/json" } }
    )
  }
})
