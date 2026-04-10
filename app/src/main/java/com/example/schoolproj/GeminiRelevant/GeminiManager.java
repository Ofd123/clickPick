package com.example.schoolproj.GeminiRelevant;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.schoolproj.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.ImagePart;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.TextPart;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

/**
 * Singleton manager class for interacting with the Google Gemini AI model.
 * Handles text-only and text-with-image prompts, and provides response cleaning utilities.
 */
public class GeminiManager {
    private static GeminiManager instance;
    private GenerativeModel gemini;

    /**
     * Private constructor to initialize the GenerativeModel with the API key from BuildConfig.
     */
    private GeminiManager() {
        gemini = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.Gemini_API_Key);
    }

    /**
     * Returns the singleton instance of GeminiManager.
     * @return The singleton instance.
     */
    public static GeminiManager getInstance() {
        if (instance == null) {
            instance = new GeminiManager();
        }
        return instance;
    }

    /**
     * Sends a prompt with an associated photo to the Gemini model.
     * @param prompt The text prompt to send.
     * @param photo The bitmap image to include in the request.
     * @param callback The callback to handle the API response.
     */
    public void sendTextWithPhotoPrompt(String prompt, Bitmap photo, GeminiCallback callback) {
        List<Part> parts = new ArrayList<>();
        parts.add(new TextPart(prompt));
        parts.add(new ImagePart(photo));

        Content[] content = new Content[1];
        content[0] = new Content(parts);

        gemini.generateContent(content, new Continuation<GenerateContentResponse>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object result) {
                if (result instanceof Result.Failure) {
                    Log.i(TAG, "Error: " + ((Result.Failure) result).exception.getMessage());
                    callback.onFailure(((Result.Failure) result).exception);
                } else {
                    callback.onSuccess(cleanResponse(((GenerateContentResponse) result).getText()));
                }
            }
        });
    }

    /**
     * Sends a text-only prompt to the Gemini model.
     * @param prompt The text prompt to send.
     * @param callback The callback to handle the API response.
     */
    public void sendTextPrompt(String prompt, GeminiCallback callback) {
        gemini.generateContent(prompt, new Continuation<GenerateContentResponse>() {
            @NonNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object result) {
                if (result instanceof Result.Failure) {
                    Log.i(TAG, "Error: " + ((Result.Failure) result).exception.getMessage());
                    callback.onFailure(((Result.Failure) result).exception);
                } else {
                    callback.onSuccess(cleanResponse(((GenerateContentResponse) result).getText()));
                }
            }
        });
    }

    /**
     * Cleans the raw response from Gemini by removing Markdown code blocks (e.g., ```json ... ```).
     * @param response The raw response string from the model.
     * @return A trimmed string with Markdown delimiters removed, or the original string if no delimiters are found.
     */
    private String cleanResponse(String response) {
        if (response == null)
            return null;

        String cleaned = response.trim();

        if (cleaned.contains("```")) {
            int firstIdx = cleaned.indexOf("```");
            int lastIdx = cleaned.lastIndexOf("```");
            if (firstIdx != lastIdx) {
                String content = cleaned.substring(firstIdx + 3, lastIdx).trim();
                if (content.startsWith("json")) {
                    content = content.substring(4).trim();
                }
                return content;
            } else if (firstIdx != -1) {
                // Only one set of backticks, try to strip it if it's at the start
                if (cleaned.startsWith("```json")) {
                    return cleaned.substring(7).trim();
                } else if (cleaned.startsWith("```")) {
                    return cleaned.substring(3).trim();
                }
            }
        }

        return cleaned;
    }
}