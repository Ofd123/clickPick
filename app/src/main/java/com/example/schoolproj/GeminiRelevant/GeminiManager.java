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


public class GeminiManager
{
    private static GeminiManager instance;
    private GenerativeModel gemini;

    private GeminiManager() {
        gemini = new GenerativeModel(
                "gemini-2.5-flash",
                BuildConfig.Gemini_API_Key
        );
    }

    public static GeminiManager getInstance() {
        if (instance == null) {
            instance = new GeminiManager();
        }
        return instance;
    }

    public void sendTextWithPhotoPrompt(String prompt, Bitmap photo, GeminiCallback callback) {
        List<Part> parts = new ArrayList<>();
        parts.add(new TextPart(prompt));
        parts.add(new ImagePart(photo));

        Content[] content = new Content[1];
        content[0] = new Content(parts);

        gemini.generateContent(content, new Continuation<GenerateContentResponse>() {
            @NonNull
            @Override
            public CoroutineContext getContext()
            {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NonNull Object result)
            {
                if (result instanceof Result.Failure)
                {
                    Log.i(TAG, "Error: " + ((Result.Failure) result).exception.getMessage());
                    callback.onFailure(((Result.Failure) result).exception);
                }
                else
                {
                    callback.onSuccess(((GenerateContentResponse) result).getText());
                }
            }
        });
    }
}


