package com.example.conversion.domain.model

/**
 * Represents a preview of how a file will be renamed.
 * Used to show users before/after comparison and detect potential issues.
 *
 * @property original The original FileItem being renamed
 * @property previewName The generated new filename (with extension if preserved)
 * @property hasConflict True if this rename would cause a conflict
 * @property conflictReason Description of why there's a conflict (null if no conflict)
 */
data class PreviewItem(
    val original: FileItem,
    val previewName: String,
    val hasConflict: Boolean = false,
    val conflictReason: String? = null
) {
    /**
     * Returns true if the preview name is different from the original name.
     */
    val isChanged: Boolean
        get() = original.name != previewName

    /**
     * Returns true if this preview item can be safely renamed.
     */
    val canRename: Boolean
        get() = !hasConflict && isChanged

    /**
     * Returns a user-friendly description of the rename operation.
     */
    val description: String
        get() = when {
            hasConflict -> "Cannot rename: $conflictReason"
            !isChanged -> "No change needed"
            else -> "${original.name} → $previewName"
        }

    companion object {
        /**
         * Creates a PreviewItem with a conflict.
         */
        fun withConflict(
            original: FileItem,
            previewName: String,
            reason: String
        ): PreviewItem {
            return PreviewItem(
                original = original,
                previewName = previewName,
                hasConflict = true,
                conflictReason = reason
            )
        }

        /**
         * Creates a PreviewItem without conflicts.
         */
        fun success(
            original: FileItem,
            previewName: String
        ): PreviewItem {
            return PreviewItem(
                original = original,
                previewName = previewName,
                hasConflict = false,
                conflictReason = null
            )
        }
    }
}

/**
 * Summary of preview results for a batch rename operation.
 *
 * @property totalFiles Total number of files in the batch
 * @property validRenames Number of files that can be safely renamed
 * @property conflicts Number of files with conflicts
 * @property unchanged Number of files that won't change
 */
data class PreviewSummary(
    val totalFiles: Int,
    val validRenames: Int,
    val conflicts: Int,
    val unchanged: Int
) {
    /**
     * Returns true if the batch can proceed without issues.
     */
    val canProceed: Boolean
        get() = conflicts == 0 && validRenames > 0

    /**
     * Returns a user-friendly summary message.
     */
    val message: String
        get() = when {
            conflicts > 0 -> "⚠️ $conflicts file(s) have conflicts"
            validRenames == 0 -> "ℹ️ No files will be renamed"
            else -> "✓ $validRenames file(s) ready to rename"
        }

    companion object {
        /**
         * Generates a summary from a list of preview items.
         */
        fun from(previewItems: List<PreviewItem>): PreviewSummary {
            return PreviewSummary(
                totalFiles = previewItems.size,
                validRenames = previewItems.count { it.canRename },
                conflicts = previewItems.count { it.hasConflict },
                unchanged = previewItems.count { !it.isChanged && !it.hasConflict }
            )
        }
    }
}
