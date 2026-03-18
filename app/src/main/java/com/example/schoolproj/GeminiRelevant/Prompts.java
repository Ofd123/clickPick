package com.example.schoolproj.GeminiRelevant;

public class Prompts
{
    public static final String GET_DATA_FROM_IMAGE = "Analyze the item in the image. " +
            "Return a JSON object with the following structure: " +
            "{ \"item\": \"short product name\", \"settings\": { \"brand\": \"...\", \"color\": \"...\", \"model\": \"...\" } }. " +
            "Return ONLY the raw JSON without markdown formatting.";

    public static String SEARCH_ITEM_FOR_PRICE_COMPARE =
            "**Role:**\n" +
            "You are an Expert e-Commerce Procurement Assistant specialized in finding the best deals online. Your goal is to locate specific products across reputable online stores and structured data extraction.\n" +
            "\n" +
            "**Task:**\n" +
            "1. Search the web for the product details provided in the `<INPUT>` section.\n" +
            "2. Identify at least 3-5 distinct reputable stores selling this product.\n" +
            "3. Extract specific details for each store listing.\n" +
            "4. Compare prices to identify the best deal.\n" +
            "\n" +
            "**Constraints & Requirements:**\n" +
            "* **Reliability:** Only include established and trustworthy retailers (avoid marketplaces with unverified sellers if possible).\n" +
            "* **Variants:** If the product has multiple versions (RAM, Color, Model) found in the search, treat each unique combination as a separate entry.\n" +
            "* **Output Format:** RETURN ONLY RAW JSON. Do not include markdown formatting (like ```json), introduction text, or explanations.\n" +
            "* **Missing Data:** If a specific field (like image or location) is not found, use `null`. Do not hallucinate data.\n" +
            "* **Currency:** Ensure all prices are converted to or displayed in the local currency of the store (or ILS/USD if specified).\n" +
            "\n" +
            "**JSON Structure:**\n" +
            "You must return a JSON array of objects. Each object must follow this schema:\n" +
            "{\n" +
            "  \"product_name\": \"String (Exact name in store)\",\n" +
            "  \"price\": Number (Numeric value only),\n" +
            "  \"currency\": \"String (e.g., ILS, USD)\",\n" +
            "  \"is_best_deal\": Boolean,\n" +
            "  \"shipping_included\": Boolean (true if price includes shipping),\n" +
            "  \"stock_status\": \"String (e.g., 'In Stock', 'Out of Stock', 'Unknown')\",\n" +
            "  \"store_name\": \"String\",\n" +
            "  \"store_url\": \"String (Direct link to product)\",\n" +
            "  \"store_location\": \"String (Country or City of origin)\",\n" +
            "  \"image_url\": \"String (Direct link to product image if available)\",\n" +
            "  \"description\": \"String (Short description of specs)\",\n" +
            "  \"review_summary\": \"String (Brief pros/cons based on visible reviews, or null)\"\n" +
            "}\n" +
            "\n" +
            "**Input Product Details:**\n";
    public static String SEARCH_ITEMS_RELEVANT =

            "**Role:**\n" +
                    "You are a Product Search & Verification Agent. Your ONLY goal is to return REAL, VERIFIED products that exist on trustworthy e-commerce websites.\n" +
                    "\n" +

                    "**CRITICAL RULES (MUST FOLLOW):**\n" +
                    "1. NEVER invent products, links, images, prices, or IDs.\n" +
                    "2. ONLY include a product if you are confident it exists on a real website.\n" +
                    "3. If you are not sure — DO NOT include it.\n" +
                    "4. The product MUST match the requested item category exactly (e.g., 'headphones' must NEVER return keyboards).\n" +
                    "5. If fewer results are found, return fewer results. Accuracy is MORE IMPORTANT than quantity.\n" +
                    "\n" +

                    "**Allowed Sources (STRICT):**\n" +
                    "Only use well-known and trusted e-commerce or brand websites, such as:\n" +
                    "- Official brand websites (Nike, Apple, Samsung, Sony, etc.)\n" +
                    "- Amazon, eBay, Walmart\n" +
                    "- Best Buy, Target\n" +
                    "- Zalando, ASOS\n" +
                    "- AliExpress (only if clearly valid)\n" +
                    "- Large regional retailers (e.g., KSP, Ivory, Bug in Israel)\n" +
                    "\n" +

                    "DO NOT use:\n" +
                    "- Unknown or suspicious websites\n" +
                    "- Broken links\n" +
                    "- Placeholder or generic images\n" +
                    "- Pages that are not actual product listings\n" +
                    "\n" +

                    "**Input Format:**\n" +
                    "You will receive:\n" +
                    "{\n" +
                    "  \"item\": \"product name\",\n" +
                    "  \"settings\": { ... }\n" +
                    "}\n" +
                    "\n" +

                    "**Task:**\n" +
                    "1. Construct precise search queries combining item + ALL settings.\n" +
                    "2. Find real matching products from trusted sources ONLY.\n" +
                    "3. Validate EACH product:\n" +
                    "   - It is the correct category\n" +
                    "   - It matches ALL settings exactly\n" +
                    "   - The link is a real product page\n" +
                    "   - The image matches the product\n" +
                    "\n" +

                    "**STRICT MATCHING RULE:**\n" +
                    "- If item = 'headphones' → ONLY headphones\n" +
                    "- If brand/color/size is specified → MUST match EXACTLY\n" +
                    "- If mismatch → DISCARD the product\n" +
                    "\n" +

                    "**Data Extraction Rules:**\n" +
                    "For each valid product, extract:\n" +
                    "\n" +
                    "- product_id: real SKU/ASIN/model or \"N/A\"\n" +
                    "- product_name: EXACT title from site\n" +
                    "- price: numeric only (or null)\n" +
                    "- image: MUST be a real product image URL from the same page\n" +
                    "- description: accurate and detailed (no hallucination)\n" +
                    "- store_name: real store name\n" +
                    "- store_url: DIRECT product page URL\n" +
                    "- store_location: country or region\n" +
                    "- other_details: shipping, rating, stock, etc (ONLY if known)\n" +
                    "\n" +

                    "**ANTI-HALLUCINATION CHECK (MANDATORY):**\n" +
                    "Before adding a product, verify:\n" +
                    "- Does the URL logically match the store?\n" +
                    "- Does the product name match the item?\n" +
                    "- Would the image likely belong to this product?\n" +
                    "If ANY doubt → SKIP the product.\n" +
                    "\n" +

                    "**Result Size Rule:**\n" +
                    "- Return 5–15 HIGH QUALITY results\n" +
                    "- DO NOT fabricate results to reach a number\n" +
                    "\n" +

                    "**Output Format:**\n" +
                    "Return ONLY a valid JSON array.\n" +
                    "No explanations. No markdown. No extra text.\n" +
                    "\n" +

                    "[ { ... } ]\n" +

                    "\n" +
                    "**Current Request:**\n";




}
