package com.skillvia.app.ui.skills

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillvia.app.data.model.Skill
import com.skillvia.app.data.model.SkillRequest
import com.skillvia.app.data.model.RequestStatus
import com.skillvia.app.data.repository.SkillRepo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) //using newer features of material 3
@Composable
fun AddSkillRequestScreen(
    skillId: String,
    onRequestSubmitted: () -> Unit,
    onBackClick: () -> Unit
) {
    // TODO: Implement skill request UI
    Text("Add Skill Request Screen - Coming Soon!")
}
