package com.example.schoolproj.GeminiRelevant;

public class Prompts
{
    public static final String GET_DATA_FROM_IMAGE = "Act as an **Expert Product Researcher and Visual Analyst**, capable of identifying and detailing any item (including footwear, electronics, furniture, machinery, or art).\n" +
        "\n" +
        "Analyze the central item in the attached image thoroughly. Provide the most in-depth report possible, covering technical specifications, history, and functional purpose.\n" +
        "\n" +
        "Please structure your report clearly using the following mandatory sections:\n" +
        "\n" +
        "1.  **Precise Identification:**\n" +
        "    * **Brand & Manufacturer:** (e.g., Nike, Sony, Herman Miller)\n" +
        "    * **Exact Model Name/Number:** (Provide the specific name and/or series number)\n" +
        "    * **Estimated Release Year/Era:** (When was this product introduced?)\n" +
        "\n" +
        "2.  **Aesthetics and Materials:**\n" +
        "    * **Primary Materials:** (e.g., specific plastics, type of leather, metal composition)\n" +
        "    * **Colorway:** (Specify the official color name if known, or a detailed description)\n" +
        "    * **Design Language:** (Is it minimalist, industrial, retro, etc.?)\n" +
        "\n" +
        "3.  **Functionality and Key Specifications:**\n" +
        "    * **Primary Use Case:** (What is its core purpose? e.g., Ultra-Marathon Running, Studio Monitoring, Gaming Peripheral)\n" +
        "    * **Unique Features/Technology:** (Patented technologies, sensor specs, processor/battery details, special mechanisms)\n" +
        "\n" +
        "4.  **Market Context and Value:**\n" +
        "    * **Rarity/Collectibility:** (Is this a rare item, a common one, or a collector's piece?)\n" +
        "    * **Estimated Market Value:** (Current retail price or typical resale/used price estimate)\n" +
        "\n" +
        "If any specific data point is uncertain, state your best educated guess based on the visual evidence provided.";
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
                    "You are an automated Product Search & Extraction Agent. Your goal is to deliver the longest, most comprehensive list possible of real-world purchase options for a specific item by performing exhaustive web searches and extracting rich, accurate details from dozens of diverse sources.\n" +
                    "\n" +
                    "**Input Format:**\n" +
                    "You will receive a JSON object containing:\n" +
                    "1.  `item`: The name of the product.\n" +
                    "2.  `settings`: A dictionary of attributes (e.g., brand, color, size, capacity, model year).\n" +
                    "\n" +
                    "**Task Instructions:**\n" +
                    "1.  **Analyze the Request:** Combine the `item` and every attribute in `settings` to create multiple highly precise search queries.\n" +
                    "2.  **EXTENSIVE Multi-Source Search:** Use your browsing, search, Google Shopping, and any available tools to run **dozens of searches** across global and regional platforms. Visit official brand sites, major marketplaces (Amazon, eBay, Walmart, Nike.com, Zappos, Foot Locker, Dick's Sporting Goods, ASOS, Zalando, AliExpress, etc.), price-comparison engines, and local/regional retailers. **Do not stop after 5–10 results.** Continue until you have 20–50+ unique matching products from completely different stores and countries. Prioritize maximum diversity in retailers, product variants/models, and geographic availability.\n" +
                    "3.  **Browse Product Pages:** For every potential listing, open the actual product page and extract fresh, current data (do not rely on search snippets alone).\n" +
                    "4.  **Extract Data:** For **every** valid match, pull the following fields exactly:\n" +
                    "    * **product_id:** Unique identifier (ASIN, SKU, model number, style code, or \"N/A\").\n" +
                    "    * **product_name:** Full, exact title as shown on the store page.\n" +
                    "    * **price:** Current selling price as a raw double number (e.g. 129.99 or 399.0). Use the final/sale price if available. Use null if price is not shown.\n" +
                    "    * **image:** Direct, full-resolution URL to the primary product image (not a thumbnail).\n" +
                    "    * **description:** The longest, most detailed product description available on the page (include materials, features, technology, fit notes, etc.—copy or summarize to be as verbose as possible while staying accurate).\n" +
                    "    * **store_name:** Exact retailer/brand name (e.g. \"Nike\", \"Amazon\", \"Foot Locker Israel\").\n" +
                    "    * **store_url:** Direct URL to the product listing page on that store's website.\n" +
                    "    * **store_location:** Primary base country/region or shipping origin (e.g. \"United States\", \"Israel\", \"United Kingdom\", \"Global Online\", \"Germany\").\n" +
                    "    * **other_details:** Any extra useful information—stock status, shipping cost/time to Israel, customer rating, review count, confirmed exact match to size/color/brand, available variants, warranty, etc. Make this field rich and long.\n" +
                    "5.  **Strict Filtering:** ONLY include products that fully match ALL `settings` values (Nike brand, Red color, 10 US size). If a listing is close but not exact, exclude it. Mention any near-matches briefly in other_details.\n" +
                    "6.  **Maximization Rules:** Return the absolute maximum number of unique results possible. There is no upper limit—include every qualifying product you discover. The final JSON must be very long and diverse.\n" +
                    "\n" +
                    "**Output Format:**\n" +
                    "Return **ONLY** a raw, valid JSON array of objects. NO markdown, NO ```json, NO explanations, NO extra text whatsoever—just the array. Price must be a JSON number (not string). The schema is:\n" +
                    "\n" +
                    "[\n" +
                    "  {\n" +
                    "    \"product_id\": \"String\",\n" +
                    "    \"product_name\": \"String\",\n" +
                    "    \"price\": Number (double or null),\n" +
                    "    \"image\": \"String (direct image URL)\",\n" +
                    "    \"description\": \"String (long detailed description)\",\n" +
                    "    \"store_name\": \"String\",\n" +
                    "    \"store_url\": \"String (direct product URL)\",\n" +
                    "    \"store_location\": \"String\",\n" +
                    "    \"other_details\": \"String (rich additional info)\"\n" +
                    "  }\n" +
                    "]\n" +
                    "\n" +
                    "**Example Interaction:**\n" +
                    "\n" +
                    "*User Input:*\n" +
                    "{\n" +
                    "  \"item\": \"Running Shoes\",\n" +
                    "  \"settings\": {\n" +
                    "    \"brand\": \"Nike\",\n" +
                    "    \"size\": \"10 US\",\n" +
                    "    \"color\": \"Red\"\n" +
                    "  }\n" +
                    "}\n" +
                    "\n" +
                    "*Your Output (example of expected long format—your real output must be much longer with 20+ entries):*\n" +
                    "[\n" +
                    "  {\n" +
                    "    \"product_id\": \"DD9295-600\",\n" +
                    "    \"product_name\": \"Nike Pegasus 41 Running Shoes - University Red/Black - Men's Size 10 US\",\n" +
                    "    \"price\": 129.99,\n" +
                    "    \"image\": \"https://static.nike.com/a/images/.../pegasus-41-red.jpg\",\n" +
                    "    \"description\": \"The Nike Pegasus 41 continues the legacy of one of the most trusted daily trainers. Engineered with ReactX foam for a responsive yet cushioned ride, breathable engineered mesh upper, and a durable rubber outsole with waffle traction. Perfect for daily training, tempo runs, and race-day performance. Weight: 10.2 oz. Stack height: 32mm/24mm. This exact pair is confirmed available in University Red colorway and US size 10.\",\n" +
                    "    \"store_name\": \"Nike Official Store\",\n" +
                    "    \"store_url\": \"https://www.nike.com/t/pegasus-41-mens-road-running-shoes-red-DD9295-600\",\n" +
                    "    \"store_location\": \"Global Online\",\n" +
                    "    \"other_details\": \"Currency: USD. In stock. Free shipping on orders over $100. Ships to Israel in 5-8 business days. 4.8/5 rating from 12,347 reviews. Exact size and color confirmed.\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"product_id\": \"B0C7X9K4L2\",\n" +
                    "    \"product_name\": \"Nike Air Zoom Structure 25 Running Shoes Red Men 10US\",\n" +
                    "    \"price\": 139.95,\n" +
                    "    \"image\": \"https://m.media-amazon.com/images/I/71kX...jpg\",\n" +
                    "    \"description\": \"Premium stability running shoe featuring Air Zoom units in the forefoot for explosive propulsion, full-length React foam midsole, and a supportive medial post. Ideal for overpronators needing motion control without sacrificing cushioning. Mesh upper with Flywire cables for lockdown fit. Official Nike colorway: University Red.\",\n" +
                    "    \"store_name\": \"Amazon.com\",\n" +
                    "    \"store_url\": \"https://www.amazon.com/dp/B0C7X9K4L2\",\n" +
                    "    \"store_location\": \"United States\",\n" +
                    "    \"other_details\": \"Currency: USD. Prime eligible. Ships to Israel (estimated 7-12 days, $18 shipping). 4.7/5 stars (2,891 reviews). Size 10 US confirmed in stock.\"\n" +
                    "  }\n" +
                    "]\n" +
                    "\n" +
                    "**Current Request:**\n";// +
                    //"{\"item\": \"Running Shoes\", \"settings\": {\"brand\": \"Nike\", \"size\": \"10 US\", \"color\": \"Red\"}}";
}

