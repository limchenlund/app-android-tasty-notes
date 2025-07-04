package com.lund.tastynotes.utils

import android.content.Context
import android.net.Uri

object ImageUtils {
    
    /**
     * Check if the image URI is from assets
     */
    fun isAssetImage(imageUri: String?): Boolean {
        return imageUri?.startsWith(Constants.ASSETS_IMAGE_PREFIX) == true
    }
    
    /**
     * Get the asset path from a full asset URI
     */
    fun getAssetPathFromUri(imageUri: String?): String? {
        return if (isAssetImage(imageUri)) {
            imageUri?.removePrefix(Constants.ASSETS_IMAGE_PREFIX)
        } else {
            null
        }
    }
    
    /**
     * Convert asset path to full URI
     */
    fun assetPathToUri(assetPath: String): String {
        return Constants.ASSETS_IMAGE_PREFIX + assetPath
    }
    
    /**
     * Check if image URI is valid (not empty and not null)
     */
    fun isValidImageUri(imageUri: String?): Boolean {
        return !imageUri.isNullOrBlank()
    }
} 