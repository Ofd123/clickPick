package com.example.schoolproj.GeminiRelevant;

/**
 * Interface for Gemini API callbacks.
 * Provides methods to handle successful responses and failures.
 */
public interface GeminiCallback
{
    /**
     * Called when the Gemini API returns a successful response.
     * @param result The cleaned text result from the API.
     */
    public void onSuccess(String result);

    /**
     * Called when the Gemini API request fails.
     * @param error The exception or error that caused the failure.
     */
    public void onFailure(Throwable error);
}
