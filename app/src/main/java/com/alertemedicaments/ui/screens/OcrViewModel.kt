package com.alertemedicaments.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alertemedicaments.data.api.ApiService
import com.alertemedicaments.data.models.Medication
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class OcrUiState(
    val extractedText: String = "",
    val matchedMedications: List<Medication> = emptyList(),
    val isProcessing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OcrViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrUiState())
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Common medication patterns in French prescriptions
    private val medicationPatterns = listOf(
        // Common medication names patterns
        Regex("\\b([A-Z][a-zéèêëàâäùûüôöîïç]+(?:\\s+[A-Z]?[a-zéèêëàâäùûüôöîïç]+)*\\s*\\d+\\s*(?:mg|g|ml|UI|µg))\\b", RegexOption.IGNORE_CASE),
        // All caps medication names
        Regex("\\b([A-Z]{3,}(?:\\s+[A-Z]+)*\\s*\\d+\\s*(?:mg|g|ml|UI|µg)?)\\b"),
        // Generic pattern with dosage
        Regex("\\b([A-Za-zéèêëàâäùûüôöîïç]{4,}\\s*\\d+(?:,\\d+)?\\s*(?:mg|g|ml|UI|µg|%))\\b", RegexOption.IGNORE_CASE)
    )

    fun processImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null, extractedText = "", matchedMedications = emptyList()) }

            try {
                // Load image
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap == null) {
                    _uiState.update { it.copy(isProcessing = false, error = "Impossible de charger l'image") }
                    return@launch
                }

                // Create InputImage for ML Kit
                val image = InputImage.fromBitmap(bitmap, 0)

                // Perform OCR
                val result = textRecognizer.process(image).await()
                val extractedText = result.text

                if (extractedText.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            extractedText = "",
                            error = "Aucun texte détecté dans l'image"
                        )
                    }
                    return@launch
                }

                _uiState.update { it.copy(extractedText = extractedText) }

                // Extract potential medication names
                val potentialMedications = extractMedicationNames(extractedText)

                // Search for each potential medication
                val matchedMedications = mutableListOf<Medication>()
                val searchedTerms = mutableSetOf<String>()

                for (medicationName in potentialMedications) {
                    val normalizedName = normalizeMedicationName(medicationName)
                    if (normalizedName.length < 3 || searchedTerms.contains(normalizedName)) {
                        continue
                    }
                    searchedTerms.add(normalizedName)

                    try {
                        val searchResult = apiService.searchMedications(normalizedName)
                        if (searchResult.medications.isNotEmpty()) {
                            // Add the first match if not already in list
                            val match = searchResult.medications.first()
                            if (matchedMedications.none { it.id == match.id }) {
                                matchedMedications.add(match)
                            }
                        }
                    } catch (e: Exception) {
                        // Continue with other medications
                    }

                    // Limit to 10 medications
                    if (matchedMedications.size >= 10) break
                }

                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        matchedMedications = matchedMedications
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        error = "Erreur lors de l'analyse: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun extractMedicationNames(text: String): List<String> {
        val medications = mutableSetOf<String>()

        // Apply all patterns
        for (pattern in medicationPatterns) {
            pattern.findAll(text).forEach { match ->
                medications.add(match.groupValues[1].trim())
            }
        }

        // Also look for lines that might be medication names
        text.lines().forEach { line ->
            val trimmed = line.trim()
            // Lines that start with capital and have reasonable length
            if (trimmed.length in 4..50 &&
                trimmed.first().isUpperCase() &&
                !trimmed.contains("Dr") &&
                !trimmed.contains("Patient") &&
                !trimmed.contains("Date") &&
                !trimmed.contains("Adresse") &&
                !trimmed.contains("Téléphone") &&
                !trimmed.contains("SECU") &&
                !trimmed.contains("Médecin")
            ) {
                // Check if it contains dosage indicators
                if (trimmed.contains(Regex("\\d+\\s*(mg|g|ml|UI|µg|%|cp|gél|sachet|comprimé)", RegexOption.IGNORE_CASE))) {
                    medications.add(trimmed.substringBefore(Regex("\\d").find(trimmed)?.value ?: "").trim())
                }
            }
        }

        return medications.toList()
    }

    private fun normalizeMedicationName(name: String): String {
        return name
            .replace(Regex("\\s+"), " ")
            .replace(Regex("\\d+\\s*(mg|g|ml|UI|µg|%)"), "")
            .replace(Regex("[^a-zA-ZéèêëàâäùûüôöîïçÉÈÊËÀÂÄÙÛÜÔÖÎÏÇ\\s]"), "")
            .trim()
            .take(50)
    }

    fun setError(error: String) {
        _uiState.update { it.copy(error = error, isProcessing = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        textRecognizer.close()
    }
}
