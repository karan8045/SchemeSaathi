package com.karan.schemesaathi.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karan.schemesaathi.data.model.EducationLevel
import com.karan.schemesaathi.data.model.EmploymentStatus
import com.karan.schemesaathi.data.model.IncomeRange
import com.karan.schemesaathi.data.model.LocationType
import com.karan.schemesaathi.data.model.SocialCategory
import com.karan.schemesaathi.data.seed.DemoPersonas

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isOnboardingComplete) {
        onOnboardingSuccess()
    }

    val statesOfIndia = listOf(
        "Andhra Pradesh", "Assam", "Bihar", "Delhi", "Gujarat", "Haryana",
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Odisha",
        "Punjab", "Rajasthan", "Tamil Nadu", "Telangana", "Uttar Pradesh", "West Bengal"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Your Profile (Step ${state.currentStep} of ${state.totalSteps})") },
                navigationIcon = {
                    if (state.currentStep > 1) {
                        IconButton(onClick = { viewModel.previousStep() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            LinearProgressIndicator(
                progress = { state.currentStep.toFloat() / state.totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation & Disclaimer Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Why profile data is needed: Your age, location, income, and occupation rules are used to calculate accurate eligibility for government schemes and jobs. Data is stored securely in Supabase.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Demo Persona Selector
            Text(
                text = "Quick Demo Profiles (Tap to auto-fill):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DemoPersonas.allPersonas.forEach { persona ->
                    Surface(
                        onClick = { viewModel.applyDemoPersona(persona) },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${persona.fullName} (${persona.occupation})",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STEP CONTROLLER
            when (state.currentStep) {
                1 -> { // Basic Info
                    Text("Step 1: Personal Identification", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.profile.fullName,
                        onValueChange = { name -> viewModel.updateProfile { it.copy(fullName = name) } },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.profile.dateOfBirth,
                        onValueChange = { dob -> viewModel.updateProfile { it.copy(dateOfBirth = dob) } },
                        label = { Text("Date of Birth (YYYY-MM-DD) *") },
                        placeholder = { Text("e.g. 2004-05-15") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    state.calculatedAge?.let { age ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Calculated Age: $age years old",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Gender:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("Female", "Male", "Other").forEach { g ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { viewModel.updateProfile { it.copy(gender = g) } }
                                    .padding(end = 16.dp, top = 6.dp, bottom = 6.dp)
                            ) {
                                RadioButton(
                                    selected = state.profile.gender == g,
                                    onClick = { viewModel.updateProfile { it.copy(gender = g) } }
                                )
                                Text(text = g, fontSize = 14.sp)
                            }
                        }
                    }
                }

                2 -> { // Location & Occupation
                    Text("Step 2: Location & Occupation", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    var stateExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = stateExpanded,
                        onExpandedChange = { stateExpanded = !stateExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.profile.state,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("State *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = stateExpanded,
                            onDismissRequest = { stateExpanded = false }
                        ) {
                            statesOfIndia.forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st) },
                                    onClick = {
                                        viewModel.updateProfile { it.copy(state = st) }
                                        stateExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.profile.district,
                        onValueChange = { d -> viewModel.updateProfile { it.copy(district = d) } },
                        label = { Text("District") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Location Area Type:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LocationType.entries.forEach { loc ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { viewModel.updateProfile { it.copy(locationType = loc.name) } }
                                    .padding(end = 12.dp, top = 6.dp, bottom = 6.dp)
                            ) {
                                RadioButton(
                                    selected = state.profile.locationType == loc.name,
                                    onClick = { viewModel.updateProfile { it.copy(locationType = loc.name) } }
                                )
                                Text(text = loc.displayName, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.profile.occupation,
                        onValueChange = { occ -> viewModel.updateProfile { it.copy(occupation = occ) } },
                        label = { Text("Current Occupation") },
                        placeholder = { Text("e.g. Student, Farmer, Job Seeker") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                3 -> { // Education & Income
                    Text("Step 3: Education & Income Category", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    var eduExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = eduExpanded,
                        onExpandedChange = { eduExpanded = !eduExpanded }
                    ) {
                        OutlinedTextField(
                            value = EducationLevel.fromString(state.profile.educationLevel).displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Education Level") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = eduExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = eduExpanded,
                            onDismissRequest = { eduExpanded = false }
                        ) {
                            EducationLevel.entries.forEach { edu ->
                                DropdownMenuItem(
                                    text = { Text(edu.displayName) },
                                    onClick = {
                                        viewModel.updateProfile { it.copy(educationLevel = edu.name) }
                                        eduExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    var incExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = incExpanded,
                        onExpandedChange = { incExpanded = !incExpanded }
                    ) {
                        OutlinedTextField(
                            value = IncomeRange.fromString(state.profile.incomeRange).displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Annual Family Income Range") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = incExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = incExpanded,
                            onDismissRequest = { incExpanded = false }
                        ) {
                            IncomeRange.entries.forEach { inc ->
                                DropdownMenuItem(
                                    text = { Text(inc.displayName) },
                                    onClick = {
                                        viewModel.updateProfile { it.copy(incomeRange = inc.name) }
                                        incExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Social Category:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SocialCategory.entries.forEach { cat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { viewModel.updateProfile { it.copy(socialCategory = cat.name) } }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = state.profile.socialCategory == cat.name,
                                    onClick = { viewModel.updateProfile { it.copy(socialCategory = cat.name) } }
                                )
                                Text(text = cat.displayName, fontSize = 13.sp)
                            }
                        }
                    }
                }

                4 -> { // Special Categories & Confirmation
                    Text("Step 4: Special Status & Preferences", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.profile.studentStatus,
                            onCheckedChange = { checked -> viewModel.updateProfile { it.copy(studentStatus = checked) } }
                        )
                        Text("I am currently a Student")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.profile.farmerStatus,
                            onCheckedChange = { checked -> viewModel.updateProfile { it.copy(farmerStatus = checked) } }
                        )
                        Text("I am a Farmer / Landowner")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.profile.disabilityStatus,
                            onCheckedChange = { checked -> viewModel.updateProfile { it.copy(disabilityStatus = checked) } }
                        )
                        Text("Person with Disability (PwD)")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Disclaimer Agreement:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "By continuing, you agree that Scheme Saathi displays government scheme information for discovery purposes. Scheme Saathi is not a government agency and requires you to review all details on official portals before applying.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            AnimatedVisibility(visible = state.errorMessage != null) {
                state.errorMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.nextStep() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (state.currentStep == state.totalSteps) "Save Profile & Explore" else "Next Step",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (state.currentStep == state.totalSteps) Icons.Default.Check else Icons.Default.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
